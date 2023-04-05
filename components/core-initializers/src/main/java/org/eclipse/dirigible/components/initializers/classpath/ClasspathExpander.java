/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.initializers.classpath;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClasspathExpander {

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ClasspathExpander.class);

    private final IRepository repository;

    @Autowired
    public ClasspathExpander(IRepository repository) {
        this.repository = repository;
    }

    public void expandContent() {
        expandContent("dirigible");
        expandContent("resources" + File.separator + "webjars");
    }

    private void expandContent(String root) {
        try {
            Enumeration<URL> urls = ClasspathContentInitializer.class.getClassLoader().getResources("META-INF");

            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                try {
                    URLConnection urlConnection = url.openConnection();
                    if (urlConnection instanceof JarURLConnection) {
                        handleJarURLConnection(root, urlConnection);
                    } else {
                        Path dirPath = Path.of(url.toURI()).resolve(root);
                        handleLocalDirectory(dirPath);
                    }
                } catch (URISyntaxException | IOException e) {
                    logDirectoryExpandingError(url.toString(), e);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleJarURLConnection(String root, URLConnection urlConnection) throws IOException {
        String jarRoot = "META-INF/" + root;
        JarURLConnection jarUrlConnection = (JarURLConnection) urlConnection;
        try (JarFile jar = jarUrlConnection.getJarFile()) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().startsWith(jarRoot)) {
                    if (!entry.isDirectory()) {
                        byte[] content = IOUtils.toByteArray(jar.getInputStream(entry));
                        String registryPath = entry.getName().substring(jarRoot.length());
                        repository.createResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + IRepository.SEPARATOR + registryPath, content);
                    }
                }
            }
        }
    }

    private void handleLocalDirectory(Path dirPath) {
        try {
            File maybeDir = dirPath.toFile();
            if (!maybeDir.exists() || maybeDir.isFile()) {
                return;
            }
            String registryPath = repository.getInternalResourcePath(IRepositoryStructure.PATH_REGISTRY_PUBLIC);
            FileUtils.copyDirectory(maybeDir, new File(registryPath));
        } catch (IOException e) {
            logDirectoryExpandingError(dirPath.toString(), e);
        }
    }

    private void logDirectoryExpandingError(String dirPath, Exception e) {
        logger.error("Could not collect dir '" + dirPath + "'", e);
    }
}
