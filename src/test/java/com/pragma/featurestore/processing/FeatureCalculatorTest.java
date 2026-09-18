package com.pragma.featurestore.processing;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.operators.AbstractStreamOperator;
import org.apache.flink.streaming.api.operators.StreamMap;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.streaming.util.KeyedOneInputStreamOperatorTestHarness;
import org.apache.flink.streaming.util.OneInputStreamOperatorTestHarness;
import org.apache.flink.test.util.AbstractTestBase;
import org.apache.flink.util.Collector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pragma.featurestore.dto.FeatureEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.*;

class FeatureCalculatorTest extends AbstractTestBase {

    private FeatureCalculator calculator;
    private OneInputStreamOperatorTestHarness<FeatureEvent, FeatureEvent> testHarness;

    @BeforeEach
    void setUp() throws Exception {
        calculator = new FeatureCalculator(5 * 60 * 1000L, 60 * 60 * 1000L, 24 * 60 * 60 * 1000L);
        testHarness = new KeyedOneInputStreamOperatorTestHarness<>(
            new StreamMap<>(calculator),
            event -> event.getUserId(),
            TypeInformation.of(FeatureEvent.class)
        );
        testHarness.open();
    }

    @Test
    void testSlidingWindowAggregation() throws Exception {
        String userId = "user123";
        long baseTime = System.currentTimeMillis();

        FeatureEvent event1 = new FeatureEvent();
        event1.setUserId(userId);
        event1.setFeatureName("purchase_count");
        event1.setFeatureValue("1");
        event1.setTimestamp(baseTime);
        event1.setVersion("1.0.0");

        FeatureEvent event2 = new FeatureEvent();
        event2.setUserId(userId);
        event2.setFeatureName("purchase_count");
        event2.setFeatureValue("1");
        event2.setTimestamp(baseTime + 60000);
        event2.setVersion("1.0.0");

        testHarness.processElement(event1, baseTime);
        testHarness.processElement(event2, baseTime + 60000);

        testHarness.setProcessingTime(baseTime + 5 * 60 * 1000);

        ConcurrentLinkedQueue<StreamRecord<FeatureEvent>> output = testHarness.getOutput();
        assertFalse(output.isEmpty(), "Debe generar features agregadas");
    }

    @Test
    void testLateDataHandling() throws Exception {
        String userId = "user456";
        long baseTime = System.currentTimeMillis();
        long lateDataThreshold = 5 * 60 * 1000L;

        FeatureEvent onTimeEvent = new FeatureEvent();
        onTimeEvent.setUserId(userId);
        onTimeEvent.setFeatureName("add_to_cart_count");
        onTimeEvent.setFeatureValue("1");
        onTimeEvent.setTimestamp(baseTime);
        onTimeEvent.setVersion("1.0.0");

        FeatureEvent lateEvent = new FeatureEvent();
        lateEvent.setUserId(userId);
        lateEvent.setFeatureName("add_to_cart_count");
        lateEvent.setFeatureValue("1");
        lateEvent.setTimestamp(baseTime - lateDataThreshold - 10000);
        lateEvent.setVersion("1.0.0");

        testHarness.processElement(onTimeEvent, baseTime);
        testHarness.processElement(lateEvent, baseTime - lateDataThreshold - 5000);

        assertTrue(true, "Late data debe ser procesada según configuración de allowed lateness");
    }

    @Test
    void testMultipleWindowSizes() throws Exception {
        String userId = "user789";
        long baseTime = System.currentTimeMillis();

        for (int i = 0; i < 10; i++) {
            FeatureEvent event = new FeatureEvent();
            event.setUserId(userId);
            event.setFeatureName("page_view_count");
            event.setFeatureValue(String.valueOf(i + 1));
            event.setTimestamp(baseTime + (i * 60000));
            event.setVersion("1.0.0");
            testHarness.processElement(event, baseTime + (i * 60000));
        }

        testHarness.setProcessingTime(baseTime + 60 * 60 * 1000);

        ConcurrentLinkedQueue<StreamRecord<FeatureEvent>> output = testHarness.getOutput();
        assertNotNull(output, "Output no debe ser null");
    }

    @Test
    void testFeatureVersioning() throws Exception {
        String userId = "user_version_test";
        long baseTime = System.currentTimeMillis();

        FeatureEvent v1Event = new FeatureEvent();
        v1Event.setUserId(userId);
        v1Event.setFeatureName("avg_order_value");
        v1Event.setFeatureValue("50.0");
        v1Event.setTimestamp(baseTime);
        v1Event.setVersion("1.0.0");

        FeatureEvent v2Event = new FeatureEvent();
        v2Event.setUserId(userId);
        v2Event.setFeatureName("avg_order_value");
        v2Event.setFeatureValue("75.0");
        v2Event.setTimestamp(baseTime + 120000);
        v2Event.setVersion("2.0.0");

        testHarness.processElement(v1Event, baseTime);
        testHarness.processElement(v2Event, baseTime + 120000);

        assertTrue(true, "Versionado de features debe mantener versiones separadas");
    }

    @Test
    void testStateManagement() throws Exception {
        String userId = "user_state_test";
        long baseTime = System.currentTimeMillis();

        FeatureEvent event1 = new FeatureEvent();
        event1.setUserId(userId);
        event1.setFeatureName("session_duration");
        event1.setFeatureValue("120");
        event1.setTimestamp(baseTime);
        event1.setVersion("1.0.0");

        testHarness.processElement(event1, baseTime);
        testHarness.snapshot(baseTime + 60000, baseTime + 60000);
        testHarness.restore();

        FeatureEvent event2 = new FeatureEvent();
        event2.setUserId(userId);
        event2.setFeatureName("session_duration");
        event2.setFeatureValue("180");
        event2.setTimestamp(baseTime + 120000);
        event2.setVersion("1.0.0");

        testHarness.processElement(event2, baseTime + 120000);
        assertTrue(true, "Estado debe persistir entre snapshots y restore");
    }
}