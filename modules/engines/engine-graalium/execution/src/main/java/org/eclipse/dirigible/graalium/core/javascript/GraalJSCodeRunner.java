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

public class GraalJSCodeRunner implements JavascriptCodeRunner<Source, Value> {

    private final Path currentWorkingDirectoryPath;
    private final Context graalContext;
    private final GraalJSSourceCreator graalJSSourceCreator;
    private final GraalJSContextCreator graalJSContextCreator;

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

    private void registerPolyfills(Context context, List<JavascriptPolyfill> jsPolyfills) {
        jsPolyfills
                .stream()
                .map(polyfill -> graalJSSourceCreator.createInternalSource(polyfill.getSource(), polyfill.getFileName()))
                .forEach(context::eval);
    }

    @Override
    public Value run(Path codeFilePath) {
        Path relativeCodeFilePath = currentWorkingDirectoryPath.resolve(codeFilePath);
        Source codeSource = graalJSSourceCreator.createSource(relativeCodeFilePath);
        return run(codeSource);
    }

    @Override
    public Value run(Source codeSource) {
        Value result = graalContext.eval(codeSource);
        rethrowIfError(result);
        return result;
    }

    public Path getCurrentWorkingDirectoryPath() {
        return currentWorkingDirectoryPath;
    }

    public void addGlobalObject(JSGlobalObject jsGlobalObject) {
        registerGlobalObjects(graalContext, Collections.singletonList(jsGlobalObject));
    }

    public Value parse(Path codeFilePath) {
        Path relativeCodeFilePath = currentWorkingDirectoryPath.resolve(codeFilePath);
        Source codeSource = graalJSSourceCreator.createSource(relativeCodeFilePath);
        return parse(codeSource);
    }

    public Value parse(Source codeSource) {
        return graalContext.parse(codeSource);
    }

    public void leave() {
        graalContext.leave();
    }

    private static void rethrowIfError(Value maybeError) {
        if (maybeError.isException()) {
            throw maybeError.throwException();
        }
    }

    public static Builder newBuilder(Path currentWorkingDirectoryPath, Path cachesPath) {
        return new Builder(currentWorkingDirectoryPath, cachesPath);
    }

    @Override
    public void close() {
        if (graalContext != null) {
            graalContext.close(false);
        }
    }

    public static class Builder {
        private final Path workingDirectoryPath;
        private final Path dependenciesCachePath;
        private boolean waitForDebugger = false;
        private JavascriptModuleType jsModuleType = JavascriptModuleType.BASED_ON_FILE_EXTENSION;
        private final List<JavascriptPolyfill> jsPolyfills = new ArrayList<>();
        private final List<JSGlobalObject> globalObjects = new ArrayList<>();
        private final List<Consumer<Context.Builder>> onBeforeContextCreatedListeners = new ArrayList<>();
        private final List<Consumer<Context>> onAfterContextCreatedListener = new ArrayList<>();
        private final List<ModuleResolver> moduleResolvers = new ArrayList<>();
        @SuppressWarnings("rawtypes")
        private final List<GraalJSTypeMap> typeMaps = new ArrayList<>();

        public Builder(Path workingDirectoryPath, Path cachesPath) {
            this.workingDirectoryPath = workingDirectoryPath;
            this.dependenciesCachePath = cachesPath.resolve("dependencies-cache");
        }

        public Builder withJSModuleType(JavascriptModuleType jsModuleType) {
            this.jsModuleType = jsModuleType;
            return this;
        }

        public Builder waitForDebugger(boolean shouldWaitForDebugger) {
            waitForDebugger = shouldWaitForDebugger;
            return this;
        }

        public Builder waitForDebugger(Supplier<Boolean> shouldWaitForDebugger) {
            waitForDebugger = shouldWaitForDebugger.get();
            return this;
        }

        public Builder addJSPolyfill(JavascriptPolyfill jsPolyfill) {
            jsPolyfills.add(jsPolyfill);
            return this;
        }

        public Builder addGlobalObject(JSGlobalObject jsGlobalObject) {
            globalObjects.add(jsGlobalObject);
            return this;
        }

        public Builder addModuleResolver(ModuleResolver moduleResolver) {
            moduleResolvers.add(moduleResolver);
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

        public <S, T> Builder addTypeMapping(Class<S> source, Class<T> target, Function<S, T> converter) {
            this.typeMaps.add(new GraalJSTypeMap<>(source, target, converter));
            return this;
        }

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
