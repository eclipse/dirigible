/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.listeners.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.eclipse.dirigible.commons.config.DirigibleConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code DIRIGIBLE_MESSAGING_LISTENER_CONNECTIONS} selects the {@link ListenerConnectionMode}:
 * shared by default, per-listener on request, and anything else refused by name.
 */
class ListenerConnectionModeTest {

    private String previous;

    @BeforeEach
    void rememberTheConfiguredValue() {
        previous = DirigibleConfig.MESSAGING_LISTENER_CONNECTIONS.getStringValue();
    }

    @AfterEach
    void restoreTheConfiguredValue() {
        DirigibleConfig.MESSAGING_LISTENER_CONNECTIONS.setStringValue(previous);
    }

    @Test
    void sharedIsTheDefault() {
        assertThat(ListenerConnectionMode.configured()).isEqualTo(ListenerConnectionMode.SHARED);
    }

    @Test
    void perListenerIsSelectedByItsKey() {
        DirigibleConfig.MESSAGING_LISTENER_CONNECTIONS.setStringValue("per-listener");

        assertThat(ListenerConnectionMode.configured()).isEqualTo(ListenerConnectionMode.PER_LISTENER);
    }

    @Test
    void theKeyIsReadRegardlessOfCaseAndWhitespace() {
        DirigibleConfig.MESSAGING_LISTENER_CONNECTIONS.setStringValue(" Per-Listener ");

        assertThat(ListenerConnectionMode.configured()).isEqualTo(ListenerConnectionMode.PER_LISTENER);
    }

    @Test
    void anUnknownValueIsRefusedRatherThanDefaulted() {
        DirigibleConfig.MESSAGING_LISTENER_CONNECTIONS.setStringValue("pooled");

        IllegalStateException refused = assertThrows(IllegalStateException.class, ListenerConnectionMode::configured);

        assertThat(refused).hasMessageContaining("pooled")
                           .hasMessageContaining(DirigibleConfig.MESSAGING_LISTENER_CONNECTIONS.getKey());
    }
}
