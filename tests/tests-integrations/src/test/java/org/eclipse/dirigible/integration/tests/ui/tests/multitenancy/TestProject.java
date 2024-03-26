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
package org.eclipse.dirigible.integration.tests.ui.tests.multitenancy;

import org.apache.commons.io.FileUtils;
import org.eclipse.dirigible.repository.api.IRepository;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Component
class TestProject {

    private static final String PROJECT_RESOURCES_PATH = "dirigible-test-project";
    private static final String ADMIN_USERNAME = "admin";

    private final IRepository dirigibleRepo;

    TestProject(IRepository dirigibleRepo) {
        this.dirigibleRepo = dirigibleRepo;
    }

    void copyToRepository() {
        String repoBasePath = dirigibleRepo.getRepositoryPath();
        String userWorkspace = repoBasePath + File.separator + "users" + File.separator + ADMIN_USERNAME + File.separator + "workspace";

        URL projectResource = TestProject.class.getClassLoader()
                                               .getResource(PROJECT_RESOURCES_PATH);
        if (null == projectResource) {
            throw new IllegalStateException("Missing test project resource folder with path " + PROJECT_RESOURCES_PATH);
        }
        String destinationDir = userWorkspace + File.separator + PROJECT_RESOURCES_PATH;

        String projectResourcesPath = projectResource.getPath();
        try {
            File sourceDirectory = new File(projectResource.getPath());
            File destinationDirectory = new File(destinationDir);
            FileUtils.copyDirectory(sourceDirectory, destinationDirectory);
            // List<Path> paths = Files.walk(Paths.get(projectResource.toURI()))
            // .toList();
            // for (Path path : paths) {
            // Path destination = Paths.get(destinationDirectory, path.toString()
            // .substring(projectResourcesPath.length()));
            // Files.copy(path, destination);
            // }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to copy test project to Dirigible repository", ex);
        }
    }

    public String getRootFolderName() {
        return "dirigible-test-project";
    }

    public String getEdmFileName() {
        return "edm.edm";
    }
}
