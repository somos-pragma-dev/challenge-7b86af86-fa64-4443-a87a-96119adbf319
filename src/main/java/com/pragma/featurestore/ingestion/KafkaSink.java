package com.pragma.featurestore.ingestion;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class KafkaSink<T> implements SinkFunction<T> {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaSink.class);
    
    private final String topic;
    private final Properties producerProperties;
    private final KeyExtractor<T> keyExtractor;
    private final TopicExtractor<T> topicExtractor;
    private final SerializationSchema<T> serializationSchema;
    
    private transient KafkaProducer<String, byte[]> kafkaProducer;
    private transient volatile boolean initialized = false;
    
    private long totalRecordsSent = 0L;
    private long totalSendErrors = 0L;
    
    public interface KeyExtractor<T> {
        String extractKey(T event);
    }
    
    public interface TopicExtractor<T> {
        String extractTopic(T event);
    }
    
    public KafkaSink(String topic, Properties producerProperties, 
                     KeyExtractor<T> keyExtractor, TopicExtractor<T> topicExtractor) {
        this(topic, producerProperties, keyExtractor, topicExtractor, null);
    }
    
    public KafkaSink(String topic, Properties producerProperties,
                     KeyExtractor<T> keyExtractor, TopicExtractor<T> topicExtractor,
                     SerializationSchema<T> serializationSchema) {
        this.topic = topic;
        this.producerProperties = producerProperties;
        this.keyExtractor = keyExtractor;
        this.topicExtractor = topicExtractor;
        this.serializationSchema = serializationSchema;
    }
    
    @Override
    public void invoke(T value, Context context) throws Exception {
        if (!initialized) {
            initialize();
        }
        
        try {
            String key = keyExtractor.extractKey(value);
            String targetTopic = topicExtractor != null ? topicExtractor.extractTopic(value) : topic;
            
            byte[] serializedValue;
            if (serializationSchema != null) {
                serializedValue = serializationSchema.serialize(value);
            } else {
                serializedValue = value.toString().getBytes();
            }
            
            long timestamp = context.timestamp();
            
            ProducerRecord<String, byte[]> record = new ProducerRecord<>(
                targetTopic,
                null,
                timestamp != -1 ? timestamp : System.currentTimeMillis(),
                key,
                serializedValue
            );
            
            Future future = kafkaProducer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    LOG.error("Error enviando a Kafka - Topic: {}, Key: {}, Error: {}",
                        targetTopic, key, exception.getMessage());
                    totalSendErrors++;
                } else {
                    LOG.debug("Evento enviado - Topic: {}, Partition: {}, Offset: {}, Key: {}",
                        metadata.topic(), metadata.partition(), metadata.offset(), key);
                }
            });
            
            totalRecordsSent++;
            
            if (totalRecordsSent % 1000 == 0) {
                LOG.info("KafkaSink stats - Total enviado: {}, Errores: {}", totalRecordsSent, totalSendErrors);
            }
            
        } catch (Exception e) {
            LOG.error("Error en invoke de KafkaSink: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    private synchronized void initialize() {
        if (initialized) {
            return;
        }
        
        Properties props = new Properties();
        props.putAll(producerProperties);
        
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        
        props.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
        props.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        props.setProperty(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "30000");
        props.setProperty(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, "120000");
        
        kafkaProducer = new KafkaProducer<>(props);
        initialized = true;
        
        LOG.info("KafkaProducer inicializado para topic: {}", topic);
    }
    
    public void flush() {
        if (kafkaProducer != null) {
            kafkaProducer.flush();
            LOG.debug("KafkaSink flush completado");
        }
    }
    
    public void close() {
        if (kafkaProducer != null) {
            try {
                kafkaProducer.flush(30, TimeUnit.SECONDS);
                kafkaProducer.close();
                LOG.info("KafkaSink cerrado - Total enviado: {}, Errores: {}", totalRecordsSent, totalSendErrors);
            } catch (Exception e) {
                LOG.error("Error cerrando KafkaSink: {}", e.getMessage());
            }
        }
    }
    
    public long getTotalRecordsSent() {
        return totalRecordsSent;
    }
    
    public long getTotalSendErrors() {
        return totalSendErrors;
    }
    
    private static class ByteArraySerializer implements org.apache.kafka.common.serialization.Serializer<byte[]> {
        @Override
        public byte[] serialize(String topic, byte[] data) {
            return data;
        }
        
        @Override
        public void close() {}
        
        @Override
        public void configure(java.util.Map<String, ?> configs, boolean isKey) {}
    }
}