package com.pragma.featurestore.serving;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.operators.StreamMap;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.resps.Tuple;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class ABRouter extends ProcessFunction<ABRequest, ABRouter.ABResponse> {
    private static final Logger LOG = LoggerFactory.getLogger(ABRouter.class);
    private static final double CHALLENGER_PERCENTAGE = 0.1;
    private static final Duration STICKY_SESSION_DURATION = Duration.ofDays(7);
    private static final int HASH_MODULUS = 1000;

    private final Map<String, ModelVariant> modelVariants;
    private final Map<String, String> stickySessions;
    private final Map<String, AtomicLong> decisionCounters;
    private final Map<String, AtomicLong> stickyCounters;
    private final boolean enableStickySessions;
    private final boolean logDecisions;
    private final double challengerPercentage;
    private final JedisPool jedisPool;

    private transient Random random;
    private transient long sessionCleanupTimestamp;

    public ABRouter() {
        this(new HashMap<>(), true, false, 0.1, null);
    }

    public ABRouter(JedisPool jedisPool, double challengerPercentage) {
        this(new HashMap<>(), true, false, challengerPercentage, jedisPool);
    }

    public ABRouter(Map<String, ModelVariant> modelVariants, boolean enableStickySessions, 
                    boolean logDecisions, double challengerPercentage, JedisPool jedisPool) {
        this.modelVariants = modelVariants;
        this.enableStickySessions = enableStickySessions;
        this.logDecisions = logDecisions;
        this.challengerPercentage = challengerPercentage;
        this.jedisPool = jedisPool;
        this.stickySessions = new HashMap<>();
        this.decisionCounters = new HashMap<>();
        this.stickyCounters = new HashMap<>();
    }

    @Override
    public void open(Configuration parameters) {
        super.open(parameters);
        this.random = new Random();
        this.sessionCleanupTimestamp = System.currentTimeMillis();
    }

    @Override
    public void processElement(ABRequest request, Context ctx, Collector<ABResponse> out) {
        try {
            String variant = route(request.getUserId(), request.getExperimentId());
            out.collect(buildResponse(request, ModelVariant.valueOf(variant.toUpperCase())));
        } catch (Exception e) {
            LOG.error("Error routing request", e);
            out.collect(createFallbackResponse(request));
        }
    }

    public String route(String userId, String experimentId) {
        if (userId == null || experimentId == null) {
            return "control";
        }

        if (enableStickySessions && jedisPool != null) {
            try (Jedis jedis = jedisPool.getResource()) {
                String sessionKey = "ab:session:" + experimentId;
                String cachedVariant = jedis.hget(sessionKey, userId);
                if (cachedVariant != null) {
                    updateStickyMetrics(experimentId, cachedVariant);
                    return cachedVariant;
                }

                String variant = selectVariant(userId, experimentId);
                jedis.hset(sessionKey, userId, variant);
                jedis.expire(sessionKey, (int) STICKY_SESSION_DURATION.getSeconds());
                
                String metricsKey = "ab:metrics:" + experimentId + ":" + variant;
                jedis.zadd(metricsKey, System.currentTimeMillis(), userId);
                
                return variant;
            } catch (Exception e) {
                LOG.warn("Redis unavailable, using fallback", e);
                return fallbackRoute(userId, experimentId);
            }
        }

        return fallbackRoute(userId, experimentId);
    }

    private String fallbackRoute(String userId, String experimentId) {
        if (enableStickySessions) {
            String stickyKey = experimentId + ":" + userId;
            String cachedVariant = stickySessions.get(stickyKey);
            if (cachedVariant != null) {
                return cachedVariant;
            }
        }

        String variant = selectVariant(userId, experimentId);
        
        if (enableStickySessions) {
            stickySessions.put(experimentId + ":" + userId, variant);
        }

        return variant;
    }

    private boolean isValidRequest(ABRequest request) {
        return request != null && request.getUserId() != null && !request.getUserId().isEmpty();
    }

    private ModelVariant selectVariant(String userId, String experimentId) {
        int hash = computeConsistentHash(userId + experimentId);
        double threshold = challengerPercentage * HASH_MODULUS;
        
        if (hash < threshold) {
            updateDecisionMetrics(ModelVariant.CHALLENGER);
            return ModelVariant.CHALLENGER;
        }
        updateDecisionMetrics(ModelVariant.CONTROL);
        return ModelVariant.CONTROL;
    }

    private int computeConsistentHash(String value) {
        return Math.abs(value.hashCode()) % HASH_MODULUS;
    }

    private void updateDecisionMetrics(ModelVariant variant) {
        String key = variant.name();
        decisionCounters.computeIfAbsent(key, k -> new AtomicLong(0)).incrementAndGet();
    }

    private void updateStickyMetrics(String experimentId, String variant) {
        String key = experimentId + ":" + variant;
        stickyCounters.computeIfAbsent(key, k -> new AtomicLong(0)).incrementAndGet();
    }

    private ABResponse buildResponse(ABRequest request, ModelVariant variant) {
        ABResponse response = new ABResponse();
        response.setUserId(request.getUserId());
        response.setExperimentId(request.getExperimentId());
        response.setVariant(variant.name().toLowerCase());
        response.setModelEndpoint(getModelEndpoint(variant));
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    private String getModelEndpoint(ModelVariant variant) {
        return modelVariants.getOrDefault(variant.name(), 
            new ModelVariant(variant.name(), "/model/" + variant.name().toLowerCase())).getEndpoint();
    }

    private void logDecision(ABRequest request, ModelVariant variant) {
        if (logDecisions) {
            LOG.info("AB Decision: userId={}, experimentId={}, variant={}", 
                request.getUserId(), request.getExperimentId(), variant);
        }
    }

    private ABResponse createErrorResponse(ABRequest request, String errorMessage) {
        ABResponse response = new ABResponse();
        response.setUserId(request.getUserId());
        response.setExperimentId(request.getExperimentId());
        response.setVariant("control");
        response.setError(errorMessage);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    private ABResponse createFallbackResponse(ABRequest request) {
        ABResponse response = new ABResponse();
        response.setUserId(request != null ? request.getUserId() : "unknown");
        response.setExperimentId(request != null ? request.getExperimentId() : "unknown");
        response.setVariant("control");
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    private void cleanupStaleSessionsIfNeeded() {
        long now = System.currentTimeMillis();
        if (now - sessionCleanupTimestamp > 3600000) {
            stickySessions.entrySet().removeIf(entry -> 
                (now - sessionCleanupTimestamp) > STICKY_SESSION_DURATION.toMillis());
            sessionCleanupTimestamp = now;
        }
    }

    public Map<String, Long> getDecisionCounts() {
        Map<String, Long> result = new HashMap<>();
        decisionCounters.forEach((k, v) -> result.put(k, v.get()));
        return result;
    }

    public Map<String, Long> getStickySessionCounts() {
        Map<String, Long> result = new HashMap<>();
        stickyCounters.forEach((k, v) -> result.put(k, v.get()));
        return result;
    }

    public double getChallengerPercentage() {
        return challengerPercentage;
    }

    public void resetCounters() {
        decisionCounters.clear();
        stickyCounters.clear();
    }

    public enum ModelVariant {
        CONTROL("control", "/model/control"),
        CHALLENGER("challenger", "/model/challenger");

        private final String name;
        private final String endpoint;

        ModelVariant(String name, String endpoint) {
            this.name = name;
            this.endpoint = endpoint;
        }

        public String getName() { return name; }
        public String getEndpoint() { return endpoint; }
    }

    public static class ABRequest {
        private String userId;
        private String experimentId;
        private Map<String, Object> context;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getExperimentId() { return experimentId; }
        public void setExperimentId(String experimentId) { this.experimentId = experimentId; }
        public Map<String, Object> getContext() { return context; }
        public void setContext(Map<String, Object> context) { this.context = context; }
    }

    public static class ABResponse {
        private String userId;
        private String experimentId;
        private String variant;
        private String modelEndpoint;
        private long timestamp;
        private String error;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getExperimentId() { return experimentId; }
        public void setExperimentId(String experimentId) { this.experimentId = experimentId; }
        public String getVariant() { return variant; }
        public void setVariant(String variant) { this.variant = variant; }
        public String getModelEndpoint() { return modelEndpoint; }
        public void setModelEndpoint(String modelEndpoint) { this.modelEndpoint = modelEndpoint; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}