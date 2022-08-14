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
package org.eclipse.dirigible.graalium.core.javascript;

import org.eclipse.dirigible.graalium.core.graal.GraalJSEngineCreator;
import org.eclipse.dirigible.graalium.core.graal.GraalJSSourceCreator;
import org.eclipse.dirigible.graalium.core.graal.GraalJSTypeMap;
import org.eclipse.dirigible.graalium.core.graal.modules.downloadable.DownloadableModuleResolver;
import org.eclipse.dirigible.graalium.core.graal.modules.ModuleResolver;
import org.eclipse.dirigible.graalium.core.graal.globals.JSGlobalObject;
import org.eclipse.dirigible.graalium.core.graal.polyfills.JavascriptPolyfill;
import org.eclipse.dirigible.graalium.core.graal.GraalJSContextCreator;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The Class GraalJSCodeRunner.
 */
public class GraalJSCodeRunner implements JavascriptCodeRunner<Source, Value> {

    /** The current working directory path. */
    private final Path currentWorkingDirectoryPath;
    
    /** The graal context. */
    private final Context graalContext;
    
    /** The graal JS source creator. */
    private final GraalJSSourceCreator graalJSSourceCreator;
    
    /** The graal JS context creator. */
    private final GraalJSContextCreator graalJSContextCreator;

    /**
     * Instantiates a new graal JS code runner.
     *
     * @param builder the builder
     */
    private GraalJSCodeRunner(Builder builder) {
        this.currentWorkingDirectoryPath = builder.workingDirectoryPath;

        Consumer<Context.Builder> onBeforeContextCreatedHook = provideOnBeforeContextCreatedHook(builder.onBeforeContextCreatedListeners);
        Consumer<Context> onAfterContextCreatedHook = provideOnAfterContextCreatedHook(builder.onAfterContextCreatedListener);

        Engine graalEngine = builder.waitForDebugger ? GraalJSEngineCreator.getOrCreateDebuggableEngine() : GraalJSEngineCreator.getOrCreateEngine();
        DownloadableModuleResolver downloadableModuleResolver = new DownloadableModuleResolver(builder.dependenciesCachePath);

        graalJSSourceCreator = new GraalJSSourceCreator(builder.jsModuleType);
        graalJSContextCreator = new GraalJSContextCreator(builder.typeMaps);

        graalContext = graalJSContextCreator.createContext(
                graalEngine,
                currentWorkingDirectoryPath,
                downloadableModuleResolver,
                builder.moduleResolvers,
                onBeforeContextCreatedHook,
                onAfterContextCreatedHook
        );

        registerGlobalObjects(graalContext, builder.globalObjects);
        registerPolyfills(graalContext, builder.jsPolyfills);
    }

    /**
     * Provide on before context created hook.
     *
     * @param onBeforeContextCreatedListeners the on before context created listeners
     * @return the consumer
     */
    private static Consumer<Context.Builder> provideOnBeforeContextCreatedHook(List<Consumer<Context.Builder>> onBeforeContextCreatedListeners) {
        return contextBuilder -> onBeforeContextCreatedListeners.forEach(x -> x.accept(contextBuilder));
    }

    /**
     * Provide on after context created hook.
     *
     * @param onAfterContextCreatedListeners the on after context created listeners
     * @return the consumer
     */
    private static Consumer<Context> provideOnAfterContextCreatedHook(List<Consumer<Context>> onAfterContextCreatedListeners) {
        return context -> onAfterContextCreatedListeners.forEach(x -> x.accept(context));
    }

    /**
     * Register global objects.
     *
     * @param context the context
     * @param globalObjects the global objects
     */
    private static void registerGlobalObjects(Context context, List<JSGlobalObject> globalObjects) {
        Value contextBindings = context.getBindings("js");
        globalObjects.forEach(global -> contextBindings.putMember(global.getName(), global.getValue()));
    }

    /**
     * Register polyfills.
     *
     * @param context the context
     * @param jsPolyfills the js polyfills
     */
    private void registerPolyfills(Context context, List<JavascriptPolyfill> jsPolyfills) {
        jsPolyfills
                .stream()
                .map(polyfill -> graalJSSourceCreator.createInternalSource(polyfill.getSource(), polyfill.getFileName()))
                .forEach(context::eval);
    }

    /**
     * Run.
     *
     * @param codeFilePath the code file path
     * @return the value
     */
    @Override
    public Value run(Path codeFilePath) {
        Path relativeCodeFilePath = currentWorkingDirectoryPath.resolve(codeFilePath);
        Source codeSource = graalJSSourceCreator.createSource(relativeCodeFilePath);
        return run(codeSource);
    }

    /**
     * Run.
     *
     * @param codeSource the code source
     * @return the value
     */
    @Override
    public Value run(Source codeSource) {
        Value result = graalContext.eval(codeSource);
        rethrowIfError(result);
        return result;
    }

    /**
     * Gets the current working directory path.
     *
     * @return the current working directory path
     */
    public Path getCurrentWorkingDirectoryPath() {
        return currentWorkingDirectoryPath;
    }

    /**
     * Adds the global object.
     *
     * @param jsGlobalObject the js global object
     */
    public void addGlobalObject(JSGlobalObject jsGlobalObject) {
        registerGlobalObjects(graalContext, Collections.singletonList(jsGlobalObject));
    }

    /**
     * Parses the.
     *
     * @param codeFilePath the code file path
     * @return the value
     */
    public Value parse(Path codeFilePath) {
        Path relativeCodeFilePath = currentWorkingDirectoryPath.resolve(codeFilePath);
        Source codeSource = graalJSSourceCreator.createSource(relativeCodeFilePath);
        return parse(codeSource);
    }

