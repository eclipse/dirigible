/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.cms.internal;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

import org.eclipse.dirigible.commons.api.helpers.LogSanitizer;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.DirigibleConfig;
import org.eclipse.dirigible.components.base.tenant.TenantTeardownParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Deprovisioning: deletes the tenant's documents in the internal CMS - the folder
 * {@code <DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER>/<tenantId>} that {@link CmsProviderInternalFactory}
 * gives each tenant.
 *
 * <p>
 * Only when the internal CMS is the configured provider: with another provider the folder holds
 * nothing of the tenant's.
 */
@Component
class CmsInternalTenantTeardown implements TenantTeardownParticipant {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CmsInternalTenantTeardown.class);

    /** The order of this participant: the tenant's content, after what runs for it. */
    static final int ORDER = 600;

    /** The provider name the internal CMS is registered under - the default. */
    private static final String PROVIDER = "cms-provider-internal";

    @Override
    public int order() {
        return ORDER;
    }

    @Override
    public void teardown(String tenantId) {
        if (!PROVIDER.equals(Configuration.get("DIRIGIBLE_CMS_PROVIDER", PROVIDER))) {
            return;
        }
        String root = DirigibleConfig.CMS_INTERNAL_ROOT_FOLDER.getStringValue();
        Path folder = tenantFolder(root, tenantId, System.getProperty("user.dir"));
        CmsProviderInternalFactory.forget(root + File.separator + tenantId);
        if (!Files.exists(folder)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(folder)) {
            paths.sorted(Comparator.reverseOrder())
                 .forEach(CmsInternalTenantTeardown::delete);
        } catch (IOException ex) {
            throw new UncheckedIOException("Could not delete the CMS folder [" + folder + "] of tenant [" + tenantId + "]", ex);
        }
        LOGGER.info("Deleted the CMS folder [{}] of tenant [{}].", LogSanitizer.sanitize(folder), LogSanitizer.sanitize(tenantId));
    }

    /**
     * The tenant's folder, resolved the way the repository resolves it: a relative root lives under the
     * working directory. The folder is deleted recursively, so it must be a direct child of the root -
     * an id that would resolve anywhere else (a separator, {@code ..}, an absolute path) is refused.
     *
     * @param root the internal CMS root folder
     * @param tenantId the tenant id
     * @param workingDirectory the directory a relative root is resolved against
     * @return the tenant's folder
     * @throws IllegalArgumentException if the tenant id does not name a direct child of the root
     */
    static Path tenantFolder(String root, String tenantId, String workingDirectory) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("A tenant id is required to resolve its CMS folder");
        }
        Path rootPath = Paths.get(root);
        if (!rootPath.isAbsolute()) {
            rootPath = Paths.get(workingDirectory, root);
        }
        rootPath = rootPath.normalize();
        Path folder = rootPath.resolve(tenantId)
                              .normalize();
        if (!rootPath.equals(folder.getParent())) {
            throw new IllegalArgumentException("The tenant id [" + tenantId + "] does not name a folder directly under the CMS root");
        }
        return folder;
    }

    private static void delete(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            throw new UncheckedIOException("Could not delete [" + path + "]", ex);
        }
    }
}
