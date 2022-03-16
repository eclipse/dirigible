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
package org.eclipse.dirigible.engine.js.graalvm.execution.js;

import org.eclipse.dirigible.engine.js.graalvm.execution.CodeRunner;
import org.eclipse.dirigible.engine.js.graalvm.execution.js.platform.GraalJSContextCreator;
import org.eclipse.dirigible.engine.js.graalvm.execution.js.platform.GraalJSEngineCreator;
import org.eclipse.dirigible.engine.js.graalvm.execution.js.platform.GraalJSSourceCreator;
import org.eclipse.dirigible.engine.js.graalvm.execution.js.polyfills.JSGlobalObject;
import org.eclipse.dirigible.engine.js.graalvm.execution.js.polyfills.JSPolyfill;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GraalJSCodeRunner implements CodeRunner {

    private final Path currentWorkingDirectoryPath;
    private final Engine graalEngine;
    private final Context graalContext;

    private GraalJSCodeRunner(Builder builder) {
        this.currentWorkingDirectoryPath = builder.currentWorkingDirectoryPath;

        Consumer<Context.Builder> onBeforeContextCreatedHook = provideOnBeforeContextCreatedHook(builder.onBeforeContextCreatedListeners);
        Consumer<Context> onAfterContextCreatedHook = provideOnAfterContextCreatedHook(builder.onAfterContextCreatedListener);

        graalEngine = builder.waitForDebugger ? GraalJSEngineCreator.getOrCreateDebuggableEngine() : GraalJSEngineCreator.getOrCreateEngine();
        graalContext = GraalJSContextCreator.createContext(graalEngine, currentWorkingDirectoryPath, onBeforeContextCreatedHook, onAfterContextCreatedHook);

        registerGlobalObjects(graalContext, builder.globalObjects);
        registerPolyfills(graalContext, builder.jsPolyfills);
    }

    private static Consumer<Context.Builder> provideOnBeforeContextCreatedHook(List<Consumer<Context.Builder>> onBeforeContextCreatedListeners) {
        return contextBuilder -> onBeforeContextCreatedListeners.forEach(x -> x.accept(contextBuilder));
    }

    private static Consumer<Context> provideOnAfterContextCreatedHook(List<Consumer<Context>> onAfterContextCreatedListeners) {
        return context -> onAfterContextCreatedListeners.forEach(x -> x.accept(context));
    }

    private static void registerGlobalObjects(Context context, List<JSGlobalObject> globalObjects) {
        Value contextBindings = context.getBindings("js");
        globalObjects.forEach(global -> contextBindings.putMember(global.getName(), global.getValue()));
    }

    private static void registerPolyfills(Context context, List<JSPolyfill> jsPolyfills) {
        jsPolyfills
                .stream()
                .map(polyfill -> GraalJSSourceCreator.createSource(polyfill.getSource(), polyfill.getFileName()))
                .forEach(context::eval);
    }

    @Override
    public Value run(Path codeFilePath) {
        Path relativeCodeFilePath = currentWorkingDirectoryPath.resolve(codeFilePath);
        Source codeSource = GraalJSSourceCreator.createSource(relativeCodeFilePath);
        Value result = graalContext.eval(codeSource);
        rethrowIfError(result);
        return result;
    }

    private static void rethrowIfError(Value maybeError) {
        if (maybeError.isException()) {
            throw maybeError.throwException();
        }
    }

    public static class Builder {
        private final Path currentWorkingDirectoryPath;
        private boolean waitForDebugger = false;
        private final List<JSPolyfill> jsPolyfills = new ArrayList<>();
        private final List<JSGlobalObject> globalObjects = new ArrayList<>();
        private final List<Consumer<Context.Builder>> onBeforeContextCreatedListeners = new ArrayList<>();
        private final List<Consumer<Context>> onAfterContextCreatedListener = new ArrayList<>();

        public Builder(Path currentWorkingDirectoryPath) {
            this.currentWorkingDirectoryPath = currentWorkingDirectoryPath;
        }

        public Builder waitForDebugger(boolean shouldWaitForDebugger) {
            waitForDebugger = shouldWaitForDebugger;
            return this;
        }

        public Builder addJSPolyfill(JSPolyfill jsPolyfill) {
            jsPolyfills.add(jsPolyfill);
            return this;
        }

        public Builder addGlobalObject(JSGlobalObject jsGlobalObject) {
            globalObjects.add(jsGlobalObject);
            return this;
        }

        public Builder addOnBeforeContextCreatedListener(Consumer<Context.Builder> onBeforeContextCreatedListener) {
            onBeforeContextCreatedListeners.add(onBeforeContextCreatedListener);
            return this;
        }

        public Builder addOnAfterContextCreatedListener(Consumer<Context> onAfterContextCreatedListener) {
            this.onAfterContextCreatedListener.add(onAfterContextCreatedListener);
            return this;
        }

        public GraalJSCodeRunner build() throws IllegalStateException {
            return new GraalJSCodeRunner(this);
        }

    }
}
