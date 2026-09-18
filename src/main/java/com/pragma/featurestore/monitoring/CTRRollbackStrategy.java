package com.pragma.featurestore.monitoring;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Gauge;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CTRRollbackStrategy extends ProcessAllWindowFunction<CTRRollbackStrategy.CTREvent, CTRRollbackStrategy.RollbackDecision, GlobalWindow> {

    private static final double DEGRADATION_THRESHOLD = 0.05;
    private static final long ROLLBACK_WINDOW_MS = 30 * 60 * 1000L;
    private static final double MIN_SAMPLE_SIZE = 1000.0;

    private transient ListState<CTREvent> eventHistoryState;
    private transient Counter rollbacksTriggeredCounter;
    private transient Counter rollbacksCancelledCounter;
    private transient Counter totalImpressionsCounter;
    private transient Counter totalClicksCounter;

    private final ConcurrentLinkedQueue<CTREvent> recentEvents = new ConcurrentLinkedQueue<>();

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        ListStateDescriptor<CTREvent> descriptor = new ListStateDescriptor<>(
            "ctr-events-history",
            TypeInformation.of(CTREvent.class)
        );
        eventHistoryState = getRuntimeContext().getListState(descriptor);

        MetricGroup metricGroup = getRuntimeContext().getMetricGroup();
        rollbacksTriggeredCounter = metricGroup.counter("ctr_rollbacks_triggered_total");
        rollbacksCancelledCounter = metricGroup.counter("ctr_rollbacks_cancelled_total");
        totalImpressionsCounter = metricGroup.counter("ctr_total_impressions");
        totalClicksCounter = metricGroup.counter("ctr_total_clicks");

        metricGroup.gauge("ctr_challenger_current", (Gauge<Double>) () -> calculateCurrentCTR("challenger"));
        metricGroup.gauge("ctr_control_current", (Gauge<Double>) () -> calculateCurrentCTR("control"));
        metricGroup.gauge("ctr_degradation_current", (Gauge<Double>) () -> calculateDegradation());
    }

    @Override
    public void process(Context context, Iterable<CTREvent> elements, Collector<RollbackDecision> out) throws Exception {
        List<CTREvent> windowEvents = new ArrayList<>();
        elements.forEach(windowEvents::add);

        if (windowEvents.isEmpty()) {
            return;
        }

        for (CTREvent event : windowEvents) {
            recentEvents.add(event);
            totalImpressionsCounter.inc();
            if (event.isClick()) {
                totalClicksCounter.inc();
            }
        }

        cleanupOldEvents();

        double challengerCTR = calculateCurrentCTR("challenger");
        double controlCTR = calculateCurrentCTR("control");
        double degradation = calculateDegradation();

        boolean shouldRollback = shouldTriggerRollback(challengerCTR, controlCTR, degradation);

        RollbackDecision decision = new RollbackDecision(
            shouldRollback,
            challengerCTR,
            controlCTR,
            degradation,
            System.currentTimeMillis(),
            getSampleSize(),
            determineAffectedFeatures()
        );

        if (shouldRollback) {
            rollbacksTriggeredCounter.inc();
        } else {
            rollbacksCancelledCounter.inc();
        }

        out.collect(decision);
    }

    private void cleanupOldEvents() {
        long cutoffTime = System.currentTimeMillis() - ROLLBACK_WINDOW_MS;
        while (!recentEvents.isEmpty() && recentEvents.peek().getTimestamp() < cutoffTime) {
            recentEvents.poll();
        }
    }

    private double calculateCurrentCTR(String variant) {
        long impressions = recentEvents.stream()
            .filter(e -> variant.equals(e.getVariant()))
            .count();

        long clicks = recentEvents.stream()
            .filter(e -> variant.equals(e.getVariant()) && e.isClick())
            .count();

        if (impressions == 0) {
            return 0.0;
        }

        return (double) clicks / impressions;
    }

    private double calculateDegradation() {
        double challengerCTR = calculateCurrentCTR("challenger");
        double controlCTR = calculateCurrentCTR("control");

        if (controlCTR == 0.0) {
            return 0.0;
        }

        return (controlCTR - challengerCTR) / controlCTR;
    }

    private boolean shouldTriggerRollback(double challengerCTR, double controlCTR, double degradation) {
        if (getSampleSize() < MIN_SAMPLE_SIZE) {
            return false;
        }

        if (challengerCTR == 0.0 && controlCTR > 0.0) {
            return true;
        }

        return degradation > DEGRADATION_THRESHOLD;
    }

    private long getSampleSize() {
        return recentEvents.stream()
            .filter(e -> "challenger".equals(e.getVariant()))
            .count();
    }

    private List<String> determineAffectedFeatures() {
        return recentEvents.stream()
            .map(CTREvent::getFeatureVersion)
            .distinct()
            .toList();
    }

    public void recordEvent(CTREvent event) throws Exception {
        eventHistoryState.add(event);
    }

    public static class CTREvent {
        private final String experimentId;
        private final String variant;
        private final boolean isClick;
        private final long timestamp;
        private final String featureVersion;
        private final String userId;

        public CTREvent(String experimentId, String variant, boolean isClick,
                       long timestamp, String featureVersion, String userId) {
            this.experimentId = experimentId;
            this.variant = variant;
            this.isClick = isClick;
            this.timestamp = timestamp;
            this.featureVersion = featureVersion;
            this.userId = userId;
        }

        public String getExperimentId() { return experimentId; }
        public String getVariant() { return variant; }
        public boolean isClick() { return isClick; }
        public long getTimestamp() { return timestamp; }
        public String getFeatureVersion() { return featureVersion; }
        public String getUserId() { return userId; }
    }

    public static class RollbackDecision {
        private final boolean shouldRollback;
        private final double challengerCTR;
        private final double controlCTR;
        private final double degradation;
        private final long timestamp;
        private final long sampleSize;
        private final List<String> affectedFeatures;

        public RollbackDecision(boolean shouldRollback, double challengerCTR, double controlCTR,
                               double degradation, long timestamp, long sampleSize,
                               List<String> affectedFeatures) {
            this.shouldRollback = shouldRollback;
            this.challengerCTR = challengerCTR;
            this.controlCTR = controlCTR;
            this.degradation = degradation;
            this.timestamp = timestamp;
            this.sampleSize = sampleSize;
            this.affectedFeatures = affectedFeatures;
        }

        public boolean shouldRollback() { return shouldRollback; }
        public double getChallengerCTR() { return challengerCTR; }
        public double getControlCTR() { return controlCTR; }
        public double getDegradation() { return degradation; }
        public long getTimestamp() { return timestamp; }
        public long getSampleSize() { return sampleSize; }
        public List<String> getAffectedFeatures() { return affectedFeatures; }
    }
}