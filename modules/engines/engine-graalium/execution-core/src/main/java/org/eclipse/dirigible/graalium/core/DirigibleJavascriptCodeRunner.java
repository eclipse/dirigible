/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.graalium.core;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.graalium.core.globals.DirigibleContextGlobalObject;
import org.eclipse.dirigible.graalium.core.globals.DirigibleEngineTypeGlobalObject;
import org.eclipse.dirigible.graalium.core.javascript.GraalJSCodeRunner;
import org.eclipse.dirigible.graalium.core.javascript.GraalJSInterceptor;
import org.eclipse.dirigible.graalium.core.javascript.modules.Module;
import org.eclipse.dirigible.graalium.core.javascript.modules.ModuleType;
import org.eclipse.dirigible.graalium.core.javascript.modules.java.JavaModuleResolver;
import org.eclipse.dirigible.graalium.core.modules.DirigibleEsmModuleResolver;
import org.eclipse.dirigible.graalium.core.modules.DirigibleModuleResolver;
import org.eclipse.dirigible.graalium.core.modules.DirigibleSourceProvider;
import org.eclipse.dirigible.graalium.core.polyfills.RequirePolyfill;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

/**
 * The Class DirigibleJavascriptCodeRunner.
 */
public class DirigibleJavascriptCodeRunner implements CodeRunner<Source, Value> {

    /** ESM files extension */
    private static final String JS_EXT = ".js";

    /** ESM files extension */
    private static final String MJS_EXT = ".mjs";

    /** TypeScript files extension */
    private static final String TS_EXT = ".ts";

    /** The code runner. */
    private final GraalJSCodeRunner codeRunner;

    private final JavascriptSourceProvider sourceProvider;

    /** The Constant DIRIGIBLE_JAVASCRIPT_HOOKS_PROVIDERS. */
    private static final ServiceLoader<DirigibleJavascriptHooksProvider> DIRIGIBLE_JAVASCRIPT_HOOKS_PROVIDERS =
            ServiceLoader.load(DirigibleJavascriptHooksProvider.class);

    /** The Constant DIRIGIBLE_JAVASCRIPT_INTERCEPTORS. */
    private static final ServiceLoader<GraalJSInterceptor> DIRIGIBLE_JAVASCRIPT_INTERCEPTORS = ServiceLoader.load(GraalJSInterceptor.class);

    /** The interceptor. */
    private final GraalJSInterceptor interceptor;

    /**
     * Instantiates a new dirigible javascript code runner.
     */
    public DirigibleJavascriptCodeRunner() {
        this(new HashMap<>(), false);
    }

    /**
     * Instantiates a new dirigible javascript code runner.
     *
     * @param shouldEnableDebug the debug
     */
    public DirigibleJavascriptCodeRunner(boolean shouldEnableDebug) {
        this(new HashMap<>(), shouldEnableDebug);
    }

    /**
     * Instantiates a new dirigible javascript code runner.
     *
     * @param context the parameters
     * @param shouldEnableDebug the debug
     */
    public DirigibleJavascriptCodeRunner(Map<Object, Object> context, boolean shouldEnableDebug) {
        this(context, shouldEnableDebug, new DirigibleSourceProvider());
    }

    /**
     * Instantiates a new dirigible javascript code runner.
     *
     * @param context the parameters
     * @param shouldEnableDebug the debug
     */
    public DirigibleJavascriptCodeRunner(Map<Object, Object> context, boolean shouldEnableDebug, JavascriptSourceProvider sourceProvider) {
        this.sourceProvider = sourceProvider;

        Path workingDirectoryPath = getDirigibleWorkingDirectory();
        Consumer<Context.Builder> onBeforeContextCreatedListener = null;
        Consumer<Context> onAfterContextCreatedListener = null;
        if (DIRIGIBLE_JAVASCRIPT_HOOKS_PROVIDERS.iterator()
                                                .hasNext()) {
            DirigibleJavascriptHooksProvider dirigibleJavascriptHooksProvider = DIRIGIBLE_JAVASCRIPT_HOOKS_PROVIDERS.iterator()
                                                                                                                    .next();
            onBeforeContextCreatedListener = dirigibleJavascriptHooksProvider.getOnBeforeContextCreatedListener();
            onAfterContextCreatedListener = dirigibleJavascriptHooksProvider.getOnAfterContextCreatedListener();
        }
        if (DIRIGIBLE_JAVASCRIPT_INTERCEPTORS.iterator()
                                             .hasNext()) {
            interceptor = DIRIGIBLE_JAVASCRIPT_INTERCEPTORS.iterator()
                                                           .next();
        } else {
            interceptor = new DirigibleJavascriptInterceptor(this);
        }

        codeRunner = createCodeRunner(workingDirectoryPath, context, shouldEnableDebug, onBeforeContextCreatedListener,
                onAfterContextCreatedListener);
    }

