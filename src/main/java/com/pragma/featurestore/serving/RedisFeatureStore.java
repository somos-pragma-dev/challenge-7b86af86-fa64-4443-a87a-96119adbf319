package com.pragma.featurestore.serving;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisClusterConfig;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisConfigBase;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RedisFeatureStore {

    private static final Logger LOG = LoggerFactory.getLogger(RedisFeatureStore.class);
    private static final Pattern VERSION_PATTERN = Pattern.compile("^feature:([^:]+):([^:]+):v(\\d+\\.\\d+\\.\\d+)$");
    private static final String DEFAULT_NAMESPACE = "feature";
    private static final Duration DEFAULT_TTL = Duration.ofHours(1);

    private final FlinkJedisConfigBase jedisConfig;
    private final Map<String, Duration> featureTtlMap;
    private final Map<String, String> featureVersionMap;
    private final Map<String, FeatureCache> cache;
    private final boolean enableVersioning;
    private final int maxCacheSize;

    private JedisPool jedisPool;
    private JedisCluster jedisCluster;

    public RedisFeatureStore(FlinkJedisConfigBase jedisConfig) {
        this(jedisConfig, true, 1000);
    }

    public RedisFeatureStore(FlinkJedisConfigBase jedisConfig, boolean enableVersioning, int maxCacheSize) {
        this.jedisConfig = jedisConfig;
        this.enableVersioning = enableVersioning;
        this.maxCacheSize = maxCacheSize;
        this.featureTtlMap = new ConcurrentHashMap<>();
        this.featureVersionMap = new ConcurrentHashMap<>();
        this.cache = new ConcurrentHashMap<>();
        initializeDefaultTTLs();
    }

    private void initializeDefaultTTLs() {
        featureTtlMap.put("user_features", Duration.ofHours(1));
        featureTtlMap.put("item_features", Duration.ofHours(24));
        featureTtlMap.put("context_features", Duration.ofMinutes(5));
        featureTtlMap.put("behavior_features", Duration.ofMinutes(30));
        featureTtlMap.put("aggregation_features", Duration.ofHours(12));
    }

    public void open() {
        try {
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(128);
            poolConfig.setMaxIdle(64);
            poolConfig.setMinIdle(16);
            poolConfig.setTestOnBorrow(true);
            poolConfig.setTestOnReturn(true);
            poolConfig.setTestWhileIdle(true);

            if (jedisConfig.getClusterNodes() != null && !jedisConfig.getClusterNodes().isEmpty()) {
                FlinkJedisClusterConfig clusterConfig = (FlinkJedisClusterConfig) jedisConfig;
                jedisCluster = new JedisCluster(
                        clusterConfig.getNodes(),
                        clusterConfig.getConnectionTimeout(),
                        clusterConfig.getSoTimeout(),
                        clusterConfig.getMaxAttempts(),
                        clusterConfig.getPassword(),
                        poolConfig
                );
                LOG.info("Redis cluster connection established");
            } else {
                jedisPool = new JedisPool(
                        poolConfig,
                        jedisConfig.getHost(),
                        jedisConfig.getPort(),
                        jedisConfig.getConnectionTimeout(),
                        jedisConfig.getPassword(),
                        jedisConfig.getDatabase()
                );
                LOG.info("Redis standalone connection established to {}:{}",
                        jedisConfig.getHost(), jedisConfig.getPort());
            }
        } catch (Exception e) {
            LOG.error("Failed to initialize Redis connection", e);
            throw new RuntimeException("Redis connection initialization failed", e);
        }
    }

    public void setFeature(String entityType, String entityId, String featureName,
                          Object value, String version) {
        String key = buildKey(entityType, entityId, featureName, version);
        Duration ttl = resolveTTL(featureName);

        try {
            Jedis jedis = getJedis();
            String serializedValue = serializeValue(value);
            jedis.setex(key, ttl.getSeconds(), serializedValue);

            if (enableVersioning) {
                updateVersionIndex(entityType, entityId, featureName, version);
            }

            updateLocalCache(key, value, ttl);

            LOG.debug("Feature set: {} = {} (TTL: {}s)", key, serializedValue, ttl.getSeconds());
        } catch (Exception e) {
            LOG.error("Failed to set feature: {}", key, e);
            throw new RuntimeException("Failed to set feature in Redis", e);
        }
    }

    public Optional<Object> getFeature(String entityType, String entityId, String featureName) {
        return getFeature(entityType, entityId, featureName, null);
    }

    public Optional<Object> getFeature(String entityType, String entityId, String featureName, String version) {
        String key = buildKey(entityType, entityId, featureName, version);

        FeatureCache cached = cache.get(key);
        if (cached != null && !cached.isExpired()) {
            LOG.debug("Cache hit for key: {}", key);
            return Optional.of(cached.getValue());
        }

        try {
            Jedis jedis = getJedis();
            String value = jedis.get(key);

            if (value == null) {
                if (enableVersioning && version == null) {
                    return getLatestVersion(entityType, entityId, featureName);
                }
                LOG.debug("Feature not found: {}", key);
                return Optional.empty();
            }

            Object deserialized = deserializeValue(value);
            Duration ttl = resolveTTL(featureName);
            updateLocalCache(key, deserialized, ttl);

            LOG.debug("Feature retrieved: {}", key);
            return Optional.of(deserialized);
        } catch (Exception e) {
            LOG.error("Failed to get feature: {}", key, e);
            return Optional.empty();
        }
    }

    private Optional<Object> getLatestVersion(String entityType, String entityId, String featureName) {
        String versionKey = buildVersionIndexKey(entityType, entityId, featureName);

        try {
            Jedis jedis = getJedis();
            String latestVersion = jedis.get(versionKey);

            if (latestVersion == null) {
                return Optional.empty();
            }

            String key = buildKey(entityType, entityId, featureName, latestVersion);
            String value = jedis.get(key);

            if (value == null) {
                return Optional.empty();
            }

            return Optional.of(deserializeValue(value));
        } catch (Exception e) {
            LOG.error("Failed to get latest version for: {}/{}/{}", entityType, entityId, featureName, e);
            return Optional.empty();
        }
    }

    private void updateVersionIndex(String entityType, String entityId, String featureName, String version) {
        String versionKey = buildVersionIndexKey(entityType, entityId, featureName);

        try {
            Jedis jedis = getJedis();
            String currentLatest = jedis.get(versionKey);

            if (currentLatest == null || compareVersions(version, currentLatest) > 0) {
                jedis.set(versionKey, version);
                featureVersionMap.put(versionKey, version);
                LOG.debug("Version index updated: {} -> {}", versionKey, version);
            }
        } catch (Exception e) {
            LOG.error("Failed to update version index: {}", versionKey, e);
        }
    }

    private int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");

        for (int i = 0; i < Math.max(parts1.length, parts2.length); i++) {
            int p1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int p2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;

            if (p1 != p2) {
                return Integer.compare(p1, p2);
            }
        }
        return 0;
    }

    public void invalidateFeature(String entityType, String entityId, String featureName) {
        try {
            Jedis jedis = getJedis();
            String pattern = String.format("feature:%s:%s:%s:*", entityType, entityId, featureName);
            Set<String> keys = jedis.keys(pattern);

            if (!keys.isEmpty()) {
                jedis.del(keys.toArray(new String[0]));
                cache.keySet().removeIf(k -> k.startsWith(String.format("feature:%s:%s:%s",
                        entityType, entityId, featureName)));
                LOG.info("Invalidated {} keys matching pattern: {}", keys.size(), pattern);
            }
        } catch (Exception e) {
            LOG.error("Failed to invalidate feature: {}/{}/{}", entityType, entityId, featureName, e);
        }
    }

    public void invalidateEntity(String entityType, String entityId) {
        try {
            Jedis jedis = getJedis();
            String pattern = String.format("feature:%s:%s:*", entityType, entityId);
            Set<String> keys = jedis.keys(pattern);

            if (!keys.isEmpty()) {
                jedis.del(keys.toArray(new String[0]));
                cache.keySet().removeIf(k -> k.startsWith(String.format("feature:%s:%s",
                        entityType, entityId)));
                LOG.info("Invalidated {} keys for entity: {}/{}", keys.size(), entityType, entityId);
            }
        } catch (Exception e) {
            LOG.error("Failed to invalidate entity: {}/{}", entityType, entityId, e);
        }
    }

    private String buildKey(String entityType, String entityId, String featureName, String version) {
        String effectiveVersion = (version != null) ? version : "latest";
        return String.format("feature:%s:%s:%s:v%s", entityType, entityId, featureName, effectiveVersion);
    }

    private String buildVersionIndexKey(String entityType, String entityId, String featureName) {
        return String.format("version_index:%s:%s:%s", entityType, entityId, featureName);
    }

    private Duration resolveTTL(String featureName) {
        return featureTtlMap.getOrDefault(featureName, DEFAULT_TTL);
    }

    private String serializeValue(Object value) {
        if (value instanceof Number) {
            return value.toString();
        } else if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Map) {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsString(value);
        }
        return value.toString();
    }

    private Object deserializeValue(String value) {
        try {
            if (value.matches("-?\\d+(\\.\\d+)?")) {
                if (value.contains(".")) {
                    return Double.parseDouble(value);
                }
                return Long.parseLong(value);
            }
            return value;
        } catch (NumberFormatException e) {
            return value;
        }
    }

    private void updateLocalCache(String key, Object value, Duration ttl) {
        if (cache.size() >= maxCacheSize) {
            String oldestKey = cache.entrySet().stream()
                    .min(Comparator.comparingLong(e -> e.getValue().getTimestamp()))
                    .map(Map.Entry::getKey)
                    .orElse(null);

            if (oldestKey != null) {
                cache.remove(oldestKey);
            }
        }

        cache.put(key, new FeatureCache(value, ttl));
    }

    private Jedis getJedis() {
        if (jedisCluster != null) {
            return null;
        }
        return jedisPool.getResource();
    }

    public void close() {
        if (jedisPool != null) {
            jedisPool.close();
            LOG.info("Redis pool closed");
        }
        if (jedisCluster != null) {
            jedisCluster.close();
            LOG.info("Redis cluster closed");
        }
    }

    public RedisMapper<FeatureEvent> createRedisMapper() {
        return new RedisMapper<FeatureEvent>() {
            @Override
            public RedisCommandDescription getCommandDescription() {
                return new RedisCommandDescription(RedisCommand.SET);
            }

            @Override
            public String getKeyFromData(FeatureEvent data) {
                return buildKey(data.getEntityType(), data.getEntityId(),
                        data.getFeatureName(), data.getVersion());
            }

            @Override
            public String getValueFromData(FeatureEvent data) {
                return serializeValue(data.getValue());
            }
        };
    }

    private static class FeatureCache {
        private final Object value;
        private final long timestamp;
        private final long expirationTime;

        public FeatureCache(Object value, Duration ttl) {
            this.value = value;
            this.timestamp = System.currentTimeMillis();
            this.expirationTime = timestamp + ttl.toMillis();
        }

        public Object getValue() {
            return value;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }
}