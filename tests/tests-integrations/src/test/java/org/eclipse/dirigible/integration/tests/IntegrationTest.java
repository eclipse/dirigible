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

import org.eclipse.dirigible.DirigibleApplication;
import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.eclipse.dirigible.integration.tests.ui.FileUtil;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = DirigibleApplication.class)
public abstract class IntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationTest.class);

    @Autowired
    private DataSourcesManager dataSourcesManager;

    @Autowired
    private IRepository dirigibleRepo;

    @AfterEach
    final void deleteDirigibleDBData() {
        LOGGER.info("Deleting Dirigible tables data...");
        DataSource defaultDataSource = dataSourcesManager.getDefaultDataSource();
        deleteAllDataInSchema(defaultDataSource);

        DataSource systemDataSource = dataSourcesManager.getSystemDataSource();
        deleteAllDataInSchema(systemDataSource);

        LOGGER.info("Dirigible tables data has been deleted...");
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

    private static List<String> getAllTables(DataSource dataSource) {
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

    private String relativeToAbsolutePath(String relativePath) {
        return System.getProperty("user.dir") + File.separator + Path.of(relativePath)
                                                                     .normalize();
    }

    @AfterEach
    final void unpublishAllResources() {
        LOGGER.info("Deleting all Dirigible project resources from the repository...");
        deleteUsersFolder();
        deleteDirigibleProjectFromRegistry();
        LOGGER.info("Dirigible project resources have been deleted.");
    }

    private void deleteUsersFolder() {
        String repoBasePath = dirigibleRepo.getRepositoryPath();
        String usersFolder = repoBasePath + File.separator + "users";
        FileUtil.deleteFolder(usersFolder);
    }

    private void deleteDirigibleProjectFromRegistry() {
        String repoBasePath = dirigibleRepo.getRepositoryPath() + IRepositoryStructure.PATH_REGISTRY_PUBLIC;
        try {

            List<Path> files = FileUtil.findFiles(Paths.get(repoBasePath), "project.json");
            files.forEach(f -> {
                Path parentPath = f.getParent();
                FileUtil.deleteFolder(parentPath);
            });

        } catch (IOException ex) {
            throw new IllegalStateException("Failed to delete all folders in registry which are Dirigible projects", ex);
        }
    }

}
