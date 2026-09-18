package com.pragma.featurestore.processing;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;
import com.pragma.featurestore.dto.FeatureEvent;
import com.pragma.featurestore.exception.FeatureComputationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class FeatureCalculator extends KeyedProcessFunction<String, FeatureEvent, FeatureEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(FeatureCalculator.class);
    private static final Duration WINDOW_5MIN = Duration.ofMinutes(5);
    private static final Duration WINDOW_1H = Duration.ofHours(1);
    private static final Duration WINDOW_24H = Duration.ofHours(24);
    private static final Duration LATE_DATA_THRESHOLD = Duration.ofMinutes(10);

    private transient ListState<FeatureEvent> eventBuffer;
    private transient Map<String, AggregatedFeature> aggregatedFeatures;
    private transient AtomicLong processingLatency;

    public FeatureCalculator() {
        this.aggregatedFeatures = new HashMap<>();
    }

    @Override
    public void open(Configuration parameters) {
        ListStateDescriptor<FeatureEvent> descriptor = new ListStateDescriptor<>(
                "eventBuffer",
                TypeInformation.of(FeatureEvent.class)
        );
        eventBuffer = getRuntimeContext().getListState(descriptor);
        processingLatency = new AtomicLong(0);
        aggregatedFeatures = new HashMap<>();

        LOG.info("FeatureCalculator initialized for key: {}", getRuntimeContext().getCurrentKey());
    }

    @Override
    public void processElement(FeatureEvent event, Context ctx, Collector<FeatureEvent> out) {
        long startTime = System.currentTimeMillis();

        try {
            if (!isValidEvent(event)) {
                LOG.warn("Invalid event received: {}", event.getEventId());
                return;
            }

            long eventTimestamp = event.getTimestamp();
            long currentWatermark = ctx.timerService().currentWatermark();

            if (isLateData(eventTimestamp, currentWatermark)) {
                handleLateData(event);
                LOG.debug("Late data handled for event: {} (timestamp: {}, watermark: {})",
                        event.getEventId(), eventTimestamp, currentWatermark);
                return;
            }

            String featureKey = buildFeatureKey(event);
            AggregatedFeature aggregated = aggregatedFeatures.computeIfAbsent(
                    featureKey,
                    k -> new AggregatedFeature(featureKey)
            );

            aggregated.addEvent(event);

            FeatureEvent windowed5min = computeWindowFeature(event, WINDOW_5MIN, "5min");
            FeatureEvent windowed1h = computeWindowFeature(event, WINDOW_1H, "1h");
            FeatureEvent windowed24h = computeWindowFeature(event, WINDOW_24H, "24h");

            if (windowed5min != null) out.collect(windowed5min);
            if (windowed1h != null) out.collect(windowed1h);
            if (windowed24h != null) out.collect(windowed24h);

            long latency = System.currentTimeMillis() - startTime;
            processingLatency.addAndGet(latency);

            LOG.debug("Event processed: {} with latency: {}ms", event.getEventId(), latency);

        } catch (Exception e) {
            LOG.error("Error processing event: {}", event.getEventId(), e);
            throw new FeatureComputationException("Failed to compute feature for event: " + event.getEventId(), e);
        }
    }

    private boolean isValidEvent(FeatureEvent event) {
        return event != null
                && event.getEventId() != null
                && event.getEntityId() != null
                && event.getFeatureName() != null
                && event.getTimestamp() > 0
                && event.getValue() != null;
    }

    private boolean isLateData(long eventTimestamp, long currentWatermark) {
        return currentWatermark > 0
                && (currentWatermark - eventTimestamp) > LATE_DATA_THRESHOLD.toMillis();
    }

    private void handleLateData(FeatureEvent event) {
        try {
            eventBuffer.add(event);
            LOG.debug("Late event added to buffer: {}", event.getEventId());
        } catch (Exception e) {
            LOG.error("Failed to buffer late event: {}", event.getEventId(), e);
        }
    }

    private String buildFeatureKey(FeatureEvent event) {
        return String.format("%s:%s:%s",
                event.getEntityType(),
                event.getEntityId(),
                event.getFeatureName());
    }

    private FeatureEvent computeWindowFeature(FeatureEvent event, Duration windowSize, String windowType) {
        String featureKey = buildFeatureKey(event);
        AggregatedFeature agg = aggregatedFeatures.get(featureKey);

        if (agg == null) {
            return null;
        }

        long windowStart = (event.getTimestamp() / windowSize.toMillis()) * windowSize.toMillis();
        long windowEnd = windowStart + windowSize.toMillis();

        List<FeatureEvent> windowEvents = agg.getEventsInWindow(windowStart, windowEnd);

        if (windowEvents.isEmpty()) {
            return null;
        }

        double aggregatedValue = computeAggregation(windowEvents, event.getAggregationType());

        FeatureEvent result = new FeatureEvent();
        result.setEventId(UUID.randomUUID().toString());
        result.setEntityType(event.getEntityType());
        result.setEntityId(event.getEntityId());
        result.setFeatureName(event.getFeatureName() + "_" + windowType);
        result.setValue(aggregatedValue);
        result.setTimestamp(windowEnd);
        result.setWindowType(windowType);
        result.setVersion(event.getVersion());
        result.setAggregationType(event.getAggregationType());

        return result;
    }

    private double computeAggregation(List<FeatureEvent> events, String aggregationType) {
        if (events == null || events.isEmpty()) {
            return 0.0;
        }

        return switch (aggregationType != null ? aggregationType.toUpperCase() : "SUM") {
            case "SUM" -> events.stream()
                    .mapToDouble(e -> ((Number) e.getValue()).doubleValue())
                    .sum();
            case "AVG" -> events.stream()
                    .mapToDouble(e -> ((Number) e.getValue()).doubleValue())
                    .average()
                    .orElse(0.0);
            case "COUNT" -> (double) events.size();
            case "MIN" -> events.stream()
                    .mapToDouble(e -> ((Number) e.getValue()).doubleValue())
                    .min()
                    .orElse(0.0);
            case "MAX" -> events.stream()
                    .mapToDouble(e -> ((Number) e.getValue()).doubleValue())
                    .max()
                    .orElse(0.0);
            default -> events.stream()
                    .mapToDouble(e -> ((Number) e.getValue()).doubleValue())
                    .sum();
        };
    }

    public static WatermarkStrategy<FeatureEvent> createWatermarkStrategy() {
        return WatermarkStrategy.<FeatureEvent>forBoundedOutOfOrderness(Duration.ofMinutes(5))
                .withTimestampAssigner((event, timestamp) -> event.getTimestamp())
                .withIdleness(Duration.ofMinutes(1));
    }

    public static SlidingEventTimeWindows createSlidingWindow(Duration size, Duration slide) {
        return SlidingEventTimeWindows.of(size, slide);
    }

    public long getProcessingLatency() {
        return processingLatency != null ? processingLatency.get() : 0;
    }

    private static class AggregatedFeature {
        private final String featureKey;
        private final List<FeatureEvent> events;
        private final Map<Long, List<FeatureEvent>> windowIndex;

        public AggregatedFeature(String featureKey) {
            this.featureKey = featureKey;
            this.events = new ArrayList<>();
            this.windowIndex = new HashMap<>();
        }

        public void addEvent(FeatureEvent event) {
            events.add(event);
            long windowKey = event.getTimestamp() / 300000;
            windowIndex.computeIfAbsent(windowKey, k -> new ArrayList<>()).add(event);
        }

        public List<FeatureEvent> getEventsInWindow(long windowStart, long windowEnd) {
            List<FeatureEvent> result = new ArrayList<>();
            for (FeatureEvent event : events) {
                if (event.getTimestamp() >= windowStart && event.getTimestamp() < windowEnd) {
                    result.add(event);
                }
            }
            return result;
        }
    }
}