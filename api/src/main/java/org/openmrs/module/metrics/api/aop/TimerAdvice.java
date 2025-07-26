/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.metrics.api.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

/**
 * This class provides the log4j aop around advice for our service layer. This advice is placed on
 * all services and daos via the spring application context. See
 * /metadata/api/spring/applicationContext.xml
 */
public class TimerAdvice implements MethodInterceptor {

	private MeterRegistry registry;

	private final Logger log = LoggerFactory.getLogger(TimerAdvice.class);

	public TimerAdvice(MeterRegistry registry) {
		this.registry = registry;
	}
	
	/**
	 * This method prints out trace statements for getters and debug statements for everything else
	 * ("setters"). If debugging is turned on, execution time for each method is printed as well.
	 * This method is called for every method in the Class/Service that it is wrapped around. This
	 * method should be fairly quick and light.
	 *
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
 		Timed timedAnnotation = null;
		timedAnnotation = invocation.getMethod().getAnnotation(Timed.class);
		final String timerName;

		if ((timedAnnotation.value().equals("") && invocation.getMethod().getDeclaringClass().getName().endsWith("Controller"))
			|| timedAnnotation.value().equals("http.server.requests")) {
			timerName = "http.server.requests";
		} else if ((timedAnnotation.value().equals("") && !invocation.getMethod().getDeclaringClass().getName().endsWith("Controller"))) {
			timerName ="method.execution.time";
		} else {
			timerName = timedAnnotation.value();
		}
		log.debug("Method {} is annotated with @Timed, applying metrics advice", invocation.getMethod().getName());
		log.debug("Timer name : {}", timerName);

		final Timer.Sample sample = Timer.start(registry);
		String exceptionClass = "none"; 

		try { 
			return invocation.proceed(); 
		} catch (Exception ex) { 
			exceptionClass = ex.getClass().getSimpleName(); 
			throw ex; 
		} finally { 
			try { 
				sample.stop(Timer.builder(timerName) 
                     .description(timedAnnotation.description().isEmpty() ? null : timedAnnotation.description()) 
                     .tags(timedAnnotation.extraTags()) 
                     .tags("exception", exceptionClass) 
                     .publishPercentileHistogram(timedAnnotation.histogram()) 
                     .publishPercentiles(timedAnnotation.percentiles().length == 0 ? null : timedAnnotation.percentiles()) 
                     .register(registry)); 
			} catch (Exception e) { 
				// ignoring on purpose 
			} 
		}
	}
}
