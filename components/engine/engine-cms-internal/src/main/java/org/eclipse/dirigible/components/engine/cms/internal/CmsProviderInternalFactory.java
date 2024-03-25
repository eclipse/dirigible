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
package org.eclipse.dirigible.components.engine.cms.internal;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.base.tenant.DefaultTenant;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.eclipse.dirigible.components.engine.cms.CmsProvider;
import org.eclipse.dirigible.components.engine.cms.CmsProviderFactory;
import org.eclipse.dirigible.components.engine.cms.internal.provider.CmsProviderInternal;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A factory for creating CmsProviderInternal objects.
 */
@Component("cms-provider-internal")
class CmsProviderInternalFactory implements CmsProviderFactory {

    /** The Constant DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER. */
    private static final String DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER = "DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER";

    /** The tenant context. */
    private final TenantContext tenantContext;

    /** The default tenant. */
    private final Tenant defaultTenant;

    /**
     * Instantiates a new cms provider internal factory.
     *
     * @param tenantContext the tenant context
     * @param defaultTenant the default tenant
     */
    CmsProviderInternalFactory(TenantContext tenantContext, @DefaultTenant Tenant defaultTenant) {
        this.tenantContext = tenantContext;
        this.defaultTenant = defaultTenant;
    }

    /**
     * Creates the.
     *
     * @return the cms provider
     */
    @Override
    public CmsProvider create() {
        String rootFolder = Configuration.get(DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER, "target/dirigible") + getTenantFolder();
        Path path = Paths.get(rootFolder);
        boolean absolutePath = path.isAbsolute();

        return new CmsProviderInternal(rootFolder, absolutePath);
    }

    /**
     * Gets the tenant folder.
     *
     * @return the tenant folder
     */
    private String getTenantFolder() {
        String tenantId = tenantContext.isNotInitialized() ? defaultTenant.getId()
                : tenantContext.getCurrentTenant()
                               .getId();
        return File.separator + tenantId;
    }
}
