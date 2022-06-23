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
package org.eclipse.dirigible.engine.js.graalium.platform.internal.modules;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.engine.api.script.AbstractScriptExecutor;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class DirigibleSourceProvider {
    private static final IRepository REPOSITORY = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);

    public String getSource(String projectName, String projectFileName) {
        Path projectFilePath = Path.of(projectName, projectFileName);
        return getSource(projectFilePath.toString());
    }

    public String getSource(String projectFilePath) {
        projectFilePath = withDefaultFileExtensionIfNecessary(projectFilePath);

        String repositoryScopedProjectFilePath = IRepositoryStructure.PATH_REGISTRY_PUBLIC + IRepository.SEPARATOR + projectFilePath;

        byte[] maybeContentFromRepository = tryGetFromRepository(repositoryScopedProjectFilePath);
        if (maybeContentFromRepository != null) {
            return new String(maybeContentFromRepository, StandardCharsets.UTF_8);
        }

        byte[] maybeContentFromClassLoader = tryGetFromClassLoader(repositoryScopedProjectFilePath, projectFilePath);
        if (maybeContentFromClassLoader != null) {
            return new String(maybeContentFromClassLoader, StandardCharsets.UTF_8);
        }

        return null;
    }

    private static String withDefaultFileExtensionIfNecessary(String filePath) {
        if (filePath.endsWith(".js") || filePath.endsWith(".mjs") || filePath.endsWith(".xsjs")) {
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
            try (InputStream bundled = AbstractScriptExecutor.class.getResourceAsStream("/META-INF/dirigible/" + filePathString)) {
                byte[] content = null;
                if (bundled != null) {
                    content = IOUtils.toByteArray(bundled);
                    REPOSITORY.createResource(repositoryAwareFilePathString, content);
                }
                return content;
            }
        } catch (IOException e) {
            return null;
        }
    }
}
