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

import org.eclipse.dirigible.components.base.spring.BeanProvider;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * The Class TenantConverter.
 */
public class TenantConverter extends ClassicConverter {

    /** The Constant BACKGROUND_TENANT_VALUE. */
    private static final String BACKGROUND_TENANT_VALUE = "background";
    
    /** The tenant context. */
    private TenantContext tenantContext;

    /**
     * Convert.
     *
     * @param event the event
     * @return the string
     */
    @Override
    public String convert(ILoggingEvent event) {
        TenantContext ctx = getTenantContext();
        if (null == ctx) {
            return BACKGROUND_TENANT_VALUE;
        }
        return ctx.isInitialized() ? ctx.getCurrentTenant()
                                        .getId()
                : BACKGROUND_TENANT_VALUE;
    }

    /**
     * Gets the tenant context.
     *
     * @return the tenant context
     */
    private TenantContext getTenantContext() {
        if (null != tenantContext) {
            return tenantContext;
        }

        tenantContext = BeanProvider.isInitialzed() ? BeanProvider.getTenantContext() : null;
        return tenantContext;
    }

}
