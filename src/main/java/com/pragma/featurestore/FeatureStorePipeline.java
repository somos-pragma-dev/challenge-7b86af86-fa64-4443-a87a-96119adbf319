package com.pragma.featurestore;

import com.pragma.featurestore.ingestion.KafkaSink;
import com.pragma.featurestore.processing.FeatureCalculator;
import com.pragma.featurestore.dto.FeatureEvent;
import com.pragma.featurestore.config.FlinkConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Properties;

public class FeatureStorePipeline {
    private static final Logger LOG = LoggerFactory.getLogger(FeatureStorePipeline.class);
    
    private static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String CDC_TOPIC = "postgres.public.user_events";
    private static final String FEATURES_TOPIC = "feature-store-features";
    private static final String CONSUMER_GROUP = "feature-store-consumer";
    private static final long CHECKPOINT_INTERVAL_MS = 60_000L;
    
    public static void main(String[] args) throws Exception {
        LOG.info("Iniciando Feature Store Pipeline para modelo de recomendación");
        
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        
        configureEnvironment(env);
        
        DataStream<String> rawCdcEvents = ingestFromPostgresCDC(env);
        
        DataStream<FeatureEvent> featureEvents = processCdcEvents(rawCdcEvents);
        
        writeToKafka(featureEvents);
        
        LOG.info("Pipeline configurado. Ejecutando job de Flink...");
        env.execute("Feature Store Pipeline - Real-time Feature Computation");
    }
    
    private static void configureEnvironment(StreamExecutionEnvironment env) {
        env.setParallelism(FlinkConfig.getDefaultParallelism());
        
        env.enableCheckpointing(CHECKPOINT_INTERVAL_MS);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(30_000);
        env.getCheckpointConfig().setCheckpointTimeout(300_000);
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        env.getCheckpointConfig().setTolerableCheckpointFailureNumber(3);
        
        env.getCheckpointConfig().setExternalizedCheckpointCleanup(
            org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION
        );
        
        env.getConfig().setAutoWatermarkInterval(5_000L);
        
        LOG.info("Entorno de Flink configurado con checkpointing cada {}ms", CHECKPOINT_INTERVAL_MS);
    }
    
    private static DataStream<String> ingestFromPostgresCDC(StreamExecutionEnvironment env) {
        LOG.info("Configurando fuente CDC desde PostgreSQL");
        
        Properties kafkaProps = new Properties();
        kafkaProps.setProperty("bootstrap.servers", KAFKA_BOOTSTRAP_SERVERS);
        kafkaProps.setProperty("group.id", CONSUMER_GROUP);
        kafkaProps.setProperty("auto.offset.reset", "earliest");
        kafkaProps.setProperty("enable.auto.commit", "false");
        kafkaProps.setProperty("isolation.level", "read_committed");
        
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
            .setBootstrapServers(KAFKA_BOOTSTRAP_SERVERS)
            .setTopics(CDC_TOPIC)
            .setGroupId(CONSUMER_GROUP)
            .setStartingOffsets(OffsetsInitializer.committedOffsets())
            .setValueOnlyDeserializer(new SimpleStringSchema())
            .setProperties(kafkaProps)
            .build();
        
        DataStream<String> cdcStream = env.fromSource(
            kafkaSource,
            WatermarkStrategy.noWatermarks(),
            "PostgreSQL CDC Source"
        );
        
        LOG.info("Fuente CDC configurada, topic: {}", CDC_TOPIC);
        return cdcStream;
    }
    
    private static DataStream<FeatureEvent> processCdcEvents(DataStream<String> rawEvents) {
        LOG.info("Procesando eventos CDC para computar features");
        
        return rawEvents
            .filter(event -> event != null && !event.isEmpty())
            .keyBy(event -> extractEntityKey(event))
            .process(new FeatureCalculator())
            .name("Feature Calculator")
            .uid("feature-calculator");
    }
    
    private static String extractEntityKey(String event) {
        try {
            if (event.contains("\"user_id\"")) {
                int start = event.indexOf("\"user_id\"");
                int colon = event.indexOf(":", start);
                int comma = event.indexOf(",", colon);
                if (comma == -1) comma = event.indexOf("}", colon);
                return "user:" + event.substring(colon + 1, comma).replace("\"", "").trim();
            }
            return "unknown";
        } catch (Exception e) {
            LOG.warn("Error extrayendo clave de entidad del evento: {}", e.getMessage());
            return "unknown";
        }
    }
    
    private static void writeToKafka(DataStream<FeatureEvent> featureEvents) {
        LOG.info("Configurando sink hacia Kafka, topic: {}", FEATURES_TOPIC);
        
        Properties producerProps = new Properties();
        producerProps.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        producerProps.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        producerProps.setProperty(ProducerConfig.RETRIES_CONFIG, "3");
        producerProps.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        producerProps.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
        producerProps.setProperty(ProducerConfig.LINGER_MS_CONFIG, "10");
        producerProps.setProperty(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");
        
        KafkaSink<FeatureEvent> kafkaSink = new KafkaSink<>(
            FEATURES_TOPIC,
            producerProps,
            event -> event.getEntityId(),
            event -> event.getFeatureName() + "-" + event.getVersion()
        );
        
        featureEvents.sinkTo(kafkaSink)
            .name("Kafka Feature Sink")
            .uid("kafka-feature-sink");
        
        LOG.info("Sink de Kafka configurado exitosamente");
    }
}