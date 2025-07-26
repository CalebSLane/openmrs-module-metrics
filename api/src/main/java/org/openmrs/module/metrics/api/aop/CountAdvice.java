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

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * This class provides the log4j aop around advice for our service layer. This advice is placed on
 * all services and daos via the spring application context. See
 * /metadata/api/spring/applicationContext.xml
 */
public class CountAdvice implements MethodInterceptor {

	private MeterRegistry registry;

	private final Logger log = LoggerFactory.getLogger(CountAdvice.class);

	public CountAdvice(MeterRegistry registry) {
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
 		Counted countedAnnotation = null;
		countedAnnotation = invocation.getMethod().getAnnotation(Counted.class);
		final String counterName;

		if ((countedAnnotation.value().equals("") && invocation.getMethod().getDeclaringClass().getName().endsWith("Controller"))
			|| countedAnnotation.value().equals("http.server.requests")) {
			counterName = "http.server.requests";
		} else if ((countedAnnotation.value().equals("") && !invocation.getMethod().getDeclaringClass().getName().endsWith("Controller"))) {
			counterName ="method.execution.count";
		} else {
			counterName = countedAnnotation.value();
		}
		log.debug("Method {} is annotated with @Counted, applying metrics advice", invocation.getMethod().getName());
		log.debug("Counter name : {}", counterName);

		Counter counter = registry.find(counterName).counter();
		if (counter == null) {
			log.debug("No counter found for method: {}", counterName);
			counter = Counter.builder(counterName)
					.description(countedAnnotation.description())
					.tags(countedAnnotation.extraTags())
					.register(registry);
		}
		try { 
			return invocation.proceed(); 
		} catch (Exception ex) { 
			throw ex; 
		} finally { 
			counter.increment();
		}
	}
}
