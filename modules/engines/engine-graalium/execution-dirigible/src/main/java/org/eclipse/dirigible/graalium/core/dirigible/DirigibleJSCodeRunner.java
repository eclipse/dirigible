package org.eclipse.dirigible.graalium.core.dirigible;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.graalium.core.dirigible.globals.DirigibleContextGlobalObject;
import org.eclipse.dirigible.graalium.core.dirigible.globals.DirigibleEngineTypeGlobalObject;
import org.eclipse.dirigible.graalium.core.dirigible.modules.DirigibleModuleResolver;
import org.eclipse.dirigible.graalium.core.dirigible.polyfills.RequirePolyfill;
import org.eclipse.dirigible.graalium.core.javascript.GraalJSCodeRunner;
import org.eclipse.dirigible.graalium.core.javascript.JSCodeRunner;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.nio.file.Path;
import java.util.HashMap;

public class DirigibleJSCodeRunner implements JSCodeRunner<Source, Value> {

    private final JSCodeRunner<Source, Value> codeRunner;

    public DirigibleJSCodeRunner() {
        var workingDirectoryPath = getDirigibleWorkingDirectory();
        var cachePath = workingDirectoryPath.resolve("caches");
        var coreModulesESMProxiesCachePath = cachePath.resolve("core-modules-proxies-cache");

        codeRunner = GraalJSCodeRunner.newBuilder(workingDirectoryPath, cachePath)
                .addJSPolyfill(new RequirePolyfill())
                .addGlobalObject(new DirigibleContextGlobalObject(new HashMap<>()))
                .addGlobalObject(new DirigibleEngineTypeGlobalObject())
                .addModuleResolver(new DirigibleModuleResolver(coreModulesESMProxiesCachePath))
                .waitForDebugger(DirigibleJSCodeRunner::shouldEnableDebug)
                .build();
    }

    private static boolean shouldEnableDebug() {
        return Configuration.get("DIRIGIBLE_GRAALIUM_ENABLE_DEBUG", Boolean.FALSE.toString()).equals(Boolean.TRUE.toString());
    }

    private Path getDirigibleWorkingDirectory() {
        var repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
        String publicRegistryPath = repository.getInternalResourcePath(IRepositoryStructure.PATH_REGISTRY_PUBLIC);
        return Path.of(publicRegistryPath);
    }

    @Override
    public Value run(Path codeFilePath) {
        return codeRunner.run(codeFilePath);
    }

    @Override
    public Value run(Source codeSource) {
        return codeRunner.run(codeSource);
    }

    @Override
    public void close() {
        codeRunner.close();
    }
}
