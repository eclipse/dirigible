/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.issues;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.dirigible.components.api.cms.AttachmentsFacade;
import org.eclipse.dirigible.components.api.cms.AttachmentsFacade.StoredAttachment;
import org.eclipse.dirigible.components.base.tenant.DefaultTenant;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.springframework.stereotype.Component;

/**
 * Keeps issue attachments in the default tenant's CMS - the instance's own store, internal or S3 -
 * whichever tenant the issue was filed in, so a tenant's documents never hold another tenant's
 * report and the operators read every attachment from one place.
 */
@Component
class IssueAttachmentStore {

    /** The CMS folder issue attachments live under. */
    private static final String MASTER = "Issues";

    private final TenantContext tenantContext;

    private final Tenant defaultTenant;

    IssueAttachmentStore(TenantContext tenantContext, @DefaultTenant Tenant defaultTenant) {
        this.tenantContext = tenantContext;
        this.defaultTenant = defaultTenant;
    }

    /**
     * Stores a file.
     *
     * @param fileName the file name
     * @param mimeType the MIME type
     * @param content the bytes
     * @return what was stored
     * @throws IOException when the CMS refuses the write
     */
    StoredAttachment store(String fileName, String mimeType, byte[] content) throws IOException {
        return tenantContext.execute(defaultTenant, () -> AttachmentsFacade.store(MASTER, fileName, mimeType, content));
    }

    /**
     * Reads a stored file whole; attachments are bounded by
     * {@code DIRIGIBLE_ISSUES_ATTACHMENT_MAX_BYTES}.
     *
     * @param storageReference where it is
     * @return the bytes
     * @throws IOException when the file is gone
     */
    byte[] read(String storageReference) throws IOException {
        return tenantContext.execute(defaultTenant, () -> {
            try (InputStream content = AttachmentsFacade.read(storageReference)) {
                return content.readAllBytes();
            }
        });
    }

    /**
     * Removes a stored file - the undo of a {@link #store} whose row could not be saved.
     *
     * @param storageReference where it is
     * @throws IOException when the CMS refuses the delete
     */
    void delete(String storageReference) throws IOException {
        tenantContext.execute(defaultTenant, () -> {
            AttachmentsFacade.delete(storageReference);
            return null;
        });
    }
}
