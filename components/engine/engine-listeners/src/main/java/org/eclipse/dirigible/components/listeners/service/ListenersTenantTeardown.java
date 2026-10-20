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

import org.eclipse.dirigible.commons.api.helpers.LogSanitizer;
import org.eclipse.dirigible.components.base.tenant.TenantTeardownParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Deprovisioning: stops the tenant's {@code .listener} consumers - every one subscribed to a
 * destination {@code <tenantId>###<name>}. The destinations themselves stay on the broker.
 */
@Component
class ListenersTenantTeardown implements TenantTeardownParticipant {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ListenersTenantTeardown.class);

    /** The order of this participant: what runs for the tenant goes first. */
    static final int ORDER = 100;

    /** The listeners manager. */
    private final ListenersManager listenersManager;

    /**
     * Instantiates a new listeners teardown.
     *
     * @param listenersManager the listeners manager
     */
    ListenersTenantTeardown(ListenersManager listenersManager) {
        this.listenersManager = listenersManager;
    }

    @Override
    public int order() {
        return ORDER;
    }

    @Override
    public void teardown(String tenantId) {
        int stopped = listenersManager.stopTenantListeners(tenantId);
        if (stopped > 0) {
            LOGGER.info("Stopped [{}] listener(s) of tenant [{}].", stopped, LogSanitizer.sanitize(tenantId));
        }
    }
}
