package com.pragma.featurestore.config;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.runtime.state.storage.FileSystemCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.concurrent.TimeUnit;

public class FlinkConfig {

    private static final long CHECKPOINT_INTERVAL_MS = 60_000L;
    private static final long MIN_PAUSE_BETWEEN_CHECKPOINTS_MS = 30_000L;
    private static final int MAX_CONCURRENT_CHECKPOINTS = 1;
    private static final int NUMBER_OF_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 10_000L;
    private static final int DEFAULT_PARALLELISM = 4;
    private static final int MAX_PARALLELISM = 16;
    private static final long WATERMARK_IDLE_TIMEOUT_MS = 60_000L;
    private static final long WATERMARK_INTERVAL_MS = 200L;
    private static final long MAX_OUT_OF_ORDERNESS_MS = 5_000L;

    private final String checkpointDir;
    private final int parallelism;
    private final boolean enableUnalignedCheckpoints;
    private final boolean enableIncrementalCheckpoints;

    public FlinkConfig(String checkpointDir) {
        this(checkpointDir, DEFAULT_PARALLELISM, true, true);
    }

    public FlinkConfig(String checkpointDir, int parallelism, 
                      boolean enableUnalignedCheckpoints, boolean enableIncrementalCheckpoints) {
        this.checkpointDir = checkpointDir;
        this.parallelism = parallelism;
        this.enableUnalignedCheckpoints = enableUnalignedCheckpoints;
        this.enableIncrementalCheckpoints = enableIncrementalCheckpoints;
    }

    public StreamExecutionEnvironment configureEnvironment(StreamExecutionEnvironment env) {
        configureCheckpointing(env);
        configureRestartStrategy(env);
        configureParallelism(env);
        configureTimeCharacteristics(env);
        configureStateBackend(env);
        configureTaskCancellation(env);

        return env;
    }

    private void configureCheckpointing(StreamExecutionEnvironment env) {
        CheckpointConfig checkpointConfig = env.getCheckpointConfig();

        env.enableCheckpointing(CHECKPOINT_INTERVAL_MS, CheckpointingMode.EXACTLY_ONCE);

        checkpointConfig.setMinPauseBetweenCheckpoints(MIN_PAUSE_BETWEEN_CHECKPOINTS_MS);
        checkpointConfig.setMaxConcurrentCheckpoints(MAX_CONCURRENT_CHECKPOINTS);

        checkpointConfig.setTolerableCheckpointFailureNumber(3);

        checkpointConfig.setExternalizedCheckpointCleanup(
            CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION
        );

        if (enableUnalignedCheckpoints) {
            checkpointConfig.enableUnalignedCheckpoints();
        }

        if (enableIncrementalCheckpoints) {
            checkpointConfig.setIncrementalCheckpointing(true);
        }

        try {
            env.getCheckpointConfig().setCheckpointStorage(
                new FileSystemCheckpointStorage(checkpointDir)
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure checkpoint storage: " + checkpointDir, e);
        }
    }

    private void configureRestartStrategy(StreamExecutionEnvironment env) {
        ExecutionConfig executionConfig = env.getConfig();

        executionConfig.setRestartStrategy(
            RestartStrategies.failureRateRestart(
                NUMBER_OF_RETRIES,
                Time.of(5, TimeUnit.MINUTES),
                Time.of(RETRY_DELAY_MS, TimeUnit.MILLISECONDS)
            )
        );
    }

    private void configureParallelism(StreamExecutionEnvironment env) {
        env.setParallelism(parallelism);
        env.setMaxParallelism(MAX_PARALLELISM);
    }

    private void configureTimeCharacteristics(StreamExecutionEnvironment env) {
        env.setStreamTimeCharacteristic(org.apache.flink.streaming.api.TimeCharacteristic.EventTime);

        env.getConfig().setAutoWatermarkInterval(WATERMARK_INTERVAL_MS);
    }

    private void configureStateBackend(StreamExecutionEnvironment env) {
        Configuration flinkConfig = new Configuration();
        flinkConfig.setString("state.backend", "rocksdb");
        flinkConfig.setString("state.checkpoints.dir", checkpointDir);
        flinkConfig.setString("state.savepoints.dir", checkpointDir + "/savepoints");

        env.configure(flinkConfig);
    }

    private void configureTaskCancellation(StreamExecutionEnvironment env) {
        env.getConfig().setTaskCancellationTimeout(30_000L);
        env.getConfig().setTaskCancellationInterruptionOrder(
            ExecutionConfig.TaskCancellationInterruptionOrder.IMMEDIATE
        );
    }

    public static class Builder {
        private String checkpointDir = "file:///tmp/flink-checkpoints";
        private int parallelism = DEFAULT_PARALLELISM;
        private boolean enableUnalignedCheckpoints = true;
        private boolean enableIncrementalCheckpoints = true;

        public Builder checkpointDir(String checkpointDir) {
            this.checkpointDir = checkpointDir;
            return this;
        }

        public Builder parallelism(int parallelism) {
            this.parallelism = parallelism;
            return this;
        }

        public Builder enableUnalignedCheckpoints(boolean enable) {
            this.enableUnalignedCheckpoints = enable;
            return this;
        }

        public Builder enableIncrementalCheckpoints(boolean enable) {
            this.enableIncrementalCheckpoints = enable;
            return this;
        }

        public FlinkConfig build() {
            return new FlinkConfig(checkpointDir, parallelism, 
                                  enableUnalignedCheckpoints, enableIncrementalCheckpoints);
        }
    }

    public String getCheckpointDir() {
        return checkpointDir;
    }

    public int getParallelism() {
        return parallelism;
    }

    public boolean isEnableUnalignedCheckpoints() {
        return enableUnalignedCheckpoints;
    }

    public boolean isEnableIncrementalCheckpoints() {
        return enableIncrementalCheckpoints;
    }

    public static Builder builder() {
        return new Builder();
    }
}