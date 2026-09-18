package com.pragma.featurestore.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class FeatureEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private String userId;
    private String featureName;
    private Object featureValue;
    private Instant timestamp;
    private Instant processingTimestamp;
    private String version;
    private Map<String, Object> metadata;
    private String entityType;
    private String entityId;
    private String aggregationType;
    private Long windowSizeMs;

    public FeatureEvent() {
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public Object getFeatureValue() {
        return featureValue;
    }

    public void setFeatureValue(Object featureValue) {
        this.featureValue = featureValue;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimestamp(long timestampMs) {
        this.timestamp = Instant.ofEpochMilli(timestampMs);
    }

    public Instant getProcessingTimestamp() {
        return processingTimestamp;
    }

    public void setProcessingTimestamp(Instant processingTimestamp) {
        this.processingTimestamp = processingTimestamp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getAggregationType() {
        return aggregationType;
    }

    public void setAggregationType(String aggregationType) {
        this.aggregationType = aggregationType;
    }

    public Long getWindowSizeMs() {
        return windowSizeMs;
    }

    public void setWindowSizeMs(Long windowSizeMs) {
        this.windowSizeMs = windowSizeMs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeatureEvent that = (FeatureEvent) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "FeatureEvent{" +
                "eventId='" + eventId + '\'' +
                ", userId='" + userId + '\'' +
                ", featureName='" + featureName + '\'' +
                ", featureValue=" + featureValue +
                ", timestamp=" + timestamp +
                ", version='" + version + '\'' +
                '}';
    }
}