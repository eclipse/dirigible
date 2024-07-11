/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.api.mail;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.base.spring.BeanProvider;

import java.util.Collection;
import java.util.Properties;

/**
 * The Class MailFacade.
 */
public class MailFacade {

    /** The Constant DIRIGIBLE_MAIL_CONFIG_PROVIDER. */
    // Dirigible mail properties
    private static final String DIRIGIBLE_MAIL_CONFIG_PROVIDER = "DIRIGIBLE_MAIL_CONFIG_PROVIDER";

    /** The Constant DEFAULT_PROVIDER_NAME. */
    // Default values
    private static final String DEFAULT_PROVIDER_NAME = "environment";

    /**
     * Get MailClient with configuration options from the chosen mail configuration provider.
     *
     * @return MailClient instance
     */
    public static MailClient getInstance() {
        Properties properties = new Properties();
        String providerName = Configuration.get(DIRIGIBLE_MAIL_CONFIG_PROVIDER, DEFAULT_PROVIDER_NAME);

        Collection<MailConfigurationProvider> providers = BeanProvider.getBeans(MailConfigurationProvider.class);
        for (MailConfigurationProvider next : providers) {
            if (providerName.equals(next.getName())) {
                properties.putAll(next.getProperties());
                break;
            }
        }
        return getInstance(properties);
    }

    /**
     * Get MailClient with custom configuration options.
     *
     * @param properties mail client configuration options
     * @return MailClient instance
     */
    public static MailClient getInstance(Properties properties) {
        return new MailClient(properties);
    }
}
