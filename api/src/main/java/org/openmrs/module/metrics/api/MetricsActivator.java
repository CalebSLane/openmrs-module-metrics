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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.ModuleException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;


/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
/**
 * {@link ModuleActivator} for the webservices.rest module
 */
@Component
public class MetricsActivator extends BaseModuleActivator implements ApplicationContextAware, DaemonTokenAware {
	
	private static ConfigurableApplicationContext applicationContext;

	private static DaemonToken daemonToken;

	private Log log = LogFactory.getLog(this.getClass());


	@Override
	public void started() {
		if (applicationContext == null) {
			throw new ModuleException("Cannot load metrics module as the main application context is not available");
		}
		applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
		
		log.info("Started the metrics Service module");
	}
	
	@Override
	public void stopped() {
		log.info("Stopped the metrics Service module");
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		MetricsActivator.applicationContext = (ConfigurableApplicationContext) applicationContext;
	}
	
	@Override
	public void setDaemonToken(DaemonToken token) {
		this.daemonToken = token;
	}
}