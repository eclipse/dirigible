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
package org.eclipse.dirigible.components.listeners.service;

import static java.text.MessageFormat.format;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.store.PListStore;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.store.jdbc.JDBCPersistenceAdapter;
import org.apache.activemq.store.kahadb.plist.PListStoreImpl;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.listeners.domain.Listener;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ListenersManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListenersManager.class);

    static final String CONNECTOR_URL_ATTACH = "vm://localhost?create=false";

    private static final String CONNECTOR_URL = "vm://localhost";

    private static final String LOCATION_TEMP_STORE = "./target/temp/kahadb";

    private static BrokerService broker;

    private static Map<String, MessageListenerManager> LISTENERS = Collections.synchronizedMap(new HashMap<>());

    private final DataSource dataSource;
    private final IRepository repository;
    private final MessageListenerManagerFactory messageListenerManagerFactory;

    public ListenersManager(@Qualifier("SystemDB") DataSource dataSource, IRepository repository,
            MessageListenerManagerFactory messageListenerManagerFactory) {
        this.dataSource = dataSource;
        this.repository = repository;
        this.messageListenerManagerFactory = messageListenerManagerFactory;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public IRepository getRepository() {
        return repository;
    }

    public void initialize() throws Exception {
        synchronized (ListenersManager.class) {
            if (broker == null) {
                broker = new BrokerService();
                if (Boolean.parseBoolean(Configuration.get("DIRIGIBLE_MESSAGING_USE_DEFAULT_DATABASE", "true"))) {
                    PersistenceAdapter persistenceAdapter = new JDBCPersistenceAdapter(getDataSource(), new OpenWireFormat());
                    broker.setPersistenceAdapter(persistenceAdapter);
                }
                broker.setPersistent(true);
                broker.setUseJmx(false);
                // broker.setUseShutdownHook(false);
                PListStore pListStore = new PListStoreImpl();
                pListStore.setDirectory(new File(LOCATION_TEMP_STORE));
                broker.setTempDataStore(pListStore);
                broker.addConnector(CONNECTOR_URL);
                broker.start();
            }
        }
    }

    public BrokerService getBrokerService() {
        return broker;
    }

    public void startListener(Listener listener) {
        if (!LISTENERS.containsKey(listener.getLocation())) {
            IResource resource = getRepository().getResource(
                    IRepositoryStructure.PATH_REGISTRY_PUBLIC + IRepositoryStructure.SEPARATOR + listener.getHandler());
            if (!resource.exists()) {
                LOGGER.error("Listener {} cannot be started, because the handler {} does not exist!", listener.getLocation(),
                        listener.getHandler());
            }
            MessageListenerManager listenerManager = messageListenerManagerFactory.create(listener);
            listenerManager.startListener();

            LISTENERS.put(listener.getLocation(), listenerManager);
            LOGGER.info("Listener started: " + listener.getLocation());
        } else {
            LOGGER.warn(format("Message consumer for listener at [{0}] already running!", listener.getLocation()));
        }
    }

    public void stopListener(Listener listener) {
        MessageListenerManager listenerManager = LISTENERS.get(listener.getLocation());
        if (listenerManager != null) {
            listenerManager.stop();
            LISTENERS.remove(listener.getLocation());
            LOGGER.info("Listener stopped: " + listener.getLocation());
        } else {
            LOGGER.warn(format("There is no a message consumer for listener at [{0}] running!", listener.getLocation()));
        }
    }

    /**
     * Check if listener is registered.
     *
     * @param listenerLocation the listener location
     * @return true, if such listener is registered
     */
    public boolean existsListener(String listenerLocation) {
        return LISTENERS.containsKey(listenerLocation);
    }

    /**
     * Gets the running listeners.
     *
     * @return the running listeners
     */
    public List<String> getRunningListeners() {
        List<String> result = new ArrayList<>();
        result.addAll(LISTENERS.keySet());
        return result;
    }

}
