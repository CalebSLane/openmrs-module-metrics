package org.openmrs.module.metrics.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import org.springframework.context.annotation.Conditional;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Conditional(BeanCondition.class)
public @interface ConditionalOnBean {
    Class<?> value();
}