    private GraalJSCodeRunner createCodeRunner(Path workingDirectoryPath, Map<Object, Object> context, boolean shouldEnableDebug,
            Consumer<Context.Builder> onBeforeContextCreatedListener, Consumer<Context> onAfterContextCreatedListener) {
        Path cachePath = workingDirectoryPath.resolve("caches");
        Path coreModulesESMProxiesCachePath = cachePath.resolve("core-modules-proxies-cache");
        Path javaModulesESMProxiesCachePath = cachePath.resolve("java-modules-proxies-cache");

        return GraalJSCodeRunner.newBuilder(workingDirectoryPath, cachePath)
                                .addJSPolyfill(new RequirePolyfill())
                                .addGlobalObject(new DirigibleContextGlobalObject(context))
                                .addGlobalObject(new DirigibleEngineTypeGlobalObject())
                                .addModuleResolver(new JavaModuleResolver(javaModulesESMProxiesCachePath))
                                .addModuleResolver(new DirigibleModuleResolver(coreModulesESMProxiesCachePath, sourceProvider))
                                .addModuleResolver(new DirigibleEsmModuleResolver(sourceProvider))
                                .waitForDebugger(shouldEnableDebug && DirigibleJavascriptCodeRunner.shouldEnableDebug())
                                .addOnBeforeContextCreatedListener(onBeforeContextCreatedListener)
                                .addOnAfterContextCreatedListener(onAfterContextCreatedListener)
                                .setOnRealPathNotFound(p -> sourceProvider.unpackedToFileSystem(p, workingDirectoryPath.relativize(p)))
                                .setInterceptor(interceptor)
                                .build();
    }

    /**
     * Prepare the Source to be run
     *
     * @param codeFilePath the code file path to use
     * @return the source
     */
    @Override
    public Source prepareSource(Path codeFilePath) {
        return codeRunner.prepareSource(codeFilePath);
    }

    /**
     * Run the given source.
     *
     * @param codeSource the code source
     * @return the value
     */
    @Override
    public Value run(Source codeSource) {
        return codeRunner.run(codeSource);
    }

    public Module run(String codeFilePath) {
        Path path = Path.of(codeFilePath);
        return run(path);
    }

    public Module run(Path codeFilePath) {
        var pathAsString = codeFilePath.toString();
        ModuleType moduleType = pathAsString.endsWith(MJS_EXT) || pathAsString.endsWith((TS_EXT)) ? ModuleType.ESM : ModuleType.CJS;
        if (pathAsString.endsWith(TS_EXT)) {
            pathAsString = transformTypeScriptHandlerPathIfNecessary(pathAsString);
        }
        Source source = prepareSource(Path.of(pathAsString));
        Value module = run(source);
        return new Module(module, moduleType);
    }

    public Value runMethod(Module codeModule, String methodName, Object... args) {
        return switch (codeModule.moduleType()) {
            case CJS -> runCjsMethod(codeModule.module(), methodName, args);
            case ESM -> runEsmMethod(codeModule.module(), methodName, args);
            default -> throw new IllegalArgumentException("Unsupported module type: " + codeModule.moduleType());
        };
    }

    private Value runEsmMethod(Value module, String methodName, Object... args) {
        Value onMessage = module.getMember(methodName);
        return onMessage.execute(args);
    }

    private Value runCjsMethod(Value module, String methodName, Object... args) {
        Value onMessage = module.getContext()
                                .getBindings("js")
                                .getMember("exports")
                                .getMember(methodName);
        return onMessage.execute(args);
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
        return Configuration.get("DIRIGIBLE_GRAALIUM_ENABLE_DEBUG", Boolean.FALSE.toString())
                            .equals(Boolean.TRUE.toString());
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

    public JavascriptSourceProvider getSourceProvider() {
        return sourceProvider;
    }

    /**
     * Gets the graal JS interceptor.
     *
     * @return the graal JS interceptor
     */
    public GraalJSInterceptor getGraalJSInterceptor() {
        return interceptor;
    }

    private static String transformTypeScriptHandlerPathIfNecessary(String handlerPath) {
        return handlerPath.endsWith(TS_EXT) ? handlerPath.replaceAll(Pattern.quote(TS_EXT), JS_EXT) : handlerPath;
    }

    /**
     * Close.
     */
    @Override
    public void close() {
        codeRunner.close();
    }
}
