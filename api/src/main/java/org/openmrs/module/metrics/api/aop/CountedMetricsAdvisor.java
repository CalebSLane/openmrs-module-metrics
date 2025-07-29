package org.openmrs.module.metrics.api.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aopalliance.aop.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;

import io.micrometer.core.annotation.Counted;

public class CountedMetricsAdvisor extends StaticMethodMatcherPointcutAdvisor {

	private static final Logger log = LoggerFactory.getLogger(CountedMetricsAdvisor.class);
	private CountAdvice countAdvice;

	public CountedMetricsAdvisor(CountAdvice countAdvice) {
		this.countAdvice = countAdvice;
	}

	public boolean matches(Method method, Class targetClass) {
		for (Annotation annotation : method.getAnnotations()) {
			if (annotation.annotationType().getName().equals(Counted.class.getName())) {
				log.debug("Method {} is annotated with @Counted", method.getName());
				return true;
			}
		}

		return false;
	}

	@Override
	public Advice getAdvice() {
		log.debug("Getting new around advice");
		return countAdvice;
	}

}
