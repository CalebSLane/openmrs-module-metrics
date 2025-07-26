package org.openmrs.module.metrics.api.service.impl;

import org.openmrs.module.metrics.api.service.MetricsService;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;

@Component("metricsService")
public class MetricsServiceImpl implements MetricsService {

    private final MeterRegistry meterRegistry;

    public MetricsServiceImpl(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public MeterRegistry getMeterRegistry() {
        return meterRegistry;
    }

    @Override
    public void onStartup() {
    }

    @Override
    public void onShutdown() {
    }
}
