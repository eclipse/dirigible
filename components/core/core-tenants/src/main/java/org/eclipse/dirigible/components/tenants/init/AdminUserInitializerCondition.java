package org.eclipse.dirigible.components.tenants.init;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

class AdminUserInitializerCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        return "true".equals(env.getProperty("basic.enabled")) || env.acceptsProfiles("snowflake");
    }
}
