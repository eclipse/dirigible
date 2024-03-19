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

import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.springframework.stereotype.Component;

@Component
class AsynchronousMessageListenerFactory {

    private final TenantPropertyManager tenantPropertyManager;
    private final TenantContext tenantContext;

    AsynchronousMessageListenerFactory(TenantPropertyManager tenantPropertyManager, TenantContext tenantContext) {
        this.tenantPropertyManager = tenantPropertyManager;
        this.tenantContext = tenantContext;
    }

    AsynchronousMessageListener create(Listener listener) {
        return new AsynchronousMessageListener(listener, tenantPropertyManager, tenantContext);
    }
}
