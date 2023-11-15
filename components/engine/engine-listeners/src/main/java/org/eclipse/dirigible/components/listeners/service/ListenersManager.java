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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.dirigible.components.listeners.domain.Listener;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class ListenersManager.
 */
@Component("ListenersManager")
public class ListenersManager {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ListenersManager.class);

    /** The listeners. */
    static Map<String, ListenerManager> LISTENERS = Collections.synchronizedMap(new HashMap<>());

    /** The repository. */
    private final IRepository repository;

    /** The message listener manager factory. */
    private final ListenerManagerFactory messageListenerManagerFactory;

    /**
     * Instantiates a new listeners manager.
     *
     * @param repository the repository
     * @param messageListenerManagerFactory the message listener manager factory
     */
    @Autowired
    public ListenersManager(IRepository repository, ListenerManagerFactory messageListenerManagerFactory) {
        this.repository = repository;
        this.messageListenerManagerFactory = messageListenerManagerFactory;
    }

    /**
     * Start listener.
     *
     * @param listener the listener
     */
    public void startListener(Listener listener) {
        if (LISTENERS.containsKey(listener.getLocation())) {
            LOGGER.warn(format("Message consumer for listener at [{0}] already running!", listener.getLocation()));
            return;

        }
        if (isMissingListener(listener)) {
            LOGGER.error("Listener {} cannot be started, because the handler {} does not exist!", listener.getLocation(),
                    listener.getHandler());
            return;
        }
        ListenerManager listenerManager = messageListenerManagerFactory.create(listener);
        listenerManager.startListener();

        LISTENERS.put(listener.getLocation(), listenerManager);
        LOGGER.info("Listener started: " + listener.getLocation());
    }

    /**
     * Checks if is missing listener.
     *
     * @param listener the listener
     * @return true, if is missing listener
     */
    private boolean isMissingListener(Listener listener) {
        IResource resource =
                repository.getResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + IRepositoryStructure.SEPARATOR + listener.getHandler());
        return !resource.exists();
    }

    /**
     * Stop listeners.
     */
    public synchronized void stopListeners() {
        LOGGER.info("Stopping all background listeners...");

        Iterator<Entry<String, ListenerManager>> iterator = LISTENERS.entrySet()
                                                                     .iterator();
        while (iterator.hasNext()) {
            Entry<String, ListenerManager> entry = iterator.next();
            ListenerManager listenerManager = entry.getValue();
            if (listenerManager != null) {
                listenerManager.stopListener();
            }
            iterator.remove();
        }
    }

    /**
     * Stop listener.
     *
     * @param listener the listener
     */
    public void stopListener(Listener listener) {
        String listenerLocation = listener.getLocation();
        ListenerManager listenerManager = LISTENERS.get(listenerLocation);
        if (listenerManager != null) {
            listenerManager.stopListener();
            LISTENERS.remove(listenerLocation);
        } else {
            LOGGER.warn("There is NO configured listener for [{}]", listenerLocation);
        }
    }

}
