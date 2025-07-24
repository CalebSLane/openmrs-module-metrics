package org.openmrs.module.metrics;

import java.io.File;
import java.util.Arrays;

import org.openmrs.module.metrics.api.annotation.ConditionalOnBean;
import org.openmrs.util.OpenmrsThreadPoolHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.cache.Cache;

import groovy.util.logging.Slf4j;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.GuavaCacheMetrics;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmCompilationMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmHeapPressureMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmInfoMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadDeadlockMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.logging.Log4j2Metrics;
import io.micrometer.core.instrument.binder.system.DiskSpaceMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;

@Configuration
@Slf4j
public class DefaultMetricsConfig {
    @Bean
    public DiskSpaceMetrics diskSpaceMetrics() {
        return new DiskSpaceMetrics(new File("."));
    }

    @Bean
    public FileDescriptorMetrics fileDescriptorMetrics() {
        return new FileDescriptorMetrics();
    }

    @Bean
    public ProcessorMetrics processorMetrics() {
        return new ProcessorMetrics();
    }

    @Bean
    public UptimeMetrics uptimeMetrics() {
        return new UptimeMetrics();
    }

    @Bean
    public ClassLoaderMetrics classLoaderMetrics() {
        return new ClassLoaderMetrics();
    }

    @Bean
    public JvmMemoryMetrics jvmMemoryMetrics() {
        return new JvmMemoryMetrics();
    }   

    @Bean
    public JvmGcMetrics jvmGcMetrics() {
        return new JvmGcMetrics();
    }
    @Bean
    public JvmThreadMetrics jvmThreadMetrics() {
        return new JvmThreadMetrics();
    }   

    @Bean   
    public JvmThreadDeadlockMetrics jvmThreadDeadlockMetrics() {
        return new JvmThreadDeadlockMetrics();
    }   

    @Bean
    public JvmHeapPressureMetrics jvmHeapPressureMetrics() {
        return new JvmHeapPressureMetrics();
    }
    @Bean
    public JvmInfoMetrics jvmInfoMetrics() {
        return new JvmInfoMetrics();
    }   
    @Bean
    public JvmCompilationMetrics jvmCompilationMetrics() {
        return new JvmCompilationMetrics();
    }   

    @Bean
    public Log4j2Metrics log4j2Metrics() {
        return new Log4j2Metrics();
    }   
    @Bean
    public ExecutorServiceMetrics executorServiceMetrics() {
        return new ExecutorServiceMetrics(OpenmrsThreadPoolHolder.threadExecutor, "threadExecutor", Arrays.asList());
    }
    @Bean
    @ConditionalOnBean(value = Cache.class)
    public GuavaCacheMetrics<String, String, Cache<String, String>> guavaCacheMetrics(Cache cache) {
        return new GuavaCacheMetrics<>(cache, "guavaCache", Arrays.asList());
    }

    //only usable in embedded tomcat
    // @Bean
    // public TomcatMetrics tomcatMetrics(MeterRegistry registry) {
    //     return new TomcatMetrics("tomcat", Arrays.asList());
    // }

    @Bean
    public TomcatJMXMetrics tomcatJmxMetrics() {
        return new TomcatJMXMetrics();
    }

    @Bean
    public Gauge activeThreadGauge(MeterRegistry registry) {
        return Gauge.builder("jvm.threads.active", Thread::activeCount)
                    .description("Number of active threads in the JVM")
                    .register(registry);
    }
    @Bean
    public Gauge customHeapUsageGauge(MeterRegistry registry) {
        return Gauge.builder("jvm.memory.heap.usage", () -> Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
                    .description("Heap memory usage")
                    .baseUnit("bytes")
                    .register(registry);
    }
}