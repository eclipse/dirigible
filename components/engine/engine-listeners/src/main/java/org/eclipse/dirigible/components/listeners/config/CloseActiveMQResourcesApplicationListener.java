/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.listeners.config;

import javax.jms.Connection;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

@Component
class CloseActiveMQResourcesApplicationListener implements ApplicationListener<ApplicationEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloseActiveMQResourcesApplicationListener.class);

    private final BrokerService broker;
    private final Connection connection;
    private final Session session;

    @Autowired
    CloseActiveMQResourcesApplicationListener(BrokerService broker, @Qualifier("ActiveMQConnection") Connection connection,
            @Qualifier("ActiveMQSession") Session session) {
        this.broker = broker;
        this.connection = connection;
        this.session = session;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (isApplicableEvent(event)) {
            closeResources(event);
        }
    }

    private boolean isApplicableEvent(ApplicationEvent event) {
        return event instanceof ContextStoppedEvent || event instanceof ContextClosedEvent;
    }

    private void closeResources(ApplicationEvent event) {
        if (broker.isStopped()) {
            LOGGER.info("ActiveMQ resources already closed");
            return;
        }

        LOGGER.info("Closing ActiveMQ resources due to event {}", event);
        closeResource(session);
        closeResource(connection);
        stopBroker();
    }

    private void closeResource(AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (Exception ex) {
            LOGGER.warn("Failed to close " + closeable, ex);
        }
    }

    private void stopBroker() {
        try {
            broker.stop();
        } catch (Exception ex) {
            LOGGER.warn("Failed to close broker ", ex);
        }
    }
}
