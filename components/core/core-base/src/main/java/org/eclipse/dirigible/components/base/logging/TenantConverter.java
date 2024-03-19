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
package org.eclipse.dirigible.components.base.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.eclipse.dirigible.components.base.spring.BeanProvider;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TenantConverter extends ClassicConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantConverter.class);

    private static final String BACKGROUND_TENANT_VALUE = "background";
    private TenantContext tenantContext;

    @Override
    public String convert(ILoggingEvent event) {
        TenantContext ctx = getTenantContext();
        if (null == ctx) {
            return BACKGROUND_TENANT_VALUE;
        }
        return ctx.isInitialized() ? ctx.getCurrentTenant()
                                        .getId() : BACKGROUND_TENANT_VALUE;
    }

    private TenantContext getTenantContext() {
        if (null != tenantContext) {
            return tenantContext;
        }

        try {
            tenantContext = BeanProvider.isInitialzed() ? BeanProvider.getTenantContext() : null;
        } catch (RuntimeException ex) {
            LOGGER.warn("Failed to get tenant context", ex);
        }
        return tenantContext;
    }

}
