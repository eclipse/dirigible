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
package org.eclipse.dirigible.components.data.management.service;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.components.data.management.helpers.DatabaseErrorHelper;
import org.eclipse.dirigible.components.data.management.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.components.data.management.helpers.DatabaseQueryHelper;
import org.eclipse.dirigible.components.data.management.helpers.DatabaseQueryHelper.RequestExecutionCallback;
import org.eclipse.dirigible.components.data.management.helpers.DatabaseResultSetHelper;
import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.eclipse.dirigible.components.data.sources.service.DataSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * The Class DataSourceMetadataService.
 */
@Service
public class DatabaseExportService {

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(DatabaseExportService.class);

    /**
     * The Constant CREATE_PROCEDURE.
     */
    private static final String CREATE_PROCEDURE = "CREATE PROCEDURE";

    /**
     * The Constant SCRIPT_DELIMITER.
     */
    private static final String SCRIPT_DELIMITER = ";";

    /**
     * The Constant PROCEDURE_DELIMITER.
     */
    private static final String PROCEDURE_DELIMITER = "--";

    /**
     * The limited.
     */
    private boolean LIMITED = true;

    /**
     * The data sources manager.
     */
    private final DataSourcesManager datasourceManager;

    /**
     * The data sources service.
     */
    private final DataSourceService datasourceService;

    private final DatabaseDefinitionService databaseDefinitionService;

    /**
     * Instantiates a new data source endpoint.
     *
     * @param datasourceManager         the datasource manager
     * @param datasourceService         the datasource service
     * @param databaseDefinitionService the database definition service
     */
    @Autowired
    public DatabaseExportService(DataSourcesManager datasourceManager, DataSourceService datasourceService, DatabaseDefinitionService databaseDefinitionService) {
        this.datasourceManager = datasourceManager;
        this.datasourceService = datasourceService;
        this.databaseDefinitionService = databaseDefinitionService;
    }

    /**
     * Export structure.
     *
     * @param datasource the datasource
     * @param schema     the schema
     * @param structure  the structure
     * @return the string
     */
    public String exportStructure(String datasource, String schema, String structure) {
        javax.sql.DataSource dataSource = datasourceManager.getDataSource(datasource);
        if (dataSource != null) {
            String sql = "SELECT * FROM \"" + schema + "\".\"" + structure + "\"";
            return executeStatement(dataSource, sql, true, false, true);
        }
        return null;
    }

    /**
     * Export schema.
     *
     * @param datasource the datasource
     * @param schema     the schema
     * @return the string
     */
    public byte[] exportSchema(String datasource, String schema) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = null;
            try {
                zipOutputStream = new ZipOutputStream(baos);

                javax.sql.DataSource dataSource = datasourceManager.getDataSource(datasource);
                if (dataSource != null) {
                    String metadata = DatabaseMetadataHelper.getMetadataAsJson(dataSource);
                    JsonElement database = GsonHelper.parseJson(metadata);
                    JsonArray schemes = database.getAsJsonObject().get("schemas").getAsJsonArray();
                    for (int i = 0; i < schemes.size(); i++) {
                        JsonObject scheme = schemes.get(i).getAsJsonObject();
                        if (!scheme.get("name").getAsString().equalsIgnoreCase(schema)) {
                            continue;
                        }
                        JsonArray tables = scheme.get("tables").getAsJsonArray();
                        for (int j = 0; j < tables.size(); j++) {
                            JsonObject table = tables.get(j).getAsJsonObject();
                            String artifact = table.get("name").getAsString();
                            String sql = "SELECT * FROM \"" + schema + "\".\"" + artifact + "\"";
                            String tableExport = executeStatement(dataSource, sql, true, false, true);

                            ZipEntry zipEntry = new ZipEntry(schema + "." + artifact + ".csv");
                            zipOutputStream.putNextEntry(zipEntry);
                            zipOutputStream.write((tableExport.getBytes() == null ? new byte[]{} : tableExport.getBytes()));
                            zipOutputStream.closeEntry();
                        }
                    }
                }

            } finally {
                if (zipOutputStream != null) {
                    zipOutputStream.finish();
                    zipOutputStream.flush();
                    zipOutputStream.close();
                }
            }

            byte[] result = baos.toByteArray();
            return result;
        } catch (IOException | SQLException e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage());
            }
            return e.getMessage().getBytes();
        }
    }

    /**
     * Execute statement.
     *
     * @param dataSource the data source
     * @param sql        the sql
     * @param isQuery    the is query
     * @param isJson     the is json
     * @param isCsv      the is csv
     * @return the string
     */
    public String executeStatement(javax.sql.DataSource dataSource, String sql, boolean isQuery, boolean isJson, boolean isCsv) {

        if ((sql == null) || (sql.length() == 0)) {
            return "";
        }

        List<String> results = new ArrayList<String>();
        List<String> errors = new ArrayList<String>();

        StringTokenizer tokenizer = new StringTokenizer(sql, getDelimiter(sql));
        while (tokenizer.hasMoreTokens()) {
            String line = tokenizer.nextToken();
            if ("".equals(line.trim())) {
                continue;
            }

            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                DatabaseQueryHelper.executeSingleStatement(connection, line, isQuery, new RequestExecutionCallback() {
                    @Override
                    public void updateDone(int recordsCount) {
                        results.add(recordsCount + "");
                    }

                    @Override
                    public void queryDone(ResultSet rs) {
                        try {
                            if (isJson) {
                                results.add(DatabaseResultSetHelper.toJson(rs, LIMITED, true));
                            } else if (isCsv) {
                                results.add(DatabaseResultSetHelper.toCsv(rs, LIMITED, false));
                            } else {
                                results.add(DatabaseResultSetHelper.print(rs, LIMITED));
                            }
                        } catch (SQLException e) {
                            if (logger.isWarnEnabled()) {
                                logger.warn(e.getMessage(), e);
                            }
                            errors.add(e.getMessage());
                        }
                    }

                    @Override
                    public void error(Throwable t) {
                        if (logger.isWarnEnabled()) {
                            logger.warn(t.getMessage(), t);
                        }
                        errors.add(t.getMessage());
                    }
                });
            } catch (SQLException e) {
                if (logger.isWarnEnabled()) {
                    logger.warn(e.getMessage(), e);
                }
                errors.add(e.getMessage());
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        if (logger.isWarnEnabled()) {
                            logger.warn(e.getMessage(), e);
                        }
                    }
                }
            }
        }

        if (!errors.isEmpty()) {
            if (isJson) {
                return DatabaseErrorHelper.toJson(String.join("\n", errors));
            }
            return DatabaseErrorHelper.print(String.join("\n", errors));
        }

        return String.join("\n", results);
    }

    /**
     * Gets the delimiter.
     *
     * @param sql the sql
     * @return the delimiter
     */
    private String getDelimiter(String sql) {
        if (StringUtils.containsIgnoreCase(sql, CREATE_PROCEDURE)) {
            return PROCEDURE_DELIMITER;
        }
        return SCRIPT_DELIMITER;
    }

}
