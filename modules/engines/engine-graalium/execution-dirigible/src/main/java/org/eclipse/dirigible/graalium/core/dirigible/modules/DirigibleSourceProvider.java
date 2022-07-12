package org.eclipse.dirigible.graalium.core.dirigible.modules;

import org.eclipse.dirigible.graalium.core.javascript.CalledFromJS;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@CalledFromJS
public class DirigibleSourceProvider {
    private static final IRepository REPOSITORY = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);

    public Path getAbsoluteSourcePath(String projectName, String projectFileName) {
        String projectFilePath = Path.of(projectName, projectFileName).toString();
        String internalRepositoryRelativeSourcePath = getInternalRepositoryRelativeSourcePath(projectFilePath);
        String absoluteSourcePathString = REPOSITORY.getInternalResourcePath(internalRepositoryRelativeSourcePath.toString());
        return Path.of(absoluteSourcePathString);
    }

    private String getInternalRepositoryRelativeSourcePath(String projectFilePath) {
        return Path.of(IRepositoryStructure.PATH_REGISTRY_PUBLIC, projectFilePath).toString();
    }

    public String getSource(String projectName, String projectFileName) {
        Path projectFilePath = Path.of(projectName, projectFileName);
        return getSource(projectFilePath.toString());
    }

    public String getSource(String projectFilePath) {
        projectFilePath = withDefaultFileExtensionIfNecessary(projectFilePath);

        String internalRepositoryRelativeSourcePath = getInternalRepositoryRelativeSourcePath(projectFilePath);

        byte[] maybeContentFromRepository = tryGetFromRepository(internalRepositoryRelativeSourcePath);
        if (maybeContentFromRepository != null) {
            return new String(maybeContentFromRepository, StandardCharsets.UTF_8);
        }

        byte[] maybeContentFromClassLoader = tryGetFromClassLoader(internalRepositoryRelativeSourcePath, projectFilePath);
        if (maybeContentFromClassLoader != null) {
            return new String(maybeContentFromClassLoader, StandardCharsets.UTF_8);
        }

        return null;
    }

    private static String withDefaultFileExtensionIfNecessary(String filePath) {
        if (filePath.endsWith(".js")
                || filePath.endsWith(".json")
                || filePath.endsWith(".mjs")
                || filePath.endsWith(".xsjs")
                || filePath.endsWith(".ts")) {
            return filePath;
        }

        return filePath + ".js";
    }

    @Nullable
    private static byte[] tryGetFromRepository(String repositoryFilePathString) {
        IResource resource = REPOSITORY.getResource(repositoryFilePathString);
        if (!resource.exists()) {
            return null;
        }
        return resource.getContent();
    }

    @Nullable
    private static byte[] tryGetFromClassLoader(String repositoryAwareFilePathString, String filePathString) {
        try {
            try (InputStream bundled = DirigibleSourceProvider.class.getResourceAsStream("/META-INF/dirigible/" + filePathString)) {
                byte[] content = null;
                if (bundled != null) {
                    content = bundled.readAllBytes();
                    REPOSITORY.createResource(repositoryAwareFilePathString, content);
                }
                return content;
            }
        } catch (IOException e) {
            return null;
        }
    }
}
