package org.eclipse.dirigible.engine.js.graalvm.execution.js.platform;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.engine.js.graalvm.execution.js.modules.DirigibleCoreModuleResolver;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.EnvironmentAccess;
import org.graalvm.polyglot.HostAccess;

import java.nio.file.Path;
import java.util.function.Consumer;

public class GraalJSContextCreator {
    private static final String DIRIGIBLE_JAVASCRIPT_GRAALVM_ALLOW_HOST_ACCESS = "DIRIGIBLE_JAVASCRIPT_GRAALVM_ALLOW_HOST_ACCESS";
    private static final String DIRIGIBLE_JAVASCRIPT_GRAALVM_ALLOW_CREATE_THREAD = "DIRIGIBLE_JAVASCRIPT_GRAALVM_ALLOW_CREATE_THREAD";
    private static final String DIRIGIBLE_JAVASCRIPT_GRAALVM_ALLOW_IO = "DIRIGIBLE_JAVASCRIPT_GRAALVM_ALLOW_IO";
    private static final String DIRIGIBLE_JAVASCRIPT_GRAALVM_ALLOW_CREATE_PROCESS = "DIRIGIBLE_JAVASCRIPT_GRAALVM_ALLOW_CREATE_PROCESS";

    public static Context createContext(Engine engine, Path currentWorkingDirectoryPath, Consumer<Context.Builder> onBeforeContextCreatedHook, Consumer<Context> onAfterContextCreatedHook) {
        DirigibleCoreModuleResolver dirigibleCoreModuleResolver = new DirigibleCoreModuleResolver();
        GraalJSFileSystem graalJSFileSystem = new GraalJSFileSystem(currentWorkingDirectoryPath, dirigibleCoreModuleResolver);

        Context.Builder contextBuilder = Context.newBuilder()
                .engine(engine)
                .allowEnvironmentAccess(EnvironmentAccess.INHERIT)
                .allowExperimentalOptions(true)
                .currentWorkingDirectory(currentWorkingDirectoryPath)
                .fileSystem(graalJSFileSystem);

        onBeforeContextCreatedHook.accept(contextBuilder);

        if (Boolean.parseBoolean(Configuration.get(DIRIGIBLE_JAVASCRIPT_GRAALVM_ALLOW_HOST_ACCESS, "true"))) {
            contextBuilder.allowHostClassLookup(s -> true);
            contextBuilder.allowHostAccess(HostAccess.ALL);
            contextBuilder.allowAllAccess(true);
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

        Context context = contextBuilder.build();
        onAfterContextCreatedHook.accept(context);
        return context;
    }
}
