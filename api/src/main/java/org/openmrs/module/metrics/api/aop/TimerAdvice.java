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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.lucene.util.automaton.Operations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Sample;

/**
 * This class provides the log4j aop around advice for our service layer. This
 * advice is placed on
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
	 * This method prints out trace statements for getters and debug statements for
	 * everything else
	 * ("setters"). If debugging is turned on, execution time for each method is
	 * printed as well.
	 * This method is called for every method in the Class/Service that it is
	 * wrapped around. This
	 * method should be fairly quick and light.
	 *
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {

		Map<Annotation, Timer.Sample> samples = new HashMap<>();

		for (Annotation annotation : invocation.getMethod().getAnnotations()) {
			if (annotation.annotationType().getName().equals(Timed.class.getName())) {
				final String timerName;
				// TODO allow for custom timer names from annotation value. currently doesn't
				// work in separate jars
				if (invocation.getMethod().getDeclaringClass().getName().endsWith("Controller")) {
					timerName = "http.server.requests";
				} else {
					timerName = "method.execution.time";
				}
				System.out.println("Method " + invocation.getMethod().getName()
						+ " is annotated with @Timed, applying metrics advice. Timer name: " + timerName);

				final Timer.Sample sample = Timer.start(registry);
				samples.put(annotation, sample);

			}
		}
		String exceptionClass = "none";

		try {
			return invocation.proceed();
		} catch (Exception ex) {
			exceptionClass = ex.getClass().getSimpleName();
			throw ex;
		} finally {
			try {
				for (Entry<Annotation, Sample> entry : samples.entrySet()) {
					// TODO allow for custom timer names, description, etc from annotation value.
					// currently doesn't work in separate jars
					Annotation timedAnnotation = entry.getKey();
					final String timerName;
					if (invocation.getMethod().getDeclaringClass().getName().endsWith("Controller")) {
						timerName = "http.server.requests";
					} else {
						timerName = "method.execution.time";
					}
					System.out.println("getting value for Timer with name: " + timerName);
					entry.getValue().stop(Timer.builder(timerName)
							// .description(descriptionValue.isEmpty() ? null : descriptionValue)
							// .tags(extraTagsValue)
							.tags("exception", exceptionClass)
							// .publishPercentileHistogram(histogramValue)
							// .publishPercentiles(
							// percentilesValue.length == 0 ? null : percentilesValue)
							.register(registry));
				}

			} catch (Exception e) {
				// ignoring on purpose
			}
		}

	}
}
