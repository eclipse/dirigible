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
package org.eclipse.dirigible.graalium.core.modules;

import org.eclipse.dirigible.graalium.core.javascript.CalledFromJS;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * The Class DirigibleSourceProvider.
 */
@CalledFromJS
public class DirigibleSourceProvider {
    
    static IRepository getRepository() {
    	return (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
    }
    
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
        String absoluteSourcePathString = getRepository().getInternalResourcePath(internalRepositoryRelativeSourcePath.toString());
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
     * @param sourceFilePath the project file path
     * @return the source
     */
    public String getSource(String sourceFilePath) {
        sourceFilePath = withDefaultFileExtensionIfNecessary(sourceFilePath);

        String internalRepositoryRelativeSourcePath = getInternalRepositoryRelativeSourcePath(sourceFilePath);

        byte[] maybeContentFromRepository = tryGetFromRepository(internalRepositoryRelativeSourcePath);
        if (maybeContentFromRepository != null) {
            return new String(maybeContentFromRepository, StandardCharsets.UTF_8);
        }

        byte[] maybeContentFromClassLoader = tryGetFromClassLoader(internalRepositoryRelativeSourcePath, sourceFilePath);
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
        IResource resource = getRepository().getResource(repositoryFilePathString);
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
            var lookupPath = createLookupPath(filePathString);
            try (InputStream bundled = DirigibleSourceProvider.class.getResourceAsStream(lookupPath)) {
                byte[] content = null;
                if (bundled != null) {
                    content = bundled.readAllBytes();
                    getRepository().createResource(repositoryAwareFilePathString, content);
                }
                return content;
            }
        } catch (IOException e) {
            return null;
        }
    }

    private static String createLookupPath(String filePathString) {
        var lookupPath = "/META-INF/dirigible/" + filePathString;
        if (filePathString.startsWith("/webjars") || filePathString.startsWith("webjars")) {
            lookupPath = "/META-INF/resources/" + filePathString;
        }

        return lookupPath;
    }

    public Path unpackedToFileSystem(Path pathToUnpack, Path pathToLookup) {
        try (InputStream bundled = DirigibleSourceProvider.class.getResourceAsStream("/META-INF/dirigible/" + pathToLookup.toString())) {
            Files.createDirectories(pathToUnpack.getParent());
            Files.createFile(pathToUnpack);
            Files.copy(bundled, pathToUnpack, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return pathToUnpack;
    }
}
