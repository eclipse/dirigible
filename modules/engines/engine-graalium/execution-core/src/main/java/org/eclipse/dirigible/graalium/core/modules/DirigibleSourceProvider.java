package org.eclipse.dirigible.graalium.core.modules;

import org.eclipse.dirigible.graalium.core.javascript.CalledFromJS;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * The Class DirigibleSourceProvider.
 */
@CalledFromJS
public class DirigibleSourceProvider {
    
    /** The Constant REPOSITORY. */
    private static final IRepository REPOSITORY = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);

    /**
     * Gets the absolute source path.
     *
     * @param projectName the project name
     * @param projectFileName the project file name
     * @return the absolute source path
     */
    public Path getAbsoluteSourcePath(String projectName, String projectFileName) {
        String projectFilePath = Path.of(projectName, projectFileName).toString();
        String internalRepositoryRelativeSourcePath = getInternalRepositoryRelativeSourcePath(projectFilePath);
        String absoluteSourcePathString = REPOSITORY.getInternalResourcePath(internalRepositoryRelativeSourcePath.toString());
        return Path.of(absoluteSourcePathString);
    }

    /**
     * Gets the internal repository relative source path.
     *
     * @param projectFilePath the project file path
     * @return the internal repository relative source path
     */
    private String getInternalRepositoryRelativeSourcePath(String projectFilePath) {
        return Path.of(IRepositoryStructure.PATH_REGISTRY_PUBLIC, projectFilePath).toString();
    }

    /**
     * Gets the source.
     *
     * @param projectName the project name
     * @param projectFileName the project file name
     * @return the source
     */
    public String getSource(String projectName, String projectFileName) {
        Path projectFilePath = Path.of(projectName, projectFileName);
        return getSource(projectFilePath.toString());
    }

    /**
     * Gets the source.
     *
     * @param projectFilePath the project file path
     * @return the source
     */
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

    /**
     * With default file extension if necessary.
     *
     * @param filePath the file path
     * @return the string
     */
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

    /**
     * Try get from repository.
     *
     * @param repositoryFilePathString the repository file path string
     * @return the byte[]
     */
    private static byte[] tryGetFromRepository(String repositoryFilePathString) {
        IResource resource = REPOSITORY.getResource(repositoryFilePathString);
        if (!resource.exists()) {
            return null;
        }
        return resource.getContent();
    }

    /**
     * Try get from class loader.
     *
     * @param repositoryAwareFilePathString the repository aware file path string
     * @param filePathString the file path string
     * @return the byte[]
     */
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
