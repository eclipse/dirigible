package org.eclipse.dirigible.components.listeners.config;

import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

@Component
class ShutdownActiveMQBrokerListener implements ApplicationListener<ApplicationEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownActiveMQBrokerListener.class);

    private final BrokerService broker;

    @Autowired
    ShutdownActiveMQBrokerListener(BrokerService broker) {
        this.broker = broker;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (isApplicableEvent(event)) {
            closeBroker(event);
        }
    }

    private boolean isApplicableEvent(ApplicationEvent event) {
        return event instanceof ContextRefreshedEvent || event instanceof ContextStoppedEvent || event instanceof ContextClosedEvent;
    }

    private void closeBroker(ApplicationEvent event) {
        try {
            if (!broker.isStopped()) {
                LOGGER.info("Stopping ActiveMQ broker due to event {}", event);
                broker.stop();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to stop ActiveMQ broker", ex);
        }
    }
}
