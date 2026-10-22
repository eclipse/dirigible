/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.extensions.modules;

import java.util.List;

/**
 * Told which script extension modules the {@link ExtensionModuleReader} skipped because no script
 * engine is deployed to evaluate them, so that skipping them is reported where a developer looks
 * and not only in the log. Every platform extension module is a JSON module, so a skipped one is
 * always application content.
 */
public interface ScriptModuleSkipListener {

    /**
     * Receives the script modules one read skipped, each the first time it is skipped on an extension
     * point.
     *
     * @param modules the module paths, relative to the public registry - the first segment names the
     *        project
     */
    void scriptModulesSkipped(List<String> modules);
}
