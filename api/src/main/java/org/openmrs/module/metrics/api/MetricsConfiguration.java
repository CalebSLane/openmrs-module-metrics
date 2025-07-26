/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.metrics.api;

import java.util.Set;

import org.openmrs.module.metrics.api.aop.CountAdvice;
import org.openmrs.module.metrics.api.aop.CountedMetricsAdvisor;
import org.openmrs.module.metrics.api.aop.TimedMetricsAdvisor;
import org.openmrs.module.metrics.api.aop.TimerAdvice;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.micrometer.prometheusmetrics.PrometheusRenameFilter;

@Configuration
public class MetricsConfiguration {

    // @Bean
    // public CompositeMeterRegistry meterRegistry(Set<MeterBinder> binders) {
    //     CompositeMeterRegistry compositeMeterRegistry = new CompositeMeterRegistry();
    //     compositeMeterRegistry.add(prometheusMeterRegistry());
    //     binders.forEach(binder -> binder.bindTo(compositeMeterRegistry));

    //     return compositeMeterRegistry;
    // }

    // @Bean
    // public TimedAspect timedAspect(MeterRegistry registry) {
    //     return new TimedAspect(registry);
    // }

    @Bean
    public TimedMetricsAdvisor timedMetricsAdvisor(MeterRegistry registry) {
       return new TimedMetricsAdvisor(new TimerAdvice(registry));
    }

    @Bean
    public CountedMetricsAdvisor countedMetricsAdvisor(MeterRegistry registry) {
       return new CountedMetricsAdvisor(new CountAdvice(registry));
    }

    // @Bean
    // public CountedAspect countedAspect(MeterRegistry registry) {
    //     return new CountedAspect(registry);
    // }

    // Timer to measure execution time of http requests
    @Bean("httpRequestExecutionTimer")
    public Timer getHttpRequestExecutionTimer(MeterRegistry registry) {
        return registry.timer("http.server.requests", "exception", "none");
    }

    // Timer to measure execution time of methods
    @Bean("methodExecutionTimer")
    public Timer getMethodExecutionTimer(MeterRegistry registry) {
        return registry.timer("method.execution.time", "exception", "none");
    }

    // // Counter to measure execution count of methods
    // @Bean("methodExecutionCounter")
    // public Counter getMethodExecutionCounter(MeterRegistry registry) {
    //     return registry.counter("method.execution.count");
    // }

    @Bean
    public PrometheusMeterRegistry prometheusMeterRegistry(Set<MeterBinder> binders) {
        PrometheusMeterRegistry registry= new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        registry.config().meterFilter(new PrometheusRenameFilter());
        registry.config().commonTags("application", "openmrs-backend");
        binders.forEach(binder -> binder.bindTo(registry));
        return registry;
    }

}