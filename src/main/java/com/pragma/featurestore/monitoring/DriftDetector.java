package com.pragma.featurestore.monitoring;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class DriftDetector extends ProcessAllWindowFunction<FeatureEvent, DriftDetector.DriftResult, GlobalWindow> {
    private static final double EPSILON = 1e-10;
    private static final int MIN_SAMPLES = 100;
    private static final double DRIFT_THRESHOLD = 0.15;

    private final double driftThreshold;
    private final double warningThreshold;

    private transient ListState<FeatureEvent> trainingSetState;
    private transient ListState<FeatureEvent> onlineFeaturesState;
    private transient Counter driftAlertsCounter;
    private transient Counter samplesProcessedCounter;
    private transient Map<String, Double> latestDriftScores;
    private transient AtomicReference<Map<String, List<Double>>> trainingDistributions;

    public DriftDetector() {
        this(DRIFT_THRESHOLD, 0.10);
    }

    public DriftDetector(double driftThreshold, double warningThreshold) {
        this.driftThreshold = driftThreshold;
        this.warningThreshold = warningThreshold;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        ListStateDescriptor<FeatureEvent> trainingDescriptor = new ListStateDescriptor<>(
            "drift-training-set",
            TypeInformation.of(FeatureEvent.class)
        );
        trainingSetState = getRuntimeContext().getListState(trainingDescriptor);

        ListStateDescriptor<FeatureEvent> onlineDescriptor = new ListStateDescriptor<>(
            "drift-online-features",
            TypeInformation.of(FeatureEvent.class)
        );
        onlineFeaturesState = getRuntimeContext().getListState(onlineDescriptor);

        MetricGroup metricGroup = getRuntimeContext().getMetricGroup();
        driftAlertsCounter = metricGroup.counter("drift_alerts_total");
        samplesProcessedCounter = metricGroup.counter("drift_samples_processed_total");

        latestDriftScores = new HashMap<>();
        trainingDistributions = new AtomicReference<>(new HashMap<>());
    }

    @Override
    public void process(Context context, Iterable<FeatureEvent> elements, Collector<DriftResult> out) throws Exception {
        List<FeatureEvent> windowEvents = new ArrayList<>();
        elements.forEach(windowEvents::add);

        if (windowEvents.isEmpty()) {
            return;
        }

        samplesProcessedCounter.inc(windowEvents.size());

        Map<String, List<Double>> onlineDistributions = computeDistributions(windowEvents);
        Map<String, List<Double>> trainingDist = trainingDistributions.get();

        for (String featureName : onlineDistributions.keySet()) {
            List<Double> online = onlineDistributions.get(featureName);
            List<Double> training = trainingDist.getOrDefault(featureName, online);

            if (training.size() != online.size()) {
                throw new IllegalArgumentException(
                    "Training and online distributions must have the same length for feature: " + featureName);
            }

            double driftScore = calculateKLDivergence(training, online);
            latestDriftScores.put(featureName, driftScore);

            DriftLevel level = determineDriftLevel(driftScore);
            DriftResult result = new DriftResult(
                featureName,
                driftScore,
                level,
                training,
                online,
                System.currentTimeMillis()
            );

            if (level != DriftLevel.NONE) {
                driftAlertsCounter.inc();
            }

            out.collect(result);
        }
    }

    public DriftResult detectDrift(String featureName, List<Double> trainingDistribution, 
                                    List<Double> onlineDistribution) {
        if (trainingDistribution == null || onlineDistribution == null) {
            throw new IllegalArgumentException("Distributions cannot be null");
        }
        if (trainingDistribution.size() != onlineDistribution.size()) {
            throw new IllegalArgumentException(
                "Distributions must have the same length: " + 
                trainingDistribution.size() + " vs " + onlineDistribution.size());
        }

        List<Double> normalizedTraining = normalizeDistribution(trainingDistribution);
        List<Double> normalizedOnline = normalizeDistribution(onlineDistribution);

        double driftScore = calculateKLDivergence(normalizedTraining, normalizedOnline);
        DriftLevel level = determineDriftLevel(driftScore);

        return new DriftResult(featureName, driftScore, level, normalizedTraining, 
                               normalizedOnline, System.currentTimeMillis());
    }

    public List<DriftResult> detectBatchDrift(Map<String, List<Double>> trainingFeatures,
                                               Map<String, List<Double>> onlineFeatures) {
        List<DriftResult> results = new ArrayList<>();

        for (String featureName : onlineFeatures.keySet()) {
            List<Double> training = trainingFeatures.get(featureName);
            List<Double> online = onlineFeatures.get(featureName);

            if (training == null) {
                results.add(new DriftResult(featureName, 0.0, DriftLevel.NONE, 
                    new ArrayList<>(), online, System.currentTimeMillis()));
                continue;
            }

            try {
                DriftResult result = detectDrift(featureName, training, online);
                results.add(result);
            } catch (IllegalArgumentException e) {
                results.add(new DriftResult(featureName, 1.0, DriftLevel.SEVERE,
                    training, online, System.currentTimeMillis()));
            }
        }

        return results;
    }

    private Map<String, List<Double>> computeDistributions(List<FeatureEvent> events) {
        Map<String, List<Double>> distributions = new HashMap<>();
        Map<String, List<Double>> featureValues = new HashMap<>();

        for (FeatureEvent event : events) {
            String featureName = event.getFeatureName();
            featureValues.computeIfAbsent(featureName, k -> new ArrayList<>()).add(event.getFeatureValue());
        }

        for (Map.Entry<String, List<Double>> entry : featureValues.entrySet()) {
            distributions.put(entry.getKey(), normalizeDistribution(entry.getValue()));
        }

        return distributions;
    }

    private List<Double> normalizeDistribution(List<Double> values) {
        if (values == null || values.isEmpty()) {
            return new ArrayList<>();
        }

        double sum = values.stream().mapToDouble(Double::doubleValue).sum();
        if (Math.abs(sum) < EPSILON) {
            return new ArrayList<>(Collections.nCopies(values.size(), 1.0 / values.size()));
        }

        return values.stream().map(v -> v / sum).toList();
    }

    private double calculateKLDivergence(List<Double> p, List<Double> q) {
        if (p.size() != q.size()) {
            throw new IllegalArgumentException("Distributions must have the same size");
        }

        double klDivergence = 0.0;
        for (int i = 0; i < p.size(); i++) {
            double pi = Math.max(p.get(i), EPSILON);
            double qi = Math.max(q.get(i), EPSILON);
            klDivergence += pi * Math.log(pi / qi);
        }

        return Math.min(Math.max(klDivergence, 0.0), 1.0);
    }

    private Map<Double, Long> calculateHistogram(List<Double> values) {
        Map<Double, Long> histogram = new HashMap<>();
        for (Double value : values) {
            histogram.merge(value, 1L, Long::sum);
        }
        return histogram;
    }

    private DriftLevel determineDriftLevel(double driftScore) {
        if (driftScore < warningThreshold) {
            return DriftLevel.NONE;
        } else if (driftScore < driftThreshold) {
            return DriftLevel.WARNING;
        } else if (driftScore < driftThreshold * 1.5) {
            return DriftLevel.SEVERE;
        } else {
            return DriftLevel.CRITICAL;
        }
    }

    public void updateTrainingSet(List<FeatureEvent> newTrainingData) throws Exception {
        trainingSetState.clear();
        for (FeatureEvent event : newTrainingData) {
            trainingSetState.add(event);
        }

        Map<String, List<Double>> newDistributions = computeDistributions(newTrainingData);
        trainingDistributions.set(newDistributions);
    }

    public static class DriftResult {
        private final String featureName;
        private final double driftScore;
        private final DriftLevel driftLevel;
        private final List<Double> trainingDistribution;
        private final List<Double> onlineDistribution;
        private final long timestamp;

        public DriftResult(String featureName, double driftScore, DriftLevel driftLevel,
                          List<Double> trainingDistribution, List<Double> onlineDistribution,
                          long timestamp) {
            this.featureName = featureName;
            this.driftScore = driftScore;
            this.driftLevel = driftLevel;
            this.trainingDistribution = trainingDistribution;
            this.onlineDistribution = onlineDistribution;
            this.timestamp = timestamp;
        }

        public String getFeatureName() { return featureName; }
        public double getDriftScore() { return driftScore; }
        public DriftLevel getDriftLevel() { return driftLevel; }
        public List<Double> getTrainingDistribution() { return trainingDistribution; }
        public List<Double> getOnlineDistribution() { return onlineDistribution; }
        public long getTimestamp() { return timestamp; }

        public boolean hasDrift() {
            return driftLevel != DriftLevel.NONE;
        }
    }

    public enum DriftLevel {
        NONE,
        WARNING,
        SEVERE,
        CRITICAL
    }
}