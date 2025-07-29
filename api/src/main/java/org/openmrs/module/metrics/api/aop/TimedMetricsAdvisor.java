package org.openmrs.module.metrics.api.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aopalliance.aop.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;

import io.micrometer.core.annotation.Timed;

public class TimedMetricsAdvisor extends StaticMethodMatcherPointcutAdvisor {

	private static final Logger log = LoggerFactory.getLogger(TimedMetricsAdvisor.class);
	private TimerAdvice timerAdvice;

	public TimedMetricsAdvisor(TimerAdvice timerAdvice) {
		this.timerAdvice = timerAdvice;
	}

	public boolean matches(Method method, Class targetClass) {
		for (Annotation annotation : method.getAnnotations()) {
			if (annotation.annotationType().getName().equals(Timed.class.getName())) {
				log.debug("Method {} is annotated with @Timed", method.getName());
				return true;
			}
		}

		return false;
	}

	@Override
	public Advice getAdvice() {
		System.out.println("Getting new around advice");
		return timerAdvice;
	}

}
