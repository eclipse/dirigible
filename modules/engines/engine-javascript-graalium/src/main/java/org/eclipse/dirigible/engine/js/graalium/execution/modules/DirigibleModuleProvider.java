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
package org.eclipse.dirigible.engine.js.graalium.execution.modules;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.engine.api.script.AbstractScriptExecutor;
import org.eclipse.dirigible.engine.js.graalium.execution.CalledFromJS;
import org.eclipse.dirigible.repository.api.*;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class DirigibleModuleProvider {
    private static final IRepository REPOSITORY = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);

    @CalledFromJS
    public static String loadSource(String module) throws IOException {
        if (module == null) {
            throw new IOException("Module location cannot be null");
        }

        String maybeJSSourceCode = getResourceContent(IRepositoryStructure.PATH_REGISTRY_PUBLIC, module + ".js");
        if (maybeJSSourceCode != null) {
            return maybeJSSourceCode;
        }

        String maybeXSJSSourceCode = getResourceContent(IRepositoryStructure.PATH_REGISTRY_PUBLIC, module + ".xsjs");
        if (maybeXSJSSourceCode != null) {
            return maybeXSJSSourceCode;
        }

        throw new IOException("Could not find module '" + module + "'");
    }

    @Nullable
    public static String getResourceContent(String root, String filePath) throws RepositoryException {
        validateProvidedModulePath(filePath);
        String resourcePath = createResourcePath(root, filePath);

        byte[] maybeContentFromRepository = tryGetFromRepository(resourcePath);
        if (maybeContentFromRepository != null) {
            return new String(maybeContentFromRepository, StandardCharsets.UTF_8);
        }

        byte[] maybeContentFromClassLoader = tryGetFromClassLoader(resourcePath, filePath);
        if (maybeContentFromClassLoader != null) {
            return new String(maybeContentFromClassLoader, StandardCharsets.UTF_8);
        }

        return null;
    }

    private static void validateProvidedModulePath(String modulePath) {
        if ((modulePath == null) || "".equals(modulePath.trim())) {
            throw new RepositoryException("Module name cannot be empty or null.");
        }
        if (modulePath.trim().endsWith(IRepositoryStructure.SEPARATOR)) {
            throw new RepositoryException("Module name cannot point to a collection.");
        }
    }

    @Nullable
    private static byte[] tryGetFromRepository(String repositoryPath) {
        IResource resource = REPOSITORY.getResource(repositoryPath);
        if (!resource.exists()) {
            return null;
        }
        return resource.getContent();
    }

    @Nullable
    private static byte[] tryGetFromClassLoader(String repositoryPath, String filePath) {
        try {
            String prefix = Character.toString(filePath.charAt(0)).equals(IRepository.SEPARATOR) ? "" : IRepository.SEPARATOR;
            String location = prefix + filePath;
            try (InputStream bundled = AbstractScriptExecutor.class.getResourceAsStream("/META-INF/dirigible" + location)) {
                byte[] content = null;
                if (bundled != null) {
                    content = IOUtils.toByteArray(bundled);
                    REPOSITORY.createResource(repositoryPath, content);
                }
                return content;
            }
        } catch (IOException e) {
            return null;
        }
    }

    private static String createResourcePath(String root, String module) {
        StringBuilder buff = new StringBuilder().append(root);
        if (!Character.toString(module.charAt(0)).equals(IRepository.SEPARATOR)) {
            buff.append(IRepository.SEPARATOR);
        }
        buff.append(module);
        return buff.toString();
    }
}