    /**
     * Parses the.
     *
     * @param codeSource the code source
     * @return the value
     */
    public Value parse(Source codeSource) {
        return graalContext.parse(codeSource);
    }

    /**
     * Leave.
     */
    public void leave() {
        graalContext.leave();
    }

    /**
     * Rethrow if error.
     *
     * @param maybeError the maybe error
     */
    private static void rethrowIfError(Value maybeError) {
        if (maybeError.isException()) {
            throw maybeError.throwException();
        }
    }

    /**
     * New builder.
     *
     * @param currentWorkingDirectoryPath the current working directory path
     * @param cachesPath the caches path
     * @return the builder
     */
    public static Builder newBuilder(Path currentWorkingDirectoryPath, Path cachesPath) {
        return new Builder(currentWorkingDirectoryPath, cachesPath);
    }

    /**
     * Close.
     */
    @Override
    public void close() {
        if (graalContext != null) {
            graalContext.close(false);
        }
    }

    /**
     * The Class Builder.
     */
    public static class Builder {
        
        /** The working directory path. */
        private final Path workingDirectoryPath;
        
        /** The dependencies cache path. */
        private final Path dependenciesCachePath;
        
        /** The wait for debugger. */
        private boolean waitForDebugger = false;
        
        /** The js module type. */
        private JavascriptModuleType jsModuleType = JavascriptModuleType.BASED_ON_FILE_EXTENSION;
        
        /** The js polyfills. */
        private final List<JavascriptPolyfill> jsPolyfills = new ArrayList<>();
        
        /** The global objects. */
        private final List<JSGlobalObject> globalObjects = new ArrayList<>();
        
        /** The on before context created listeners. */
        private final List<Consumer<Context.Builder>> onBeforeContextCreatedListeners = new ArrayList<>();
        
        /** The on after context created listener. */
        private final List<Consumer<Context>> onAfterContextCreatedListener = new ArrayList<>();
        
        /** The module resolvers. */
        private final List<ModuleResolver> moduleResolvers = new ArrayList<>();
        
        /** The type maps. */
        @SuppressWarnings("rawtypes")
        private final List<GraalJSTypeMap> typeMaps = new ArrayList<>();

        /**
         * Instantiates a new builder.
         *
         * @param workingDirectoryPath the working directory path
         * @param cachesPath the caches path
         */
        public Builder(Path workingDirectoryPath, Path cachesPath) {
            this.workingDirectoryPath = workingDirectoryPath;
            this.dependenciesCachePath = cachesPath.resolve("dependencies-cache");
        }

        /**
         * With JS module type.
         *
         * @param jsModuleType the js module type
         * @return the builder
         */
        public Builder withJSModuleType(JavascriptModuleType jsModuleType) {
            this.jsModuleType = jsModuleType;
            return this;
        }

        /**
         * Wait for debugger.
         *
         * @param shouldWaitForDebugger the should wait for debugger
         * @return the builder
         */
        public Builder waitForDebugger(boolean shouldWaitForDebugger) {
            waitForDebugger = shouldWaitForDebugger;
            return this;
        }

        /**
         * Wait for debugger.
         *
         * @param shouldWaitForDebugger the should wait for debugger
         * @return the builder
         */
        public Builder waitForDebugger(Supplier<Boolean> shouldWaitForDebugger) {
            waitForDebugger = shouldWaitForDebugger.get();
            return this;
        }

        /**
         * Adds the JS polyfill.
         *
         * @param jsPolyfill the js polyfill
         * @return the builder
         */
        public Builder addJSPolyfill(JavascriptPolyfill jsPolyfill) {
            jsPolyfills.add(jsPolyfill);
            return this;
        }

        /**
         * Adds the global object.
         *
         * @param jsGlobalObject the js global object
         * @return the builder
         */
        public Builder addGlobalObject(JSGlobalObject jsGlobalObject) {
            globalObjects.add(jsGlobalObject);
            return this;
        }

        /**
         * Adds the module resolver.
         *
         * @param moduleResolver the module resolver
         * @return the builder
         */
        public Builder addModuleResolver(ModuleResolver moduleResolver) {
            moduleResolvers.add(moduleResolver);
            return this;
        }

        /**
         * Adds the on before context created listener.
         *
         * @param onBeforeContextCreatedListener the on before context created listener
         * @return the builder
         */
        public Builder addOnBeforeContextCreatedListener(Consumer<Context.Builder> onBeforeContextCreatedListener) {
            onBeforeContextCreatedListeners.add(onBeforeContextCreatedListener);
            return this;
        }

        /**
         * Adds the on after context created listener.
         *
         * @param onAfterContextCreatedListener the on after context created listener
         * @return the builder
         */
        public Builder addOnAfterContextCreatedListener(Consumer<Context> onAfterContextCreatedListener) {
            this.onAfterContextCreatedListener.add(onAfterContextCreatedListener);
            return this;
        }

        /**
         * Adds the type mapping.
         *
         * @param <S> the generic type
         * @param <T> the generic type
         * @param source the source
         * @param target the target
         * @param converter the converter
         * @return the builder
         */
        public <S, T> Builder addTypeMapping(Class<S> source, Class<T> target, Function<S, T> converter) {
            this.typeMaps.add(new GraalJSTypeMap<>(source, target, converter));
            return this;
        }

        /**
         * Builds the.
         *
         * @return the graal JS code runner
         * @throws IllegalStateException the illegal state exception
         */
        public GraalJSCodeRunner build() throws IllegalStateException {
            if (workingDirectoryPath == null
                    || dependenciesCachePath == null
            ) {
                throw new RuntimeException("Please, provide all folder paths!");
            }

            return new GraalJSCodeRunner(this);
        }

    }
}
