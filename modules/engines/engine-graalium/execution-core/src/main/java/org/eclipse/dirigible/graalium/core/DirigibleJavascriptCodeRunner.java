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
package org.eclipse.dirigible.graalium.core;

import java.nio.file.Path;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Consumer;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.graalium.core.globals.DirigibleContextGlobalObject;
import org.eclipse.dirigible.graalium.core.globals.DirigibleEngineTypeGlobalObject;
import org.eclipse.dirigible.graalium.core.graal.GraalJSInterceptor;
import org.eclipse.dirigible.graalium.core.javascript.GraalJSCodeRunner;
import org.eclipse.dirigible.graalium.core.javascript.JavascriptCodeRunner;
import org.eclipse.dirigible.graalium.core.modules.DirigibleModuleResolver;
import org.eclipse.dirigible.graalium.core.polyfills.RequirePolyfill;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

/**
 * The Class DirigibleJavascriptCodeRunner.
 */
public class DirigibleJavascriptCodeRunner implements JavascriptCodeRunner<Source, Value> {

    /** The code runner. */
    private final GraalJSCodeRunner codeRunner;
    
    /** The Constant DIRIGIBLE_JAVASCRIPT_HOOKS_PROVIDERS. */
    private static final ServiceLoader<DirigibleJavascriptHooksProvider> DIRIGIBLE_JAVASCRIPT_HOOKS_PROVIDERS = ServiceLoader.load(DirigibleJavascriptHooksProvider.class);
    
    /** The Constant DIRIGIBLE_JAVASCRIPT_INTERCEPTORS. */
    private static final ServiceLoader<GraalJSInterceptor> DIRIGIBLE_JAVASCRIPT_INTERCEPTORS = ServiceLoader.load(GraalJSInterceptor.class);
    
    /** The interceptor. */
    private final GraalJSInterceptor interceptor;

    /**
     * Instantiates a new dirigible javascript code runner.
     *
     * @param parameters the parameters
     * @param debug the debug
     */
    public DirigibleJavascriptCodeRunner(Map<Object, Object> parameters, boolean debug) {
        Path workingDirectoryPath = getDirigibleWorkingDirectory();
        Path cachePath = workingDirectoryPath.resolve("caches");
        Path coreModulesESMProxiesCachePath = cachePath.resolve("core-modules-proxies-cache");
        
        Consumer<Context.Builder> onBeforeContextCreatedListener = null;
        Consumer<Context> onAfterContextCreatedListener = null;
        if (DIRIGIBLE_JAVASCRIPT_HOOKS_PROVIDERS.iterator().hasNext()) {
        	DirigibleJavascriptHooksProvider dirigibleJavascriptHooksProvider = DIRIGIBLE_JAVASCRIPT_HOOKS_PROVIDERS.iterator().next();
        	onBeforeContextCreatedListener = dirigibleJavascriptHooksProvider.getOnBeforeContextCreatedListener(); 
        	onAfterContextCreatedListener = dirigibleJavascriptHooksProvider.getOnAfterContextCreatedListener();
        }
        if (DIRIGIBLE_JAVASCRIPT_INTERCEPTORS.iterator().hasNext()) {
        	interceptor = DIRIGIBLE_JAVASCRIPT_INTERCEPTORS.iterator().next();
        } else {
        	interceptor = new DirigibleJavascriptInterceptor(this);	
        }

        codeRunner = GraalJSCodeRunner.newBuilder(workingDirectoryPath, cachePath)
                .addJSPolyfill(new RequirePolyfill())
                .addGlobalObject(new DirigibleContextGlobalObject(parameters))
                .addGlobalObject(new DirigibleEngineTypeGlobalObject())
                .addModuleResolver(new DirigibleModuleResolver(coreModulesESMProxiesCachePath))
                .waitForDebugger(debug && DirigibleJavascriptCodeRunner.shouldEnableDebug())
                .addOnBeforeContextCreatedListener(onBeforeContextCreatedListener != null ? onBeforeContextCreatedListener : null)
                .addOnAfterContextCreatedListener(onAfterContextCreatedListener != null ? onAfterContextCreatedListener : null)
                .setInterceptor(interceptor)
                .build();
    }
    
    /**
     * Gets the code runner.
     *
     * @return the code runner
     */
    public GraalJSCodeRunner getCodeRunner() {
		return codeRunner;
	}

    /**
     * Should enable debug.
     *
     * @return true, if successful
     */
    private static boolean shouldEnableDebug() {
        return Configuration.get("DIRIGIBLE_GRAALIUM_ENABLE_DEBUG", Boolean.FALSE.toString()).equals(Boolean.TRUE.toString());
    }

    /**
     * Gets the dirigible working directory.
     *
     * @return the dirigible working directory
     */
    private Path getDirigibleWorkingDirectory() {
    	IRepository repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
        String publicRegistryPath = repository.getInternalResourcePath(IRepositoryStructure.PATH_REGISTRY_PUBLIC);
        return Path.of(publicRegistryPath);
    }

    /**
     * Run.
     *
     * @param codeFilePath the code file path
     * @return the source
     */
    @Override
    public Source prepareSource(Path codeFilePath) {
        return codeRunner.prepareSource(codeFilePath);
    }

    /**
     * Run.
     *
     * @param codeSource the code source
     * @return the value
     */
    @Override
    public Value run(Source codeSource) {
        return codeRunner.run(codeSource);
    }

    /**
     * Close.
     */
    @Override
    public void close() {
        codeRunner.close();
    }

	/**
	 * Gets the graal JS interceptor.
	 *
	 * @return the graal JS interceptor
	 */
	public GraalJSInterceptor getGraalJSInterceptor() {
		return interceptor;
	}
}
