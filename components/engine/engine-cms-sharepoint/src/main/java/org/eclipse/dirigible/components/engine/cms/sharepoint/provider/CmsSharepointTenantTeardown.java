/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.cms.sharepoint.provider;

import org.eclipse.dirigible.commons.api.helpers.LogSanitizer;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.api.sharepoint.CmisObjectNotFoundException;
import org.eclipse.dirigible.components.api.sharepoint.SharepointFacade;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.eclipse.dirigible.components.base.tenant.TenantTeardownParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Deprovisioning: deletes the tenant's documents in the SharePoint CMS - the folder
 * {@code <tenantId>} at the root of the document library all tenants share.
 *
 * <p>
 * The facade prefixes every path with the <em>current</em> tenant's id, and the deprovisioning
 * request runs as the default tenant, so the folder delete runs inside the tenant's context, where
 * the root {@code /} resolves to the tenant's folder. A folder that is not there is success - the
 * tenant never stored a document, or an earlier run deleted it.
 *
 * <p>
 * The Graph delete moves the folder to the site's recycle bin, where the site's retention policy
 * applies; it is not purged from there.
 *
 * <p>
 * Only when SharePoint is the configured provider: with another provider the library holds nothing
 * of the tenant's.
 */
@Component
class CmsSharepointTenantTeardown implements TenantTeardownParticipant {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CmsSharepointTenantTeardown.class);

    /** The order of this participant: the tenant's content, after what runs for it. */
    static final int ORDER = 600;

    /** The provider name the SharePoint CMS is registered under. */
    static final String PROVIDER = "cms-provider-ms-sharepoint";

    /** The tenant context. */
    private final TenantContext tenantContext;

    /**
     * Instantiates a new SharePoint CMS tenant teardown.
     *
     * @param tenantContext the tenant context
     */
    CmsSharepointTenantTeardown(TenantContext tenantContext) {
        this.tenantContext = tenantContext;
    }

    @Override
    public int order() {
        return ORDER;
    }

    @Override
    public void teardown(String tenantId) {
        if (!PROVIDER.equals(Configuration.get("DIRIGIBLE_CMS_PROVIDER"))) {
            return;
        }
        boolean deleted = tenantContext.execute(tenantId, () -> {
            try {
                SharepointFacade.deleteFolder("/");
                return true;
            } catch (CmisObjectNotFoundException absent) {
                return false;
            }
        });
        if (deleted) {
            LOGGER.info("Deleted the SharePoint CMS folder of tenant [{}].", LogSanitizer.sanitize(tenantId));
        }
    }
}
