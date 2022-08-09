package org.eclipse.dirigible.graalium.core.graal;

import org.eclipse.dirigible.graalium.core.graal.configuration.Configuration;
import org.eclipse.dirigible.graalium.core.graal.modules.downloadable.DownloadableModuleResolver;
import org.eclipse.dirigible.graalium.core.graal.modules.ModuleResolver;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.EnvironmentAccess;
import org.graalvm.polyglot.HostAccess;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

/**
 * The Class GraalJSContextCreator.
 */
public class GraalJSContextCreator {
    
    /** The Constant JAVASCRIPT_GRAALVM_ALLOW_HOST_ACCESS. */
    private static final String JAVASCRIPT_GRAALVM_ALLOW_HOST_ACCESS = "JAVASCRIPT_GRAALVM_ALLOW_HOST_ACCESS";
    
    /** The Constant JAVASCRIPT_GRAALVM_ALLOW_CREATE_THREAD. */
    private static final String JAVASCRIPT_GRAALVM_ALLOW_CREATE_THREAD = "JAVASCRIPT_GRAALVM_ALLOW_CREATE_THREAD";
    
    /** The Constant JAVASCRIPT_GRAALVM_ALLOW_IO. */
    private static final String JAVASCRIPT_GRAALVM_ALLOW_IO = "JAVASCRIPT_GRAALVM_ALLOW_IO";
    
    /** The Constant JAVASCRIPT_GRAALVM_ALLOW_CREATE_PROCESS. */
    private static final String JAVASCRIPT_GRAALVM_ALLOW_CREATE_PROCESS = "JAVASCRIPT_GRAALVM_ALLOW_CREATE_PROCESS";

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
     * @return the context
     */
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
