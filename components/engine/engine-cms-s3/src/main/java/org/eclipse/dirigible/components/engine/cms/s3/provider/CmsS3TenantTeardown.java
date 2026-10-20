/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.cms.s3.provider;

import org.eclipse.dirigible.commons.api.helpers.LogSanitizer;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.api.s3.S3Facade;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.eclipse.dirigible.components.base.tenant.TenantTeardownParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Deprovisioning: deletes the tenant's documents in the S3 CMS - every object under
 * {@code <tenantId>/} in the bucket all tenants share.
 *
 * <p>
 * The facade prefixes every path with the <em>current</em> tenant's id, and the deprovisioning
 * request runs as the default tenant, so the folder delete runs inside the tenant's context, where
 * the root {@code /} resolves to {@code <tenantId>/}. The trailing slash keeps a tenant whose id
 * starts with this one's untouched.
 *
 * <p>
 * Only when S3 is the configured provider: with another provider the bucket holds nothing of the
 * tenant's.
 */
@Component
class CmsS3TenantTeardown implements TenantTeardownParticipant {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CmsS3TenantTeardown.class);

    /** The order of this participant: the tenant's content, after what runs for it. */
    static final int ORDER = 600;

    /** The provider name the S3 CMS is registered under. */
    static final String PROVIDER = "cms-provider-s3";

    /** The tenant context. */
    private final TenantContext tenantContext;

    /**
     * Instantiates a new S3 CMS tenant teardown.
     *
     * @param tenantContext the tenant context
     */
    CmsS3TenantTeardown(TenantContext tenantContext) {
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
        tenantContext.execute(tenantId, () -> {
            S3Facade.deleteFolder("/");
            return null;
        });
        LOGGER.info("Deleted the S3 CMS documents of tenant [{}].", LogSanitizer.sanitize(tenantId));
    }
}
