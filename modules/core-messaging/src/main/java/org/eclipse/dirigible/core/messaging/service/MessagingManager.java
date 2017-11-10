/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.messaging.service;

import static java.text.MessageFormat.format;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.store.PListStore;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.store.jdbc.JDBCPersistenceAdapter;
import org.apache.activemq.store.kahadb.plist.PListStoreImpl;
import org.eclipse.dirigible.core.messaging.api.DestinationType;
import org.eclipse.dirigible.core.messaging.definition.ListenerDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessagingManager {

	private static final Logger logger = LoggerFactory.getLogger(MessagingManager.class);

	static final String CONNECTOR_URL = "vm://localhost";

	static final String CONNECTOR_URL_ATTACH = "vm://localhost?create=false";

	static final String LOCATION_TEMP_STORE = "./target/temp/kahadb";

	@Inject
	private DataSource dataSource;

	private static BrokerService broker;

	private static Map<String, MessagingConsumer> LISTENERS = Collections.synchronizedMap(new HashMap<String, MessagingConsumer>());

	public void initialize() throws Exception {
		synchronized (MessagingManager.class) {
			if (broker == null) {
				broker = new BrokerService();
				PersistenceAdapter persistenceAdapter = new JDBCPersistenceAdapter(dataSource, new OpenWireFormat());
				broker.setPersistenceAdapter(persistenceAdapter);
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

	public static void shutdown() throws Exception {
		for (MessagingConsumer consumer : LISTENERS.values()) {
			consumer.stop();
		}
		if (broker != null) {
			broker.stop();
		}
	}

	public BrokerService getBrokerService() {
		return broker;
	}

	public void startListener(ListenerDefinition listener) {
		if (!LISTENERS.keySet().contains(listener.getLocation())) {
			MessagingConsumer consumer = new MessagingConsumer(listener.getName(), DestinationType.values()[listener.getType()], listener.getModule(),
					1000);
			Thread consumerThread = new Thread(consumer);
			consumerThread.setDaemon(false);
			consumerThread.start();
			LISTENERS.put(listener.getLocation(), consumer);
			logger.info("Listener started: " + listener.getLocation());
		} else {
			logger.warn(format("Message consumer for listener at [{0}] already running!", listener.getLocation()));
		}
	}

	public void stopListener(ListenerDefinition listener) {
		MessagingConsumer consumer = LISTENERS.get(listener.getLocation());
		if (consumer != null) {
			consumer.stop();
			LISTENERS.remove(listener.getLocation());
			logger.info("Listener stopped: " + listener.getLocation());
		} else {
			logger.warn(format("There is no a message consumer for listener at [{0}] running!", listener.getLocation()));
		}
	}

	public boolean existsListener(String listenerLocation) {
		return LISTENERS.keySet().contains(listenerLocation);
	}

	public List<String> getRunningListeners() {
		List<String> result = new ArrayList<String>();
		result.addAll(LISTENERS.keySet());
		return result;
	}

}
