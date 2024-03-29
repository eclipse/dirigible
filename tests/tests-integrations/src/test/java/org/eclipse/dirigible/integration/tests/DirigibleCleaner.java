/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.integration.tests;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.tests.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Component
class DirigibleCleaner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirigibleCleaner.class);

    private final IRepository dirigibleRepo;

    DirigibleCleaner(IRepository dirigibleRepo) {
        this.dirigibleRepo = dirigibleRepo;
    }

    void clean() {
        try {
            deleteDatabases();
            unpublishAllResources();
        } catch (Throwable ex) {
            throw new IllegalStateException("Failed to cleanup resources", ex);
        }
    }

    private void deleteDatabases() {
        LOGGER.info("Deleting Dirigible databases...");

        deleteH2Folder();

        LOGGER.info("Dirigible databases have been deleted...");
    }

    private void unpublishAllResources() throws IOException {
        LOGGER.info("Deleting all Dirigible project resources from the repository...");

        List<String> userProjects = getUserProjects();
        deleteUsersFolder();
        deleteUserProjectsFromRegistry(userProjects);

        LOGGER.info("Dirigible project resources have been deleted.");
    }

    private List<String> getUserProjects() throws IOException {
        File usersRepoFolder = getUsersRepoFolder();
        if (usersRepoFolder.exists()) {
            List<Path> userProjectFiles = FileUtil.findFiles(usersRepoFolder, "project.json");
            return userProjectFiles.stream()
                                   .map(p -> p.toFile()
                                              .getParentFile()
                                              .getName())
                                   .toList();
        }
        LOGGER.info("Missing users repo folder [{}]", usersRepoFolder);
        return Collections.emptyList();
    }

    private void deleteUsersFolder() {
        File usersFolder = getUsersRepoFolder();
        FileUtil.deleteFolder(usersFolder);
    }

    private void deleteH2Folder() {
        File h2Folder = getH2Folder();
        FileUtil.deleteFolder(h2Folder);
    }

    private void deleteUserProjectsFromRegistry(List<String> userProjects) {
        String repoBasePath = dirigibleRepo.getRepositoryPath() + IRepositoryStructure.PATH_REGISTRY_PUBLIC + File.separator;
        LOGGER.info("Will delete user projects [{}] from the registry [{}]", userProjects, repoBasePath);
        userProjects.forEach(projectName -> {
            String projectPath = repoBasePath + projectName;
            FileUtil.deleteFolder(projectPath);
        });

    }

    private File getUsersRepoFolder() {
        String repoBasePath = dirigibleRepo.getRepositoryPath();
        return new File(repoBasePath + File.separator + "users");
    }

    private File getH2Folder() {
        String path = System.getProperty("user.dir") + File.separator + "target" + File.separator + "dirigible" + File.separator + "h2";
        return new File(path);
    }

}
