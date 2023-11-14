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

@ExtendWith(MockitoExtension.class)
class BackgroundListenersManagerTest {

    private static final String HANDLER = "test-handler";
    private static final String LISTNER_LOCATION = "my-listener";

    @InjectMocks
    private BackgroundListenersManager listenersManager;

    @Mock
    private IRepository repository;

    @Mock
    private BackgroundListenerManagerFactory messageListenerManagerFactory;

    @Mock
    private Listener listener;

    @Mock
    private IResource resource;

    @Mock
    private BackgroundListenerManager listenerManager;

    @Mock
    private BackgroundListenerManager listenerManager2;

    @BeforeEach
    void setUp() {
        BackgroundListenersManager.LISTENERS.clear();
        lenient().when(listener.getLocation())
                 .thenReturn(LISTNER_LOCATION);
    }

    @Test
    void testStartListenerOnTryToStartTheListnerAgain() {
        testStartListenerOnExistingListener();

        listenersManager.startListener(listener);

        verifyNoMoreInteractions(listenerManager);
    }

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

    @Test
    void testStartListenerOnMissingListener() {
        when(listener.getHandler()).thenReturn(HANDLER);
        when(repository.getResource(
                IRepositoryStructure.PATH_REGISTRY_PUBLIC + IRepositoryStructure.SEPARATOR + HANDLER)).thenReturn(resource);
        when(resource.exists()).thenReturn(false);

        listenersManager.startListener(listener);

        verifyNoInteractions(messageListenerManagerFactory);
    }

    @Test
    void testStopListener() {
        BackgroundListenersManager.LISTENERS.put(LISTNER_LOCATION, listenerManager);

        listenersManager.stopListener(listener);
        listenersManager.stopListener(listener);

        // stop is called only once
        verify(listenerManager).stopListener();
    }

    @Test
    void testStopListeners() {
        BackgroundListenersManager.LISTENERS.put(LISTNER_LOCATION, listenerManager);
        BackgroundListenersManager.LISTENERS.put("my-listener2", listenerManager2);

        listenersManager.stopListeners();
        listenersManager.stopListeners();

        // stop is called only once
        verify(listenerManager).stopListener();
        verify(listenerManager2).stopListener();
    }
}
