package org.openmrs.module.metrics.api.aop;

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
        Timed timedAnnotation = null;
		timedAnnotation = method.getAnnotation(Timed.class);
		if (timedAnnotation != null ) {
			log.debug("Method {} is annotated with @Timed, applying metrics advice", method.getName());
			log.debug("Timed annotation value: {}", timedAnnotation.value());
			return true;
		}
    

		return false;
	}

    @Override
    public Advice getAdvice() {
		log.debug("Getting new around advice");
		return timerAdvice;
	}


}
