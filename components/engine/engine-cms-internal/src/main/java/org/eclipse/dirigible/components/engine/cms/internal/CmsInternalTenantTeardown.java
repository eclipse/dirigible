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
        String rootFolder = DirigibleConfig.CMS_INTERNAL_ROOT_FOLDER.getStringValue() + File.separator + tenantId;
        CmsProviderInternalFactory.forget(rootFolder);
        // resolved the way the repository resolves it: a relative root lives under the working directory
        Path folder = Paths.get(rootFolder);
        if (!folder.isAbsolute()) {
            folder = Paths.get(System.getProperty("user.dir"), rootFolder);
        }
        if (!Files.exists(folder)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(folder)) {
            paths.sorted(Comparator.reverseOrder())
                 .forEach(CmsInternalTenantTeardown::delete);
        } catch (IOException ex) {
            throw new UncheckedIOException("Could not delete the CMS folder [" + folder + "] of tenant [" + tenantId + "]", ex);
        }
        LOGGER.info("Deleted the CMS folder [{}] of tenant [{}].", folder, tenantId);
    }

    private static void delete(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            throw new UncheckedIOException("Could not delete [" + path + "]", ex);
        }
    }
}
