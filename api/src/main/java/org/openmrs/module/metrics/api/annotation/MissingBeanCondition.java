package org.openmrs.module.metrics.api.annotation;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;

class BeanCondition implements ConfigurationCondition {
    @Override
    public ConfigurationPhase getConfigurationPhase() {
        return ConfigurationPhase.REGISTER_BEAN;
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        var targetBeanType = metadata.getAnnotations()
                .get(ConditionalOnBean.class)
                .getValue("value", Class.class)
                // TODO throw a more informative error
                .orElseThrow(() -> new RuntimeException("Failed to evaluate BeanCondition"));

        try {
            context.getBeanFactory().getBean(targetBeanType);
        } catch (NoSuchBeanDefinitionException e) {
            return false;
        }
       
        return true;
    }
}