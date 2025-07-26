package org.openmrs.module.metrics.api.service;

import org.openmrs.api.OpenmrsService;

import io.micrometer.core.instrument.MeterRegistry;  

public interface MetricsService extends OpenmrsService {

	public MeterRegistry getMeterRegistry();

}
