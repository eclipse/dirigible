/*
 * Copyright (c) 2010-2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.tests.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

@Component
public class ProjectUtil {

    private static final String PROJECT_TEMPLATE_FOLDER = "project-template";
    private final IRepository repository;

    ProjectUtil(IRepository repository) {

        this.repository = repository;
    }

    public void createProject(String projectName) {
        String projectTemplatePath = getResourcePath(PROJECT_TEMPLATE_FOLDER);
        String destinationDirPath = createRegistryFolderPath(projectName);

        File sourceDir = new File(projectTemplatePath);
        File destinationDir = new File(destinationDirPath);

        try {
            FileUtils.copyDirectory(sourceDir, destinationDir);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to copy [" + sourceDir + "] to " + destinationDir, ex);
        }
    }

    private String getResourcePath(String path) {
        URL resourceURL = ProjectUtil.class.getClassLoader()
                                           .getResource(path);
        if (null == resourceURL) {
            throw new IllegalStateException("Missing resource with path " + path);
        }
        return resourceURL.getPath();
    }

    private String createRegistryFolderPath(String folder) {
        return repository.getRepositoryPath() + File.separator + IRepositoryStructure.KEYWORD_REGISTRY + File.separator
                + IRepositoryStructure.KEYWORD_PUBLIC + File.separator + folder;
    }

    public void copyFolderContentToProject(String resourcesFolder, String targetProjectName, Map<String, String> placeholders) {
        String sourceDirPath = getResourcePath(resourcesFolder);
        File sourceDir = new File(sourceDirPath);

        String destinationDirPath = createRegistryFolderPath(targetProjectName);
        File destinationDir = new File(destinationDirPath);

        try {
            FileUtils.copyDirectory(sourceDir, destinationDir);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to copy [" + sourceDir + "]test project to " + destinationDir, ex);
        }

        Collection<File> files = FileUtils.listFiles(destinationDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            replacePlaceholderInFile(file, placeholders);
        }
    }

    private void replacePlaceholderInFile(File file, Map<String, String> placeholders) {
        try {
            String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                String placeholder = entry.getKey();
                String replacement = entry.getValue();
                content = StringUtils.replace(content, placeholder, replacement);
            }

            FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to replace placeholders [" + placeholders + "] in file " + file, ex);
        }
    }
}
