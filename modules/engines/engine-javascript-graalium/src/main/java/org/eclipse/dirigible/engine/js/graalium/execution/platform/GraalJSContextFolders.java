package org.eclipse.dirigible.engine.js.graalium.execution.platform;

import java.nio.file.Path;

public class GraalJSContextFolders {

    private final Path workingDirectoryPath;
    private final Path coreModulesProxiesCachePath;
    private final Path dependenciesCachePath;

    public GraalJSContextFolders(Path workingDirectoryPath, Path coreModulesProxiesCachePath, Path dependenciesCachePath) {
        this.workingDirectoryPath = workingDirectoryPath;
        this.coreModulesProxiesCachePath = coreModulesProxiesCachePath;
        this.dependenciesCachePath = dependenciesCachePath;
    }

    public Path getWorkingDirectoryPath() {
        return workingDirectoryPath;
    }

    public Path getCoreModulesProxiesCachePath() {
        return coreModulesProxiesCachePath;
    }

    public Path getDependenciesCachePath() {
        return dependenciesCachePath;
    }
}
