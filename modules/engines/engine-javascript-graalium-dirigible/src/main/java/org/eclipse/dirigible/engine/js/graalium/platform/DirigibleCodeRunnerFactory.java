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
package org.eclipse.dirigible.engine.js.graalium.platform;

import org.eclipse.dirigible.afterburner.core.CodeRunner;
import org.eclipse.dirigible.afterburner.core.JavaScriptCodeRunner;
import org.eclipse.dirigible.afterburner.core.engine.polyfills.RequirePolyfill;
import org.eclipse.dirigible.engine.js.graalium.platform.internal.modules.DirigibleCoreModuleResolver;
import org.eclipse.dirigible.engine.js.graalium.platform.internal.polyfills.DirigibleContextGlobalObject;
import org.eclipse.dirigible.engine.js.graalium.platform.internal.polyfills.DirigibleEngineTypeGlobalObject;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DirigibleCodeRunnerFactory {

    private DirigibleCodeRunnerFactory() {
    }

    public static CodeRunner createDirigibleJSCodeRunner(Path workingDirectoryPath) {
        return createDirigibleJSCodeRunner(workingDirectoryPath, new HashMap<>());
    }

    public static CodeRunner createDirigibleJSCodeRunner(Path workingDirectoryPath, Map<Object, Object> executionContext) {
        var cachePath = workingDirectoryPath.resolve("caches");
        var coreModulesESMProxiesCachePath = cachePath.resolve("core-modules-proxies-cache");

        return JavaScriptCodeRunner.newBuilder(workingDirectoryPath, cachePath)
                .addJSPolyfill(new RequirePolyfill())
                .addGlobalObject(new DirigibleContextGlobalObject(executionContext))
                .addGlobalObject(new DirigibleEngineTypeGlobalObject())
                .addModuleResolver(new DirigibleCoreModuleResolver(coreModulesESMProxiesCachePath))
                .waitForDebugger(false)
                .build();
    }
}
