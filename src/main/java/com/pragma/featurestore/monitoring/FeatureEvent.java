package com.pragma.featurestore.monitoring;

public class FeatureEvent {
    private String eventId;
    private String entityType;
    private String entityId;
    private String featureName;
    private double featureValue;
    private long timestamp;
    private String version;
    private Map<String, Object> metadata;

    public FeatureEvent() {}

    public FeatureEvent(String eventId, String entityType, String entityId, 
                       String featureName, double featureValue, long timestamp) {
        this.eventId = eventId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.featureName = featureName;
        this.featureValue = featureValue;
        this.timestamp = timestamp;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getFeatureName() { return featureName; }
    public void setFeatureName(String featureName) { this.featureName = featureName; }
    public double getFeatureValue() { return featureValue; }
    public void setFeatureValue(double featureValue) { this.featureValue = featureValue; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}