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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** The folder deprovisioning deletes is always a direct child of the internal CMS root. */
class CmsInternalTenantTeardownTest {

    @TempDir
    Path workingDirectory;

    @Test
    void aRelativeRootIsResolvedUnderTheWorkingDirectory() {
        Path folder = CmsInternalTenantTeardown.tenantFolder("target/cms", "acme", workingDirectory.toString());

        assertEquals(workingDirectory.resolve("target/cms/acme")
                                     .normalize(),
                folder);
    }

    @Test
    void anAbsoluteRootIsUsedAsItIs() {
        Path root = workingDirectory.resolve("cms");

        assertEquals(root.resolve("acme"), CmsInternalTenantTeardown.tenantFolder(root.toString(), "acme", "/elsewhere"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", ".", "..", "../acme", "acme/..", "acme/../..", "a/b", "/acme"})
    void anIdThatDoesNotNameADirectChildOfTheRootIsRefused(String tenantId) {
        Path root = workingDirectory.resolve("cms");

        assertThrows(IllegalArgumentException.class,
                () -> CmsInternalTenantTeardown.tenantFolder(root.toString(), tenantId, workingDirectory.toString()));
    }
}
