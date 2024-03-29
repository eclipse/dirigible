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

import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.eclipse.dirigible.integration.tests.ui.FileUtil;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
class DirigibleCleaner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirigibleCleaner.class);

    private final DataSourcesManager dataSourcesManager;
    private final IRepository dirigibleRepo;

    DirigibleCleaner(DataSourcesManager dataSourcesManager, IRepository dirigibleRepo) {
        this.dataSourcesManager = dataSourcesManager;
        this.dirigibleRepo = dirigibleRepo;
    }

    void clean() {
        try {
            deleteDirigibleDBData();
            unpublishAllResources();
        } catch (Throwable ex) {
            throw new IllegalStateException("Failed to cleanup resources", ex);
        }
    }

    private void deleteDirigibleDBData() {
        LOGGER.info("Deleting Dirigible tables data...");
        deleteH2Folder();
        //
        // DataSource defaultDataSource = dataSourcesManager.getDefaultDataSource();
        // deleteAllDataInSchema(defaultDataSource);
        //
        // DataSource systemDataSource = dataSourcesManager.getSystemDataSource();
        // deleteAllDataInSchema(systemDataSource);
        //
        // deleteSchemas(defaultDataSource);

        LOGGER.info("Dirigible DB data has been deleted...");
    }

    private void deleteSchemas(DataSource dataSource) {
        Set<String> schemas = getSchemas(dataSource);
        schemas.remove("PUBLIC");
        schemas.remove("INFORMATION_SCHEMA");

        LOGGER.info("Will drop schemas [{}] from data source [{}]", schemas, dataSource);
        schemas.forEach(schema -> deleteSchema(schema, dataSource));
    }

    private void deleteSchema(String schema, DataSource dataSource) {
        LOGGER.info("Will drop schema [{}] from data source [{}]", schema, dataSource);
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("DROP SCHEMA `" + schema + "` CASCADE")) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to drop schema [" + schema + "] from dataSource: " + dataSource, ex);
        }
    }

    private Set<String> getSchemas(DataSource dataSource) {
        Set<String> schemas = new HashSet<>();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SHOW SCHEMAS");
                ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                schemas.add(resultSet.getString(1));
            }
            return schemas;
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to get all schemas from data source: " + dataSource, ex);
        }
    }

    private void deleteAllDataInSchema(DataSource dataSource) {
        List<String> tables = getAllTables(dataSource);

        tables.forEach(t -> {
            try (Connection connection = dataSource.getConnection();
                    PreparedStatement prepareStatement = connection.prepareStatement("DELETE FROM " + t)) {
                int rowsAffected = prepareStatement.executeUpdate();
                LOGGER.info("Deleted [{}] from table [{}]", rowsAffected, t);
            } catch (SQLException ex) {
                LOGGER.warn("Failed to delete data from table [{}] in data source [{}]", t, dataSource, ex);
            }
        });
    }

    private List<String> getAllTables(DataSource dataSource) {
        List<String> tables = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement prepareStatement =
                        connection.prepareStatement("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'")) {
            ResultSet resultSet = prepareStatement.executeQuery();
            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
            }
            return tables;
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to get all tables in data source:" + dataSource, ex);
        }
    }

    private void unpublishAllResources() throws IOException {
        LOGGER.info("Deleting all Dirigible project resources from the repository...");

        List<String> userProjects = getUserProjects();
        deleteUsersFolder();
        deleteDirigibleProjectsFromRegistry(userProjects);
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

    private void deleteDirigibleProjectsFromRegistry(List<String> userProjects) {
        String repoBasePath = dirigibleRepo.getRepositoryPath() + IRepositoryStructure.PATH_REGISTRY_PUBLIC + File.separator;
        LOGGER.info("Will delete user projects [{}] from the registry [{}]", userProjects, repoBasePath);
        try {
            List<Path> files = FileUtil.findFiles(Paths.get(repoBasePath), "project.json");
            userProjects.forEach(projectName -> {
                String projectPath = repoBasePath + projectName;
                FileUtil.deleteFolder(projectPath);
            });

        } catch (IOException ex) {
            throw new IllegalStateException("Failed to delete all user projects [" + userProjects + "] from registry", ex);
        }
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
