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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Publishes the configured {@link ListenerConnectionMode} as a bean, read once at boot, so the two
 * subscribing sides - the {@code .listener} artefacts and the client-Java listeners - agree on it
 * and their unit tests can pass either mode in.
 */
@Configuration
class ListenerConnectionModeConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListenerConnectionModeConfig.class);

    @Bean
    ListenerConnectionMode listenerConnectionMode() {
        ListenerConnectionMode mode = ListenerConnectionMode.configured();
        LOGGER.info("Message listeners attach to the broker with [{}] connections.", mode.key());
        return mode;
    }
}
