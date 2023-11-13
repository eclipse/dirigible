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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.dirigible.components.listeners.domain.Listener;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("ListenersManager")
public class BackgroundListenersManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackgroundListenersManager.class);

    private static Map<String, BackgroundListenerManager> LISTENERS = Collections.synchronizedMap(new HashMap<>());

    private final IRepository repository;
    private final BackgroundListenerManagerFactory messageListenerManagerFactory;

    @Autowired
    BackgroundListenersManager(IRepository repository, BackgroundListenerManagerFactory messageListenerManagerFactory) {
        this.repository = repository;
        this.messageListenerManagerFactory = messageListenerManagerFactory;
    }

    public IRepository getRepository() {
        return repository;
    }

    public void startListener(Listener listener) {
        if (!LISTENERS.containsKey(listener.getLocation())) {
            IResource resource = getRepository().getResource(
                    IRepositoryStructure.PATH_REGISTRY_PUBLIC + IRepositoryStructure.SEPARATOR + listener.getHandler());
            if (!resource.exists()) {
                LOGGER.error("Listener {} cannot be started, because the handler {} does not exist!", listener.getLocation(),
                        listener.getHandler());
            }
            BackgroundListenerManager listenerManager = messageListenerManagerFactory.create(listener);
            listenerManager.startListener();

            LISTENERS.put(listener.getLocation(), listenerManager);
            LOGGER.info("Listener started: " + listener.getLocation());
        } else {
            LOGGER.warn(format("Message consumer for listener at [{0}] already running!", listener.getLocation()));
        }
    }

    public void stopListener(Listener listener) {
        BackgroundListenerManager listenerManager = LISTENERS.get(listener.getLocation());
        if (listenerManager != null) {
            listenerManager.stopListener();
            LISTENERS.remove(listener.getLocation());
        } else {
            LOGGER.warn("There is no a message consumer for listener [{}] running!", listener);
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
