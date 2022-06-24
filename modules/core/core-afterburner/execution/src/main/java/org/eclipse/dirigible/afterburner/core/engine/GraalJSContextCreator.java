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
package org.eclipse.dirigible.afterburner.core.engine;

import org.eclipse.dirigible.afterburner.core.engine.configuration.Configuration;
import org.eclipse.dirigible.afterburner.core.engine.modules.downloadable.DownloadableModuleResolver;
import org.eclipse.dirigible.afterburner.core.engine.modules.ModuleResolver;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.EnvironmentAccess;
import org.graalvm.polyglot.HostAccess;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class GraalJSContextCreator {
    private static final String JAVASCRIPT_GRAALVM_ALLOW_HOST_ACCESS = "JAVASCRIPT_GRAALVM_ALLOW_HOST_ACCESS";
    private static final String JAVASCRIPT_GRAALVM_ALLOW_CREATE_THREAD = "JAVASCRIPT_GRAALVM_ALLOW_CREATE_THREAD";
    private static final String JAVASCRIPT_GRAALVM_ALLOW_IO = "JAVASCRIPT_GRAALVM_ALLOW_IO";
    private static final String JAVASCRIPT_GRAALVM_ALLOW_CREATE_PROCESS = "JAVASCRIPT_GRAALVM_ALLOW_CREATE_PROCESS";

    private static HostAccess graalHostAccess;

    @SuppressWarnings("rawtypes")
    public GraalJSContextCreator(List<GraalJSTypeMap> typeMaps) {
        if (graalHostAccess == null) {
            graalHostAccess = createHostAccess(typeMaps);
        }
    }

    public Context createContext(
            Engine engine,
            Path workingDirectoryPath,
            DownloadableModuleResolver downloadableModuleResolver,
            List<ModuleResolver> moduleResolvers,
            Consumer<Context.Builder> onBeforeContextCreatedHook,
            Consumer<Context> onAfterContextCreatedHook
    ) {
        GraalJSFileSystem graalJSFileSystem = new GraalJSFileSystem(
                workingDirectoryPath,
                moduleResolvers,
                downloadableModuleResolver
        );

        Context.Builder contextBuilder = Context.newBuilder()
                .engine(engine)
                .allowEnvironmentAccess(EnvironmentAccess.INHERIT)
                .allowExperimentalOptions(true)
                .currentWorkingDirectory(workingDirectoryPath)
                .fileSystem(graalJSFileSystem);

        onBeforeContextCreatedHook.accept(contextBuilder);

        if (Boolean.parseBoolean(Configuration.get(JAVASCRIPT_GRAALVM_ALLOW_HOST_ACCESS, "true"))) {
            contextBuilder.allowHostClassLookup(s -> true);
            contextBuilder.allowHostAccess(graalHostAccess);
            contextBuilder.allowAllAccess(true);
        }
        if (Boolean.parseBoolean(Configuration.get(JAVASCRIPT_GRAALVM_ALLOW_CREATE_THREAD, "true"))) {
            contextBuilder.allowCreateThread(true);
        }
        if (Boolean.parseBoolean(Configuration.get(JAVASCRIPT_GRAALVM_ALLOW_IO, "true"))) {
            contextBuilder.allowIO(true);
        }
        if (Boolean.parseBoolean(Configuration.get(JAVASCRIPT_GRAALVM_ALLOW_CREATE_PROCESS, "true"))) {
            contextBuilder.allowCreateProcess(true);
        }

        Context context = contextBuilder.build();
        onAfterContextCreatedHook.accept(context);
        return context;
    }

    @SuppressWarnings("rawtypes")
    private static HostAccess createHostAccess(List<GraalJSTypeMap> typeMaps) {
        HostAccess.Builder hostAccessConfigBuilder = HostAccess.newBuilder(HostAccess.ALL);

        for (GraalJSTypeMap typeMap : typeMaps) {
            hostAccessConfigBuilder.targetTypeMapping(
                    typeMap.getSourceClass(),
                    typeMap.getTargetClass(),
                    s -> true,
                    typeMap.getConverter()
            );
        }

        return hostAccessConfigBuilder.build();
    }
}
