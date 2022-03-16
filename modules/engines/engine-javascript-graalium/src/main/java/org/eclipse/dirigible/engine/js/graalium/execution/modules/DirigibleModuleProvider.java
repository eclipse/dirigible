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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DirigibleModuleProvider {
    private static final IRepository REPOSITORY = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);

    public static byte[] getResourceContent(String root, String module, String extension) throws RepositoryException {
        validateProvidedModulePath(module);
        String resourcePath = createResourcePath(root, module, extension);

        byte[] maybeContentFromRepository = tryGetFromRepository(resourcePath);
        if (maybeContentFromRepository != null) {
            return maybeContentFromRepository;
        }

        maybeContentFromRepository = tryGetFromClassLoader(resourcePath, module, extension);
        if (maybeContentFromRepository != null) {
            return maybeContentFromRepository;
        }

        REPOSITORY.getInternalResourcePath(resourcePath);

        final String logMsg = String.format("There is no resource at the specified path: %s", resourcePath);
        throw new RepositoryNotFoundException(logMsg);
    }

    private static void validateProvidedModulePath(String modulePath) {
        if ((modulePath == null) || "".equals(modulePath.trim())) {
            throw new RepositoryException("Module name cannot be empty or null.");
        }
        if (modulePath.trim().endsWith(IRepositoryStructure.SEPARATOR)) {
            throw new RepositoryException("Module name cannot point to a collection.");
        }
    }

    private static byte[] tryGetFromRepository(String repositoryPath) {
        IResource resource = REPOSITORY.getResource(repositoryPath);
        if (!resource.exists()) {
            return null;
        }
        return resource.getContent();
    }

    private static byte[] tryGetFromClassLoader(String repositoryPath, String module, String extension) {
        try {
            String prefix = Character.toString(module.charAt(0)).equals(IRepository.SEPARATOR) ? "" : IRepository.SEPARATOR;
            String location = prefix + module + (extension != null ? extension : "");
            try (InputStream bundled = AbstractScriptExecutor.class.getResourceAsStream("/META-INF/dirigible" + location)) {
                if (bundled == null) {
                    return null;
                }
                byte[] content = IOUtils.toByteArray(bundled);
                REPOSITORY.createResource(repositoryPath, content);
                return content;
            }
        } catch (IOException e) {
            throw new RepositoryException(e);
        }
    }

    private static String createResourcePath(String root, String module, String extension) {
        StringBuilder buff = new StringBuilder().append(root);
        if (!Character.toString(module.charAt(0)).equals(IRepository.SEPARATOR)) {
            buff.append(IRepository.SEPARATOR);
        }
        buff.append(module);
        if (extension != null) {
            buff.append(extension);
        }
        return buff.toString();
    }

    @CalledFromJS
    public static String loadSource(String module) throws IOException {

        if (module == null) {
            throw new IOException("Module location cannot be null");
        }

        byte[] sourceCode = getResourceContent(IRepositoryStructure.PATH_REGISTRY_PUBLIC, module, ".js");
        return new String(sourceCode, StandardCharsets.UTF_8);
    }
}
