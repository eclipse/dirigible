package org.eclipse.dirigible.graalium.core;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.graalium.core.globals.DirigibleContextGlobalObject;
import org.eclipse.dirigible.graalium.core.globals.DirigibleEngineTypeGlobalObject;
import org.eclipse.dirigible.graalium.core.javascript.GraalJSCodeRunner;
import org.eclipse.dirigible.graalium.core.javascript.JavascriptCodeRunner;
import org.eclipse.dirigible.graalium.core.modules.DirigibleModuleResolver;
import org.eclipse.dirigible.graalium.core.polyfills.RequirePolyfill;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.nio.file.Path;
import java.util.HashMap;

/**
 * The Class DirigibleJavascriptCodeRunner.
 */
public class DirigibleJavascriptCodeRunner implements JavascriptCodeRunner<Source, Value> {

    /** The code runner. */
    private final JavascriptCodeRunner<Source, Value> codeRunner;

    /**
     * Instantiates a new dirigible javascript code runner.
     *
     * @param debug the debug
     */
    public DirigibleJavascriptCodeRunner(boolean debug) {
        Path workingDirectoryPath = getDirigibleWorkingDirectory();
        Path cachePath = workingDirectoryPath.resolve("caches");
        Path coreModulesESMProxiesCachePath = cachePath.resolve("core-modules-proxies-cache");

        codeRunner = GraalJSCodeRunner.newBuilder(workingDirectoryPath, cachePath)
                .addJSPolyfill(new RequirePolyfill())
                .addGlobalObject(new DirigibleContextGlobalObject(new HashMap<>()))
                .addGlobalObject(new DirigibleEngineTypeGlobalObject())
                .addModuleResolver(new DirigibleModuleResolver(coreModulesESMProxiesCachePath))
                .waitForDebugger(debug && DirigibleJavascriptCodeRunner.shouldEnableDebug())
                .build();
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
     * @return the value
     */
    @Override
    public Value run(Path codeFilePath) {
        return codeRunner.run(codeFilePath);
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
}
