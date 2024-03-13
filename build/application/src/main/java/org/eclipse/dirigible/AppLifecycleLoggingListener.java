package org.eclipse.dirigible;

import org.eclipse.dirigible.components.base.ApplicationListenersOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(ApplicationListenersOrder.APP_LYFECYCLE_LOGGING_LISTENER)
@Component
class AppLifecycleLoggingListener implements ApplicationListener<ApplicationEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppLifecycleLoggingListener.class);

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            LOGGER.info("------------------------ Eclipse Dirigible started ------------------------");
        }
        if (event instanceof ContextClosedEvent) {
            LOGGER.info("------------------------ Eclipse Dirigible stopped ------------------------");
        }
    }

}
