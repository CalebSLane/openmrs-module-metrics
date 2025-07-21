/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.metrics;

import java.io.File;
import java.util.Arrays;

import org.openmrs.util.OpenmrsThreadPoolHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Tag;
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
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

@Configuration
public class MetricsConfiguration {

    @Bean
    public CompositeMeterRegistry meterRegistry() {
        CompositeMeterRegistry compositeMeterRegistry = new CompositeMeterRegistry();
        compositeMeterRegistry.add(prometheusMeterRegistry());
        return compositeMeterRegistry;
    }

    public PrometheusMeterRegistry prometheusMeterRegistry() {
        PrometheusMeterRegistry registry= new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        registry.config().commonTags("application", "openmrs-backend");
        registerCommonMetrics(registry);
        return registry;
    } 

	private void registerCommonMetrics(PrometheusMeterRegistry meterRegistry) {
		new ClassLoaderMetrics().bindTo(meterRegistry);
        new JvmMemoryMetrics().bindTo(meterRegistry);
        new JvmGcMetrics().bindTo(meterRegistry);
        new ProcessorMetrics().bindTo(meterRegistry);
        new JvmThreadMetrics().bindTo(meterRegistry);
        new JvmThreadDeadlockMetrics().bindTo(meterRegistry);
        new Log4j2Metrics().bindTo(meterRegistry);
        new UptimeMetrics().bindTo(meterRegistry);
        new DiskSpaceMetrics(new File("/")).bindTo(meterRegistry);
        // new PostgreSQLDatabaseMetrics(dataSource, OpenmrsConstants.DATABASE_NAME).bindTo(meterRegistry);
        // new TomcatMetrics(getManager(context), Arrays.asList(Tag.of("tomcat-metrics", "openmrs-backend"))).bindTo(meterRegistry);
        new ExecutorServiceMetrics(OpenmrsThreadPoolHolder.threadExecutor, "threadExecutor", Arrays.asList(Tag.of("thread-executor", "openmrs-backend"))).bindTo(meterRegistry);
        new JvmHeapPressureMetrics().bindTo(meterRegistry);
        new JvmInfoMetrics().bindTo(meterRegistry);
        new JvmCompilationMetrics().bindTo(meterRegistry);
	}


}