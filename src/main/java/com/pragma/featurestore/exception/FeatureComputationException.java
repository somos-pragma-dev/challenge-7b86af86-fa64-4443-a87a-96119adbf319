package com.pragma.featurestore.exception;

import lombok.Getter;
import java.time.Instant;
import java.util.Map;

@Getter
public class FeatureComputationException extends RuntimeException {
    
    private final String featureName;
    private final String eventId;
    private final Instant eventTimestamp;
    private final Map<String, Object> eventContext;
    private final String computationStage;
    private final boolean retryable;
    
    public FeatureComputationException(String message, String featureName, String eventId) {
        super(message);
        this.featureName = featureName;
        this.eventId = eventId;
        this.eventTimestamp = null;
        this.eventContext = null;
        this.computationStage = "UNKNOWN";
        this.retryable = false;
    }
    
    public FeatureComputationException(String message, String featureName, String eventId, 
                                       Instant eventTimestamp, Map<String, Object> eventContext,
                                       String computationStage, boolean retryable) {
        super(buildMessage(message, featureName, eventId, computationStage));
        this.featureName = featureName;
        this.eventId = eventId;
        this.eventTimestamp = eventTimestamp;
        this.eventContext = eventContext != null ? Map.copyOf(eventContext) : Map.of();
        this.computationStage = computationStage;
        this.retryable = retryable;
    }
    
    public FeatureComputationException(String message, String featureName, String eventId, 
                                       Throwable cause) {
        super(message, cause);
        this.featureName = featureName;
        this.eventId = eventId;
        this.eventTimestamp = null;
        this.eventContext = null;
        this.computationStage = "UNKNOWN";
        this.retryable = cause instanceof java.io.IOException || 
                         cause instanceof java.util.concurrent.TimeoutException;
    }
    
    private static String buildMessage(String message, String featureName, String eventId, 
                                       String computationStage) {
        return String.format("Feature computation failed: %s | Feature: %s | Event: %s | Stage: %s",
                           message, featureName, eventId, computationStage);
    }
    
    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("FeatureComputationException{")
          .append("featureName=").append(featureName)
          .append(", eventId=").append(eventId)
          .append(", computationStage=").append(computationStage)
          .append(", retryable=").append(retryable);
        
        if (eventTimestamp != null) {
            sb.append(", eventTimestamp=").append(eventTimestamp);
        }
        
        if (eventContext != null && !eventContext.isEmpty()) {
            sb.append(", contextKeys=").append(eventContext.keySet());
        }
        
        sb.append("}");
        return sb.toString();
    }
    
    public FeatureComputationException withContext(String key, Object value) {
        Map<String, Object> newContext = new java.util.HashMap<>(this.eventContext);
        newContext.put(key, value);
        return new FeatureComputationException(
            getMessage(),
            this.featureName,
            this.eventId,
            this.eventTimestamp,
            newContext,
            this.computationStage,
            this.retryable
        );
    }
}