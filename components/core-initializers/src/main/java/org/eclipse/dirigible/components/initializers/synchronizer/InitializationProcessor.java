/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.initializers.synchronizer;

import org.eclipse.dirigible.components.base.initializer.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InitializationProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitializationProcessor.class);
    private final List<Initializer> initializers;

    @Autowired
    public InitializationProcessor(List<Initializer> initializers) {
        this.initializers = initializers;
    }

    void processInitializers() {
        initializers.forEach(Initializer::initialize);
        for (var initializer : initializers) {
            try {
                initializer.initialize();
            } catch (Exception e) {
                LOGGER.error("Initializer failed", e);
            }

        }
    }
}
