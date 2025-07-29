package org.openmrs.module.metrics.api.service.impl;

import org.openmrs.module.metrics.api.service.MetricsService;
import org.springframework.stereotype.Component;


@Component("metricsService")
public class MetricsServiceImpl implements MetricsService {

    @Override
    public void onStartup() {
    }

    @Override
    public void onShutdown() {
    }
}
