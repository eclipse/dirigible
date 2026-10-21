/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.tracing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * The Tracing perspective's switch reads and turns over the tracing flag.
 */
class TracingSwitchEndpointTest {

    private final TracingSwitchEndpoint endpoint =
            new TracingSwitchEndpoint(new TaskStateService(mock(TaskStateRepository.class), mock(TenantContext.class), mock(Tenant.class)));

    @AfterEach
    void resetTracingFlag() {
        Configuration.remove(TaskStateService.DIRIGIBLE_TRACING_TASK_ENABLED);
    }

    @Test
    void tracing_is_off_until_it_is_switched_on() {
        Configuration.remove(TaskStateService.DIRIGIBLE_TRACING_TASK_ENABLED);

        assertEquals("false", endpoint.isEnabled());
    }

    @Test
    void each_toggle_turns_tracing_over_and_answers_the_new_state() {
        Configuration.set(TaskStateService.DIRIGIBLE_TRACING_TASK_ENABLED, "false");

        assertEquals("true", endpoint.toggle());
        assertEquals("true", endpoint.isEnabled());
        assertEquals("true", Configuration.get(TaskStateService.DIRIGIBLE_TRACING_TASK_ENABLED));

        assertEquals("false", endpoint.toggle());
        assertEquals("false", endpoint.isEnabled());
    }
}
