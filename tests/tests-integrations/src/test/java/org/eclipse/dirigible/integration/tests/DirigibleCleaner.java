/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.integration.tests;

import org.eclipse.dirigible.commons.config.DirigibleConfig;
import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.dialects.SqlDialectFactory;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.tests.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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
            deleteDatabases();
            deleteCMSFolderFiles();
            unpublishResources();
        } catch (Throwable ex) {
            throw new IllegalStateException("Failed to cleanup resources", ex);
        }
    }

    private void deleteDatabases() {
        LOGGER.info("Deleting Dirigible databases...");

        deleteDirigibleDBData();
        deleteH2Folder();

        LOGGER.info("Dirigible databases have been deleted...");
    }

    /**
     * Execute this before H2 folder deletion because it is in memory DB. Otherwise, will remain data in
     * memory.
     */
    private void deleteDirigibleDBData() {
        DataSource defaultDataSource = dataSourcesManager.getDefaultDataSource();
        deleteAllDataInSchema(defaultDataSource);

        DataSource systemDataSource = dataSourcesManager.getSystemDataSource();
        deleteAllDataInSchema(systemDataSource);

        deleteSchemas(defaultDataSource);
    }

    private void deleteAllDataInSchema(DataSource dataSource) {
        List<String> tables = getAllTables(dataSource);

        for (int idx = 0; idx < 3; idx++) { // execute it a few times due to constraint violations
            tables.forEach(t -> {
                try (Connection connection = dataSource.getConnection()) {
                    String sql = SqlDialectFactory.getDialect(connection)
                                                  .delete()
                                                  .from(t)
                                                  .build();
                    try (PreparedStatement prepareStatement = connection.prepareStatement(sql)) {
                        int rowsAffected = prepareStatement.executeUpdate();
                        LOGGER.info("Deleted [{}] from table [{}]", rowsAffected, t);
                    }
                } catch (SQLException ex) {
                    LOGGER.warn("Failed to delete data from table [{}] in data source [{}]", t, dataSource, ex);
                }
            });
        }
    }

    private List<String> getAllTables(DataSource dataSource) {
        List<String> tables = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement prepareStatement = connection.prepareStatement(
                        "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC' OR TABLE_SCHEMA='public'")) {
            ResultSet resultSet = prepareStatement.executeQuery();
            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
            }
            return tables;
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to get all tables in data source:" + dataSource, ex);
        }
    }

    private void deleteSchemas(DataSource dataSource) {
        Set<String> schemas = getSchemas(dataSource);
        schemas.remove("PUBLIC");
        schemas.remove("public");
        schemas.remove("INFORMATION_SCHEMA");
        schemas.remove("information_schema");
        schemas.removeIf(s -> s.startsWith("pg_"));

        LOGGER.info("Will drop schemas [{}] from data source [{}]", schemas, dataSource);
        schemas.forEach(schema -> deleteSchema(schema, dataSource));
    }

    private Set<String> getSchemas(DataSource dataSource) {
        try {
            return getSchemas(dataSource, "SHOW SCHEMAS");
        } catch (SQLException ex) {
            try {
                return getSchemas(dataSource, "SELECT nspname FROM pg_catalog.pg_namespace");
            } catch (SQLException e) {
                throw new IllegalStateException("Failed to get all schemas from data source: " + dataSource, e);
            }
        }
    }

    private Set<String> getSchemas(DataSource dataSource, String sql) throws SQLException {
        Set<String> schemas = new HashSet<>();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                schemas.add(resultSet.getString(1));
            }
            return schemas;
        }
    }

    private void deleteSchema(String schema, DataSource dataSource) {
        LOGGER.info("Will drop schema [{}] from data source [{}]", schema, dataSource);
        try (Connection connection = dataSource.getConnection()) {
            ISqlDialect dialect = SqlDialectFactory.getDialect(connection);
            String sql = dialect.drop()
                                .schema(schema)
                                .generate();
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                throw new IllegalStateException(
                        "Failed to drop schema [" + schema + "] from dataSource [" + dataSource + "] using sql: " + sql, ex);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to drop schema [" + schema + "] from dataSource [" + dataSource + "] ", ex);
        }
    }

    private void deleteH2Folder() {
        String h2Folder = getDirigibleSubfolder("h2");
        FileUtil.deleteFolder(h2Folder);
    }

    private String getDirigibleSubfolder(String folder) {
        return System.getProperty("user.dir") + File.separator + "target" + File.separator + "dirigible" + File.separator + folder;
    }

    private void unpublishResources() throws IOException {
        LOGGER.info("Deleting all Dirigible project resources from the repository...");

        List<String> userProjects = getUserProjects();
        deleteCurrentUserFolder();
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

    private File getUsersRepoFolder() {
        String repoBasePath = dirigibleRepo.getRepositoryPath();
        return new File(repoBasePath + File.separator + "users");
    }

    private void deleteCurrentUserFolder() {
        File usersFolder = getUsersRepoFolder();
        String currentUserFolder = usersFolder.getPath() + File.separator + DirigibleConfig.BASIC_ADMIN_USERNAME.getFromBase64Value();
        LOGGER.info("Will delete current user folder [{}]", currentUserFolder);
        FileUtil.deleteFolder(currentUserFolder);
    }

    private void deleteDirigibleProjectsFromRegistry(List<String> userProjects) {
        String repoBasePath = dirigibleRepo.getRepositoryPath() + IRepositoryStructure.PATH_REGISTRY_PUBLIC + File.separator;
        LOGGER.info("Will delete user projects [{}] from the registry [{}]", userProjects, repoBasePath);
        userProjects.forEach(projectName -> {
            String projectPath = repoBasePath + projectName;
            FileUtil.deleteFolder(projectPath);
        });
    }

    private void deleteCMSFolderFiles() throws IOException {
        String cmdFolder = getDirigibleSubfolder("cms");
        FileUtil.findFiles(cmdFolder)
                .stream()
                .map(Path::toFile)
                .forEach(File::delete);
    }
}
