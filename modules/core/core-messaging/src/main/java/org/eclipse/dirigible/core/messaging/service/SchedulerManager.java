/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.messaging.service;

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
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.messaging.definition.ListenerDefinition;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class MessagingManager.
 */
public class SchedulerManager {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerManager.class);

	/** The Constant CONNECTOR_URL_ATTACH. */
	static final String CONNECTOR_URL_ATTACH = "vm://localhost?create=false";

	/** The Constant CONNECTOR_URL. */
	private static final String CONNECTOR_URL = "vm://localhost";

	/** The Constant LOCATION_TEMP_STORE. */
	private static final String LOCATION_TEMP_STORE = "./target/temp/kahadb";

	private DataSource dataSource = null;
	
	private IRepository repository = null;

	private static BrokerService broker;

	private static Map<String, MessagingConsumer> LISTENERS = Collections.synchronizedMap(new HashMap<String, MessagingConsumer>());
	
	protected synchronized DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = (DataSource) StaticObjects.get(StaticObjects.SYSTEM_DATASOURCE);
		}
		return dataSource;
	}
	
	protected synchronized IRepository getRepository() {
		if (repository == null) {
			repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
		}
		return repository;
	}

	/**
	 * Initialize.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public void initialize() throws Exception {
		synchronized (SchedulerManager.class) {
			if (broker == null) {
				broker = new BrokerService();
				Configuration.loadModuleConfig("/dirigible-messaging.properties");
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

	/**
	 * Shutdown all registered listeners.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public static void shutdown() throws Exception {
		for (MessagingConsumer consumer : LISTENERS.values()) {
			consumer.stop();
		}
		if (broker != null) {
			broker.stop();
		}
	}

	/**
	 * Gets the broker service.
	 *
	 * @return the broker service
	 */
	public BrokerService getBrokerService() {
		return broker;
	}

	/**
	 * Adds listener.
	 *
	 * @param listener
	 *            the listener
	 */
	public void startListener(ListenerDefinition listener) {
		if (!LISTENERS.keySet().contains(listener.getLocation())) {
			IResource resource = getRepository().getResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + IRepositoryStructure.SEPARATOR + listener.getHandler());
			if (!resource.exists()) {
				if (logger.isErrorEnabled()) {logger.error("Listener {} cannot be started, because the handler {} does not exist!", listener.getLocation(), listener.getHandler());}
			}
			MessagingConsumer consumer = new MessagingConsumer(listener.getName(), listener.getType(), listener.getHandler(), 1000);
			Thread consumerThread = new Thread(consumer);
			consumerThread.setDaemon(false);
			consumerThread.start();
			LISTENERS.put(listener.getLocation(), consumer);
			if (logger.isInfoEnabled()) {logger.info("Listener started: " + listener.getLocation());}
		} else {
			if (logger.isWarnEnabled()) {logger.warn(format("Message consumer for listener at [{0}] already running!", listener.getLocation()));}
		}
	}

	/**
	 * Remove listener.
	 *
	 * @param listener
	 *            the listener
	 */
	public void stopListener(ListenerDefinition listener) {
		MessagingConsumer consumer = LISTENERS.get(listener.getLocation());
		if (consumer != null) {
			consumer.stop();
			LISTENERS.remove(listener.getLocation());
			if (logger.isInfoEnabled()) {logger.info("Listener stopped: " + listener.getLocation());}
		} else {
			if (logger.isWarnEnabled()) {logger.warn(format("There is no a message consumer for listener at [{0}] running!", listener.getLocation()));}
		}
	}

	/**
	 * Check if listener is registered.
	 *
	 * @param listenerLocation
	 *            the listener location
	 * @return true, if such listener is registered
	 */
	public boolean existsListener(String listenerLocation) {
		return LISTENERS.keySet().contains(listenerLocation);
	}

	/**
	 * Gets the running listeners.
	 *
	 * @return the running listeners
	 */
	public List<String> getRunningListeners() {
		List<String> result = new ArrayList<String>();
		result.addAll(LISTENERS.keySet());
		return result;
	}

}
