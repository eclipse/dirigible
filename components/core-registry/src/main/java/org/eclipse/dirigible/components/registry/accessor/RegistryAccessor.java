/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.registry.accessor;

import org.eclipse.dirigible.engine.api.resource.RegistryResourceExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RegistryAccessor {

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(RegistryAccessor.class);

    /**
     * Registry content.
     *
     * @param templateLocation the template location
     * @param defaultLocation the default location
     * @return the byte[] template
     */
    public byte[] getRegistryContent(String templateLocation, String defaultLocation){
        RegistryResourceExecutor registryResourceExecutor = new RegistryResourceExecutor();
        byte[] template = registryResourceExecutor.getRegistryContent(templateLocation);
        if (template == null) {
            template = registryResourceExecutor.getRegistryContent(defaultLocation);
            if (template == null) {
                if (logger.isErrorEnabled()) {logger.error("Template for the e-mail has not been set nor the default one is available");}
                return null;
            }
        }
        return template;
    }
}
