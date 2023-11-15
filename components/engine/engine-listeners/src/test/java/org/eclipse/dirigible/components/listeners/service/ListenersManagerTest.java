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

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.eclipse.dirigible.components.listeners.domain.Listener;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The Class BackgroundListenersManagerTest.
 */
@ExtendWith(MockitoExtension.class)
class ListenersManagerTest {

    /** The Constant HANDLER. */
    private static final String HANDLER = "test-handler";

    /** The Constant LISTNER_LOCATION. */
    private static final String LISTNER_LOCATION = "my-listener";

    /** The listeners manager. */
    @InjectMocks
    private ListenersManager listenersManager;

    /** The repository. */
    @Mock
    private IRepository repository;

    /** The message listener manager factory. */
    @Mock
    private ListenerManagerFactory messageListenerManagerFactory;

    /** The listener. */
    @Mock
    private Listener listener;

    /** The resource. */
    @Mock
    private IResource resource;

    /** The listener manager. */
    @Mock
    private ListenerManager listenerManager;

    /** The listener manager 2. */
    @Mock
    private ListenerManager listenerManager2;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        ListenersManager.LISTENERS.clear();
        lenient().when(listener.getLocation())
                 .thenReturn(LISTNER_LOCATION);
    }

    /**
     * Test start listener on try to start the listner again.
     */
    @Test
    void testStartListenerOnTryToStartTheListnerAgain() {
        testStartListenerOnExistingListener();

        listenersManager.startListener(listener);

        verifyNoMoreInteractions(listenerManager);
    }

    /**
     * Test start listener on existing listener.
     */
    @Test
    void testStartListenerOnExistingListener() {
        when(listener.getHandler()).thenReturn(HANDLER);
        when(repository.getResource(
                    IRepositoryStructure.PATH_REGISTRY_PUBLIC + IRepositoryStructure.SEPARATOR + HANDLER)).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(messageListenerManagerFactory.create(listener)).thenReturn(listenerManager);

        listenersManager.startListener(listener);

        verify(listenerManager).startListener();
    }

    /**
     * Test start listener on missing listener.
     */
    @Test
    void testStartListenerOnMissingListener() {
        when(listener.getHandler()).thenReturn(HANDLER);
        when(repository.getResource(
                IRepositoryStructure.PATH_REGISTRY_PUBLIC + IRepositoryStructure.SEPARATOR + HANDLER)).thenReturn(resource);
        when(resource.exists()).thenReturn(false);

        listenersManager.startListener(listener);

        verifyNoInteractions(messageListenerManagerFactory);
    }

    /**
     * Test stop listener.
     */
    @Test
    void testStopListener() {
        ListenersManager.LISTENERS.put(LISTNER_LOCATION, listenerManager);

        listenersManager.stopListener(listener);
        listenersManager.stopListener(listener);

        // stop is called only once
        verify(listenerManager).stopListener();
    }

    /**
     * Test stop listeners.
     */
    @Test
    void testStopListeners() {
        ListenersManager.LISTENERS.put(LISTNER_LOCATION, listenerManager);
        ListenersManager.LISTENERS.put("my-listener2", listenerManager2);

        listenersManager.stopListeners();
        listenersManager.stopListeners();

        // stop is called only once
        verify(listenerManager).stopListener();
        verify(listenerManager2).stopListener();
    }
}
