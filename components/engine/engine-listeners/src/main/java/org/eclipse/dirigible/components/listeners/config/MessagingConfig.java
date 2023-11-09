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

import java.io.File;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.sql.DataSource;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.store.PListStore;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.store.jdbc.JDBCPersistenceAdapter;
import org.apache.activemq.store.kahadb.plist.PListStoreImpl;
import org.eclipse.dirigible.components.engine.javascript.service.JavascriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class MessagingConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingConfig.class);

    private static final String CONNECTOR_URL_ATTACH = "vm://localhost?create=false";
    private static final String CONNECTOR_URL = "vm://localhost";
    private static final String LOCATION_TEMP_STORE = "./target/temp/kahadb";

    @Bean("ActiveMQBroker")
    BrokerService createBrokerService(@Qualifier("SystemDB") DataSource dataSource) {
        try {
            BrokerService broker = new BrokerService();
            if (Boolean.parseBoolean(
                    org.eclipse.dirigible.commons.config.Configuration.get("DIRIGIBLE_MESSAGING_USE_DEFAULT_DATABASE", "true"))) {
                PersistenceAdapter persistenceAdapter = new JDBCPersistenceAdapter(dataSource, new OpenWireFormat());
                broker.setPersistenceAdapter(persistenceAdapter);
            }
            broker.setPersistent(true);
            broker.setUseJmx(false);
            PListStore pListStore = new PListStoreImpl();
            pListStore.setDirectory(new File(LOCATION_TEMP_STORE));
            broker.setTempDataStore(pListStore);
            broker.addConnector(CONNECTOR_URL);

            broker.start();

            return broker;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to init ActiveMQ broker", ex);
        }
    }

    @Bean("ActiveMQSession")
    Session createConnection(@Qualifier("ActiveMQConnection") Connection connection) {
        try {
            return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException ex) {
            throw new IllegalStateException("Failed to create session to ActiveMQ", ex);
        }
    }

    @Bean("ActiveMQConnection")
    @DependsOn("ActiveMQBroker")
    Connection createConnection(JavascriptService javascriptService) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(CONNECTOR_URL_ATTACH);
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();

            MessageConsumerExceptionListener exceptionListener = new MessageConsumerExceptionListener(javascriptService);
            connection.setExceptionListener(exceptionListener);

            return connection;
        } catch (JMSException ex) {
            throw new IllegalStateException("Failed to create connection to ActiveMQ", ex);
        }
    }
}
