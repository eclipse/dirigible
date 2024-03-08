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
package org.eclipse.dirigible.components.base;

import org.springframework.core.Ordered;

/**
 * Lower value means higher precedence - lower values will be executed before the higher values
 */
public interface ApplicationListenersOrder {

    public interface ApplicationReadyEventListeners {

        int SYSTEM_ROLES_INITIALIZER = 10;

        int DEFAULT_TENANT_INITIALIZER = 20;

        int ADMIN_USER_INITIALIZER = 30;

        int SYNCHRONIZATION_INTIALIZER = 40;

    }

    public interface ApplicationStoppedEventListeners {

        int ACTIVE_MQ_CLEANUP = APP_LYFECYCLE_LOGGING_LISTENER - 10;

    }

    int APP_LYFECYCLE_LOGGING_LISTENER = Ordered.LOWEST_PRECEDENCE;

}
