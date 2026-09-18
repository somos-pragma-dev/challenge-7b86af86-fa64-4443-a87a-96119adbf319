package com.pragma.featurestore.monitoring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class DriftDetectorTest {

    private DriftDetector detector;
    private static final double DEFAULT_THRESHOLD = 0.15;
    private static final double WARNING_THRESHOLD = 0.10;

    @BeforeEach
    void setUp() {
        detector = new DriftDetector(DEFAULT_THRESHOLD, WARNING_THRESHOLD);
    }

    @Test
    void testNoDriftDetected() {
        List<Double> trainingDistribution = Arrays.asList(0.3, 0.4, 0.3);
        List<Double> onlineDistribution = Arrays.asList(0.31, 0.39, 0.30);

        DriftDetector.DriftResult result = detector.detectDrift(
            "feature_user_age",
            trainingDistribution,
            onlineDistribution
        );

        assertFalse(result.hasDrift(), "No debe detectar drift con distribuciones similares");
        assertEquals(DriftDetector.DriftLevel.NONE, result.getDriftLevel());
    }

    @Test
    void testSignificantDriftDetected() {
        List<Double> trainingDistribution = Arrays.asList(0.5, 0.3, 0.2);
        List<Double> onlineDistribution = Arrays.asList(0.1, 0.6, 0.3);

        DriftDetector.DriftResult result = detector.detectDrift(
            "feature_purchase_category",
            trainingDistribution,
            onlineDistribution
        );

        assertTrue(result.hasDrift(), "Debe detectar drift significativo");
        assertTrue(result.getDriftScore() > DEFAULT_THRESHOLD);
    }

    @Test
    void testWarningLevelDrift() {
        List<Double> trainingDistribution = Arrays.asList(0.4, 0.35, 0.25);
        List<Double> onlineDistribution = Arrays.asList(0.32, 0.40, 0.28);

        DriftDetector.DriftResult result = detector.detectDrift(
            "feature_browse_time",
            trainingDistribution,
            onlineDistribution
        );

        assertTrue(result.getDriftScore() > WARNING_THRESHOLD);
        assertTrue(result.getDriftScore() <= DEFAULT_THRESHOLD);
    }

    @Test
    void testDistributionNormalization() {
        List<Double> trainingUnnormalized = Arrays.asList(100.0, 80.0, 70.0);
        List<Double> onlineUnnormalized = Arrays.asList(90.0, 85.0, 75.0);

        DriftDetector.DriftResult result = detector.detectDrift(
            "feature_click_count",
            trainingUnnormalized,
            onlineUnnormalized
        );

        assertNotNull(result);
        assertTrue(result.getDriftScore() >= 0.0 && result.getDriftScore() <= 1.0);
    }

    @Test
    void testDifferentDistributionLengths() {
        List<Double> training = Arrays.asList(0.4, 0.3, 0.2, 0.1);
        List<Double> online = Arrays.asList(0.35, 0.35, 0.2);

        assertThrows(IllegalArgumentException.class, () -> detector.detectDrift(
            "feature_mismatch",
            training,
            online
        ));
    }

    @Test
    void testSyntheticDistributionGeneration() {
        List<Double> uniformDist = generateSyntheticDistribution(10, "uniform");
        assertEquals(10, uniformDist.size());
        assertEquals(1.0, uniformDist.stream().mapToDouble(Double::doubleValue).sum(), 0.001);

        List<Double> skewedDist = generateSyntheticDistribution(10, "skewed");
        assertEquals(10, skewedDist.size());
        assertTrue(skewedDist.get(0) > skewedDist.get(skewedDist.size() - 1));
    }

    @Test
    void testMultipleFeatureDriftMonitoring() {
        Map<String, List<Double>> trainingFeatures = new HashMap<>();
        Map<String, List<Double>> onlineFeatures = new HashMap<>();

        trainingFeatures.put("age", Arrays.asList(0.3, 0.4, 0.3));
        onlineFeatures.put("age", Arrays.asList(0.25, 0.45, 0.30));

        trainingFeatures.put("income", Arrays.asList(0.2, 0.5, 0.3));
        onlineFeatures.put("income", Arrays.asList(0.1, 0.6, 0.3));

        List<DriftDetector.DriftResult> results = detector.detectBatchDrift(trainingFeatures, onlineFeatures);

        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(r -> r.getFeatureName().equals("age")));
        assertTrue(results.stream().anyMatch(r -> r.getFeatureName().equals("income")));
    }

    @Test
    void testAlertThresholdConfiguration() {
        DriftDetector strictDetector = new DriftDetector(0.05, 0.03);

        List<Double> training = Arrays.asList(0.5, 0.3, 0.2);
        List<Double> online = Arrays.asList(0.48, 0.32, 0.20);

        DriftDetector.DriftResult result = strictDetector.detectDrift(
            "feature_strict",
            training,
            online
        );

        assertTrue(result.getDriftScore() > 0.03);
    }

    @Test
    void testDriftScoreCalculation() {
        List<Double> identical = Arrays.asList(0.33, 0.33, 0.34);
        DriftDetector.DriftResult result = detector.detectDrift(
            "feature_identical",
            identical,
            identical
        );

        assertEquals(0.0, result.getDriftScore(), 0.001);
    }

    private List<Double> generateSyntheticDistribution(int bins, String type) {
        Random random = new Random(42);
        List<Double> values = new ArrayList<>();

        if ("uniform".equals(type)) {
            double base = 1.0 / bins;
            for (int i = 0; i < bins; i++) {
                values.add(base + (random.nextDouble() * 0.01 - 0.005));
            }
        } else if ("skewed".equals(type)) {
            double sum = 0;
            for (int i = 0; i < bins; i++) {
                double value = 1.0 / (i + 1);
                values.add(value);
                sum += value;
            }
            values = values.stream().map(v -> v / sum).collect(Collectors.toList());
        }

        return values;
    }
}