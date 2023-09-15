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
package org.eclipse.dirigible.components.engine.typescript;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.components.base.initializer.Initializer;
import org.eclipse.dirigible.components.base.publisher.PublisherHandler;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

@Component
public class ProjectBuildCallback implements PublisherHandler, Initializer {

    private final ProjectBuildService projectBuildService;
    private final IRepository repository;

    @Autowired
    public ProjectBuildCallback(ProjectBuildService projectBuildService, IRepository repository) {
        this.projectBuildService = projectBuildService;
        this.repository = repository;
    }

    @Override
    public void afterPublish(String workspaceLocation, String registryLocation, AfterPublishMetadata metadata) {
        String project = metadata.projectName();
        if (StringUtils.isEmpty(project)) {
            initialize();
        } else {
            projectBuildService.build(metadata.projectName(), metadata.entryPath());
        }
    }

    @Override
    public void initialize() {
        onEachRegistryProject(dir -> projectBuildService.build(dir.getName()));
    }

    private void onEachRegistryProject(Consumer<File> callback) {
        var registryPath = registryPath();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(registryPath)) {
            for (Path projectPath : directoryStream) {
                File projectDir = projectPath.toFile();
                if (projectDir.isDirectory()) {
                    callback.accept(projectDir);
                }
            }
        } catch (IOException e) {
            throw new TypeScriptException("Error while reading registry projects", e);
        }
    }

    private Path registryPath() {
        return Path.of(repository.getInternalResourcePath(IRepositoryStructure.PATH_REGISTRY_PUBLIC));
    }

}
