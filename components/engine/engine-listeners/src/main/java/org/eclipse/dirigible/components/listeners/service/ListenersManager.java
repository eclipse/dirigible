/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.listeners.service;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The Class ListenersManager.
 */
@Component("ListenersManager")
public class ListenersManager {

    /** The listeners. */
    static final Map<ListenerDescriptor, ListenerManager> LISTENERS = Collections.synchronizedMap(new HashMap<>());

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ListenersManager.class);

    /** The repository. */
    private final IRepository repository;

    /** The message listener manager factory. */
    private final ListenerManagerFactory messageListenerManagerFactory;

    /** The listener creator. */
    private final ListenerCreator listenerCreator;

    /** The connection the artefacts share, closed once every listener is stopped. */
    private final SharedListenerConnection sharedConnection;

    /**
     * Instantiates a new listeners manager.
     *
     * @param repository the repository
     * @param messageListenerManagerFactory the message listener manager factory
     * @param listenerCreator the listener creator
     * @param sharedConnection the connection the artefacts share in the shared mode
     */
    @Autowired
    public ListenersManager(IRepository repository, ListenerManagerFactory messageListenerManagerFactory, ListenerCreator listenerCreator,
            SharedListenerConnection sharedConnection) {
        this.repository = repository;
        this.messageListenerManagerFactory = messageListenerManagerFactory;
        this.listenerCreator = listenerCreator;
        this.sharedConnection = sharedConnection;
    }

    /**
     * Start listener.
     *
     * @param listenerEntity the listener
     */
    public void startListener(org.eclipse.dirigible.components.listeners.domain.Listener listenerEntity) {
        ListenerDescriptor listenerDescriptor = listenerCreator.fromEntity(listenerEntity);
        if (LISTENERS.containsKey(listenerDescriptor)) {
            if (DestinationNameManager.isGlobal(listenerEntity.getName())) {
                // Every provisioned tenant reaches this artefact, and a global destination resolves to
                // one physical name for all of them - so the tenants after the first share the single
                // subscription that is already open. Expected, not a misconfiguration.
                LOGGER.debug("Listener [{}] is bound to a global destination and is already running for the whole deployment.",
                        listenerDescriptor);
            } else {
                LOGGER.warn("Message consumer for listener [{}] already running!", listenerDescriptor);
            }
            return;

        }
        if (isMissingHandler(listenerDescriptor)) {
            // Thrown, not logged and swallowed: a normal return had the synchronizer record the
            // artefact as CREATED and running with nothing subscribed (#7248). The handler may
            // simply be published on a later pass, and FAILED is what keeps the listener retried.
            throw new IllegalStateException("Listener " + listenerDescriptor + " cannot be started, because the handler ["
                    + listenerDescriptor.getHandlerPath() + "] does not exist");
        }
        ListenerManager listenerManager = messageListenerManagerFactory.create(listenerDescriptor);
        listenerManager.startListener();

        LISTENERS.put(listenerDescriptor, listenerManager);
        LOGGER.info("Listener [{}] started.", listenerDescriptor);
    }

    /**
     * Checks if is missing handler.
     *
     * @param listenerDescriptor the listener
     * @return true, if is missing handler
     */
    private boolean isMissingHandler(ListenerDescriptor listenerDescriptor) {
        IResource resource = repository.getResource(
                IRepositoryStructure.PATH_REGISTRY_PUBLIC + IRepositoryStructure.SEPARATOR + listenerDescriptor.getHandlerPath());
        return !resource.exists();
    }

    /**
     * Stop listeners.
     */
    public synchronized void stopListeners() {
        LOGGER.info("Stopping all background listeners...");

        LISTENERS.forEach((l, m) -> {
            if (null != m) {
                m.stopListener();
            }
        });
        LISTENERS.clear();
        sharedConnection.close();
    }

    /**
     * Stop listener.
     *
     * @param listenerEntity the listener
     */
    /**
     * Stops every listener subscribed to one of the tenant's destinations ({@code <tenantId>###<name>})
     * - the tenant's deprovisioning. The destinations themselves stay on the broker.
     *
     * @param tenantId the tenant id
     * @return how many listeners were stopped
     */
    public int stopTenantListeners(String tenantId) {
        String prefix = tenantId + "###";
        List<ListenerManager> stopped = new ArrayList<>();
        synchronized (LISTENERS) {
            Iterator<Map.Entry<ListenerDescriptor, ListenerManager>> entries = LISTENERS.entrySet()
                                                                                        .iterator();
            while (entries.hasNext()) {
                Map.Entry<ListenerDescriptor, ListenerManager> entry = entries.next();
                if (entry.getKey()
                         .getDestination()
                         .startsWith(prefix)) {
                    stopped.add(entry.getValue());
                    entries.remove();
                }
            }
        }
        for (ListenerManager manager : stopped) {
            if (manager != null) {
                manager.stopListener();
            }
        }
        return stopped.size();
    }

    public void stopListener(org.eclipse.dirigible.components.listeners.domain.Listener listenerEntity) {
        ListenerDescriptor listenerDescriptor = listenerCreator.fromEntity(listenerEntity);
        ListenerManager listenerManager = LISTENERS.get(listenerDescriptor);
        if (listenerManager != null) {
            listenerManager.stopListener();
            LISTENERS.remove(listenerDescriptor);
        } else {
            LOGGER.warn("There is NO configured listener for [{}]", listenerDescriptor);
        }
    }

}
