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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.dirigible.components.base.script.ScriptEngineUnavailableException;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.eclipse.dirigible.components.listeners.config.ActiveMQConnectionArtifactsFactory;
import org.eclipse.dirigible.components.listeners.config.ListenerConnectionMode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * An instance without the JavaScript engine (#8006): the listener callbacks' factory starts, and a
 * listener is refused before it asks the broker for anything - which the listener synchronizer
 * records as {@code FAILED}, with this reason.
 */
class ListenerWithoutScriptEngineTest {

    private static final String HANDLER = "project/handler.js";

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner().withBean(TenantPropertyManager.class, () -> mock(TenantPropertyManager.class))
                                          .withBean(TenantContext.class, () -> mock(TenantContext.class))
                                          .withUserConfiguration(AsynchronousMessageListenerFactory.class);

    @Test
    void aListenerIsRefusedBeforeItConnects() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();

            ListenerDescriptor listenerDescriptor = mock(ListenerDescriptor.class);
            when(listenerDescriptor.getHandlerPath()).thenReturn(HANDLER);
            ActiveMQConnectionArtifactsFactory connectionArtifactsFactory = mock(ActiveMQConnectionArtifactsFactory.class);
            SharedListenerConnection sharedConnection = mock(SharedListenerConnection.class);
            ListenerManager listenerManager = new ListenerManager(listenerDescriptor, connectionArtifactsFactory,
                    context.getBean(AsynchronousMessageListenerFactory.class), ListenerConnectionMode.PER_LISTENER, sharedConnection);

            assertThatThrownBy(listenerManager::startListener).isInstanceOf(ScriptEngineUnavailableException.class)
                                                              .hasMessageContaining(HANDLER)
                                                              .hasMessageContaining("JavaScript engine");
            verifyNoInteractions(connectionArtifactsFactory, sharedConnection);
        });
    }
}
