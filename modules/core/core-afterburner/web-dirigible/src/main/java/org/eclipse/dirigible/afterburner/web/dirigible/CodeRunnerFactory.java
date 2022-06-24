/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.afterburner.web.dirigible;

import org.eclipse.dirigible.afterburner.core.CodeRunner;
import org.eclipse.dirigible.afterburner.core.JavaScriptCodeRunner;
import org.eclipse.dirigible.afterburner.core.dirigible.modules.DirigibleModuleResolver;
import org.eclipse.dirigible.afterburner.core.dirigible.globals.DirigibleContextGlobalObject;
import org.eclipse.dirigible.afterburner.core.dirigible.globals.DirigibleEngineTypeGlobalObject;
import org.eclipse.dirigible.afterburner.core.dirigible.polyfills.RequirePolyfill;

import java.nio.file.Path;
import java.util.HashMap;

public class CodeRunnerFactory {

    private CodeRunnerFactory() {
    }

    public static CodeRunner createDirigibleJSCodeRunner(Path workingDirectoryPath) {
        var cachePath = workingDirectoryPath.resolve("caches");
        var coreModulesESMProxiesCachePath = cachePath.resolve("core-modules-proxies-cache");

        return JavaScriptCodeRunner.newBuilder(workingDirectoryPath, cachePath)
                .addJSPolyfill(new RequirePolyfill())
                .addGlobalObject(new DirigibleContextGlobalObject(new HashMap<>()))
                .addGlobalObject(new DirigibleEngineTypeGlobalObject())
                .addModuleResolver(new DirigibleModuleResolver(coreModulesESMProxiesCachePath))
                .waitForDebugger(false)
                .build();
    }
}
