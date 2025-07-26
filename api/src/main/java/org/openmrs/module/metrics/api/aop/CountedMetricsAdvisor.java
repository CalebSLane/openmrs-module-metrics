package org.openmrs.module.metrics.api.aop;

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
       
        Counted countedAnnotation = null;
		countedAnnotation = method.getAnnotation(Counted.class);
		if (countedAnnotation != null ) {
			log.debug("Method {} is annotated with @Counted, applying metrics advice", method.getName());
			log.debug("Counted annotation value: {}", countedAnnotation.value());
			return true;
		}

		return false;
	}

    @Override
    public Advice getAdvice() {
		log.debug("Getting new around advice");
		return countAdvice;
	}


}
