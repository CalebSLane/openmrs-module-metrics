package org.openmrs.module.metrics;

import java.io.File;
import java.util.Arrays;

import org.openmrs.module.metrics.api.annotation.ConditionalOnBean;
import org.openmrs.module.metrics.api.meters.TomcatJMXMetrics;
import org.openmrs.util.OpenmrsThreadPoolHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.cache.Cache;

import groovy.util.logging.Slf4j;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
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
    public MeterBinder diskSpaceMetrics() {
        return new DiskSpaceMetrics(new File("."));
    }

    @Bean
    public MeterBinder fileDescriptorMetrics() {
        return new FileDescriptorMetrics();
    }

    @Bean
    public MeterBinder processorMetrics() {
        return new ProcessorMetrics();
    }

    @Bean
    public MeterBinder uptimeMetrics() {
        return new UptimeMetrics();
    }

    @Bean
    public MeterBinder classLoaderMetrics() {
        return new ClassLoaderMetrics();
    }

    @Bean
    public MeterBinder jvmMemoryMetrics() {
        return new JvmMemoryMetrics();
    }   

    @Bean
    public MeterBinder jvmGcMetrics() {
        return new JvmGcMetrics();
    }

    @Bean
    public MeterBinder jvmThreadMetrics() {
        return new JvmThreadMetrics();
    }   

    @Bean   
    public MeterBinder jvmThreadDeadlockMetrics() {
        return new JvmThreadDeadlockMetrics();
    }   

    @Bean
    public MeterBinder jvmHeapPressureMetrics() {
        return new JvmHeapPressureMetrics();
    }

    @Bean
    public MeterBinder jvmInfoMetrics() {
        return new JvmInfoMetrics();
    }   

    @Bean
    public MeterBinder jvmCompilationMetrics() {
        return new JvmCompilationMetrics();
    }   

    @Bean
    public MeterBinder log4j2Metrics() {
        return new Log4j2Metrics();
    }   

    @Bean
    public MeterBinder executorServiceMetrics() {
        return new ExecutorServiceMetrics(OpenmrsThreadPoolHolder.threadExecutor, "threadExecutor", Arrays.asList());
    }

    @Bean
    @ConditionalOnBean(value = Cache.class)
    public MeterBinder guavaCacheMetrics(Cache cache) {
        return new GuavaCacheMetrics<>(cache, "guavaCache", Arrays.asList());
    }

    //only usable in embedded tomcat
    // @Bean
    // public TomcatMetrics tomcatMetrics(MeterRegistry registry) {
    //     return new TomcatMetrics("tomcat", Arrays.asList());
    // }

    @Bean
    public MeterBinder tomcatJmxMetrics() {
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