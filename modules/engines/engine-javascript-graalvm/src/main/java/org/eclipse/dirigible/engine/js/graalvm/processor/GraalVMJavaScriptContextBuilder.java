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
package org.eclipse.dirigible.engine.js.graalvm.processor;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.engine.js.graalvm.debugger.GraalVMJavascriptDebugProcessor;
import org.eclipse.dirigible.engine.js.graalvm.processor.truffle.RegistryTruffleFileSystem;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.EnvironmentAccess;
import org.graalvm.polyglot.HostAccess;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

class GraalVMJavaScriptContextBuilder {
    private static final String BUILDER_OPTION_INSPECT = "inspect";
    private static final String BUILDER_OPTION_INSPECT_SECURE = "inspect.Secure";
    private static final String BUILDER_OPTION_INSPECT_PATH = "inspect.Path";

    private static final String DIRIGIBLE_JAVASCRIPT_GRAALVM_DEBUGGER_ENABLED = "DIRIGIBLE_JAVASCRIPT_GRAALVM_DEBUGGER_ENABLED";
    private static final String DIRIGIBLE_JAVASCRIPT_GRAALVM_DEBUGGER_PORT = "DIRIGIBLE_JAVASCRIPT_GRAALVM_DEBUGGER_PORT";
    private static final String DIRIGIBLE_JAVASCRIPT_GRAALVM_ALLOW_HOST_ACCESS = "DIRIGIBLE_JAVASCRIPT_GRAALVM_ALLOW_HOST_ACCESS";
    private static final String DIRIGIBLE_JAVASCRIPT_GRAALVM_ALLOW_CREATE_THREAD = "DIRIGIBLE_JAVASCRIPT_GRAALVM_ALLOW_CREATE_THREAD";
    private static final String DIRIGIBLE_JAVASCRIPT_GRAALVM_ALLOW_CREATE_PROCESS = "DIRIGIBLE_JAVASCRIPT_GRAALVM_ALLOW_CREATE_PROCESS";
    private static final String DIRIGIBLE_JAVASCRIPT_GRAALVM_ALLOW_IO = "DIRIGIBLE_JAVASCRIPT_GRAALVM_ALLOW_IO";
    private static final String DIRIGIBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_NASHORN = "DIRIGIBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_NASHORN";
    private static final String DIRIGIBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_MOZILLA = "DIRIGIBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_MOZILLA";

    public static final String DEFAULT_DEBUG_PORT = "8081";

    Context createJavaScriptContext(String moduleOrCode, Function<String, RegistryTruffleFileSystem> truffleFileSystemProvider) {
        Context.Builder contextBuilder = Context.newBuilder("js")
                .allowEnvironmentAccess(EnvironmentAccess.INHERIT)
                .allowExperimentalOptions(true)
                .option("js.ecmascript-version", "2022")
                .option("engine.WarnInterpreterOnly", "false");

        if (moduleOrCode.endsWith(".mjs")) {
            if (moduleOrCode.startsWith("/")) {
                moduleOrCode = StringUtils.substringAfter(moduleOrCode, "/");
            }
            String project = StringUtils.substringBeforeLast(moduleOrCode, "/");
            RegistryTruffleFileSystem registryTruffleFileSystem = truffleFileSystemProvider.apply(project);
            contextBuilder.fileSystem(registryTruffleFileSystem);
            contextBuilder.option("js.esm-eval-returns-exports", "true");
        }


        if (Boolean.parseBoolean(Configuration.get(DIRIGIBLE_JAVASCRIPT_GRAALVM_ALLOW_HOST_ACCESS, "true"))) {
            contextBuilder.allowHostClassLookup(s -> true)
                    .allowHostAccess(HostAccess.ALL)
                    .allowAllAccess(true);
        }
        if (Boolean.parseBoolean(Configuration.get(DIRIGIBLE_JAVASCRIPT_GRAALVM_ALLOW_CREATE_THREAD, "true"))) {
            contextBuilder.allowCreateThread(true);
        }
        if (Boolean.parseBoolean(Configuration.get(DIRIGIBLE_JAVASCRIPT_GRAALVM_ALLOW_IO, "true"))) {
            contextBuilder.allowIO(true);
        }
        if (Boolean.parseBoolean(Configuration.get(DIRIGIBLE_JAVASCRIPT_GRAALVM_ALLOW_CREATE_PROCESS, "true"))) {
            contextBuilder.allowCreateProcess(true);
        }
        if (Boolean.parseBoolean(Configuration.get(DIRIGIBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_NASHORN, "true"))) {
            contextBuilder.option("js.nashorn-compat", "true");
        }

        if (isDebugEnabled()) {
            contextBuilder.option(BUILDER_OPTION_INSPECT, Configuration.get(DIRIGIBLE_JAVASCRIPT_GRAALVM_DEBUGGER_PORT, DEFAULT_DEBUG_PORT));
            contextBuilder.option(BUILDER_OPTION_INSPECT_SECURE, Boolean.FALSE.toString());
            contextBuilder.option(BUILDER_OPTION_INSPECT_PATH, moduleOrCode);
        }

        return contextBuilder.build();
    }

    private boolean isDebugEnabled() {
        return GraalVMJavascriptDebugProcessor.haveUserSession(UserFacade.getName());
    }
}
