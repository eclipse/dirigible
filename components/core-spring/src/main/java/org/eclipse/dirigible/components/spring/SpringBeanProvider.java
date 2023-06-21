package org.eclipse.dirigible.components.spring;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringBeanProvider implements InitializingBean {

    private static SpringBeanProvider INSTANCE;
    private final ApplicationContext applicationContext;

    @Autowired
    public SpringBeanProvider(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        INSTANCE = this;
    }

    public static <T> T getBean(Class<T> clazz) {
        return INSTANCE.applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String className) {
        return (T)INSTANCE.applicationContext.getBean(className);
    }
}
