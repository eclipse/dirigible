/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.graalium.core.graal;

import org.eclipse.dirigible.graalium.core.graal.configuration.Configuration;
import org.eclipse.dirigible.graalium.core.graal.modules.downloadable.DownloadableModuleResolver;
import org.eclipse.dirigible.graalium.core.graal.modules.ModuleResolver;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.EnvironmentAccess;
import org.graalvm.polyglot.HostAccess;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The Class GraalJSContextCreator.
 */
public class GraalJSContextCreator {
    
	/** The Constant DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_HOST_ACCESS. */
	public static final String DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_HOST_ACCESS = "DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_HOST_ACCESS";
	
	/** The Constant DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_CREATE_THREAD. */
	public static final String DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_CREATE_THREAD = "DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_CREATE_THREAD";
	
	/** The Constant DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_CREATE_PROCESS. */
	public static final String DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_CREATE_PROCESS = "DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_CREATE_PROCESS";
	
	/** The Constant DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_IO. */
	public static final String DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_IO = "DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_IO";
	
	/** The Constant DIRIGBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_NASHORN. */
	public static final String DIRIGBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_NASHORN = "DIRIGBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_NASHORN";
	
	/** The Constant DIRIGBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_MOZILLA. */
	public static final String DIRIGBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_MOZILLA = "DIRIGBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_MOZILLA";

    /** The Constant DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_JSON_MODULES. */
    public static final String DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_JSON_MODULES = "DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_JSON_IMPORTS";

    /** The Constant DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_IMPORT_ASSERTIONS. */
    public static final String DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_IMPORT_ASSERTIONS = "DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_IMPORT_ASSERTIONS";

    /** The graal host access. */
    private static HostAccess graalHostAccess;

    /**
     * Instantiates a new graal JS context creator.
     *
     * @param typeMaps the type maps
     */
    @SuppressWarnings("rawtypes")
    public GraalJSContextCreator(List<GraalJSTypeMap> typeMaps) {
        if (graalHostAccess == null) {
            graalHostAccess = createHostAccess(typeMaps);
        }
    }

    /**
     * Creates the context.
     *
     * @param engine the engine
     * @param workingDirectoryPath the working directory path
     * @param downloadableModuleResolver the downloadable module resolver
     * @param moduleResolvers the module resolvers
     * @param onBeforeContextCreatedHook the on before context created hook
     * @param onAfterContextCreatedHook the on after context created hook
     * @param delegateFileSystem the file system to delegate to
     * @return the context
     */
    public Context createContext(
            Engine engine,
            Path workingDirectoryPath,
            DownloadableModuleResolver downloadableModuleResolver,
            List<ModuleResolver> moduleResolvers,
            Consumer<Context.Builder> onBeforeContextCreatedHook,
            Consumer<Context> onAfterContextCreatedHook,
            Function<Path, Path> onRealPathNotFound,
            FileSystem delegateFileSystem
    ) {
        GraalJSFileSystem graalJSFileSystem = new GraalJSFileSystem(
                workingDirectoryPath,
                moduleResolvers,
                downloadableModuleResolver,
                onRealPathNotFound,
                delegateFileSystem
        );

        Context.Builder contextBuilder = Context.newBuilder()
                .engine(engine)
                .allowEnvironmentAccess(EnvironmentAccess.INHERIT)
                .allowExperimentalOptions(true)
                .currentWorkingDirectory(workingDirectoryPath)
                .fileSystem(graalJSFileSystem);

        onBeforeContextCreatedHook.accept(contextBuilder);

        if (Boolean.parseBoolean(Configuration.get(DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_HOST_ACCESS, "true"))) {
            contextBuilder.allowHostClassLookup(s -> true);
            contextBuilder.allowHostAccess(graalHostAccess);
            contextBuilder.allowAllAccess(true);
        }
        if (Boolean.parseBoolean(Configuration.get(DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_CREATE_THREAD, "true"))) {
            contextBuilder.allowCreateThread(true);
        }
        if (Boolean.parseBoolean(Configuration.get(DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_IO, "true"))) {
            contextBuilder.allowIO(true);
        }
        if (Boolean.parseBoolean(Configuration.get(DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_CREATE_PROCESS, "true"))) {
            contextBuilder.allowCreateProcess(true);
        }
        if (Boolean.parseBoolean(Configuration.get(DIRIGBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_NASHORN, "true"))) {
			contextBuilder.option("js.nashorn-compat", "true");
		}
        if (Boolean.parseBoolean(Configuration.get(DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_IMPORT_ASSERTIONS, "true"))) {
            contextBuilder.option("js.import-assertions", "true");
        }
        if (Boolean.parseBoolean(Configuration.get(DIRIGBLE_JAVASCRIPT_GRAALVM_ALLOW_JSON_MODULES, "true"))) {
            contextBuilder.option("js.json-modules", "true");
        }

        Context context = contextBuilder.build();
        
        if (Boolean.parseBoolean(Configuration.get(DIRIGBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_MOZILLA, "false"))) {
        	context.eval("js", "load(\"nashorn:mozilla_compat.js\")");
        }
        
        onAfterContextCreatedHook.accept(context);
        return context;
    }

    /**
     * Creates the host access.
     *
     * @param typeMaps the type maps
     * @return the host access
     */
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
