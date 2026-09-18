package com.pragma.featurestore.ingestion;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PostgresCDCSource implements SourceFunction<String> {
    private static final Logger LOG = LoggerFactory.getLogger(PostgresCDCSource.class);
    
    private final String bootstrapServers;
    private final String topic;
    private final String groupId;
    private final Properties debeziumConfig;
    
    private transient KafkaConsumer<String, String> kafkaConsumer;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    
    private long lastProcessedTimestamp = 0L;
    private final Map<TopicPartition, Long> offsetMap = new HashMap<>();
    
    public PostgresCDCSource(String bootstrapServers, String topic, String groupId) {
        this.bootstrapServers = bootstrapServers;
        this.topic = topic;
        this.groupId = groupId;
        this.debeziumConfig = buildDebeziumConfig();
    }
    
    private Properties buildDebeziumConfig() {
        Properties config = new Properties();
        config.put("connector.class", "io.debezium.connector.postgresql.PostgresConnector");
        config.put("database.hostname", "localhost");
        config.put("database.port", "5432");
        config.put("database.user", "postgres");
        config.put("database.password", "postgres");
        config.put("database.dbname", "featurestore");
        config.put("database.server.name", "postgres");
        config.put("table.include.list", "public.user_events,public.product_events,public.transaction_events");
        config.put("plugin.name", "pgoutput");
        config.put("publication.name", "featurestore_publication");
        config.put("slot.name", "featurestore_slot");
        config.put("snapshot.mode", "initial");
        config.put("decimal.handling.mode", "double");
        config.put("time.precision.mode", "adaptive");
        config.put("schema.history.internal.kafka.bootstrap.servers", bootstrapServers);
        config.put("schema.history.internal.kafka.topic", "schema-changes.featurestore");
        config.put("transforms", "unwrap");
        config.put("transforms.unwrap.type", "io.debezium.transforms.ExtractNewRecordState");
        config.put("transforms.unwrap.drop.tombstones", "false");
        config.put("transforms.unwrap.delete.handling.mode", "rewrite");
        config.put("key.converter", "org.apache.kafka.connect.json.JsonConverter");
        config.put("value.converter", "org.apache.kafka.connect.json.JsonConverter");
        config.put("key.converter.schemas.enable", "false");
        config.put("value.converter.schemas.enable", "false");
        return config;
    }
    
    @Override
    public void run(SourceContext<String> ctx) throws Exception {
        isRunning.set(true);
        
        initializeConsumer();
        
        LOG.info("PostgreSQL CDC Source iniciado, escuchando topic: {}", topic);
        
        while (isRunning.get()) {
            try {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(1000));
                
                if (!records.isEmpty()) {
                    for (ConsumerRecord<String, String> record : records) {
                        if (isRunning.get()) {
                            String event = processCdcEvent(record);
                            if (event != null) {
                                ctx.collect(event);
                                updateOffset(record);
                            }
                        }
                    }
                    kafkaConsumer.commitSync();
                }
                
                emitWatermark(ctx);
                
            } catch (Exception e) {
                LOG.error("Error procesando eventos CDC: {}", e.getMessage(), e);
                handleFailure();
            }
        }
        
        LOG.info("PostgreSQL CDC Source detenido");
    }
    
    private void initializeConsumer() {
        Properties consumerProps = new Properties();
        consumerProps.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProps.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerProps.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        consumerProps.setProperty(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        consumerProps.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");
        consumerProps.setProperty(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "300000");
        consumerProps.setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        consumerProps.setProperty(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "10000");
        
        kafkaConsumer = new KafkaConsumer<>(consumerProps);
        kafkaConsumer.subscribe(Collections.singletonList(topic));
        
        LOG.info("Kafka consumer inicializado para CDC");
    }
    
    private String processCdcEvent(ConsumerRecord<String, String> record) {
        if (record.value() == null || record.value().isEmpty()) {
            return null;
        }
        
        String rawEvent = record.value();
        
        try {
            if (rawEvent.contains("\"__deleted\" : \"true\"")) {
                LOG.debug("Evento de borrado detectado, ignorando para feature computation");
                return null;
            }
            
            long eventTimestamp = extractTimestamp(rawEvent);
            if (eventTimestamp > lastProcessedTimestamp) {
                lastProcessedTimestamp = eventTimestamp;
            }
            
            return rawEvent;
            
        } catch (Exception e) {
            LOG.warn("Error procesando evento CDC: {}", e.getMessage());
            return null;
        }
    }
    
    private long extractTimestamp(String event) {
        try {
            if (event.contains("\"__ts_ms\"")) {
                int start = event.indexOf("\"__ts_ms\"");
                int colon = event.indexOf(":", start);
                int comma = event.indexOf(",", colon);
                if (comma == -1) comma = event.indexOf("}", colon);
                return Long.parseLong(event.substring(colon + 1, comma).trim());
            }
            return Instant.now().toEpochMilli();
        } catch (Exception e) {
            return Instant.now().toEpochMilli();
        }
    }
    
    private void updateOffset(ConsumerRecord<String, String> record) {
        TopicPartition tp = new TopicPartition(record.topic(), record.partition());
        offsetMap.put(tp, record.offset() + 1);
    }
    
    private void emitWatermark(SourceContext<String> ctx) {
        if (lastProcessedTimestamp > 0) {
            ctx.emitWatermark(new org.apache.flink.streaming.api.watermark.Watermark(lastProcessedTimestamp - 5000));
        }
    }
    
    private void handleFailure() {
        try {
            Thread.sleep(5000);
            LOG.info("Reintentando conexión CDC");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @Override
    public void cancel() {
        isRunning.set(false);
        if (kafkaConsumer != null) {
            kafkaConsumer.wakeup();
            LOG.info("CDC Source cancelado");
        }
    }
}