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
import java.sql.SQLException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.components.data.management.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
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
     * The data sources manager.
     */
    private final DataSourcesManager datasourceManager;

    private final DatabaseExecutionService databaseExecutionService;

    /**
     * Instantiates a new data source endpoint.
     *
     * @param datasourceManager        the datasource manager
     * @param databaseExecutionService
     */
    @Autowired
    public DatabaseExportService(DataSourcesManager datasourceManager, DatabaseExecutionService databaseExecutionService) {
        this.datasourceManager = datasourceManager;
        this.databaseExecutionService = databaseExecutionService;
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
            return databaseExecutionService.executeStatement(dataSource, sql, true, false, true, true);
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
                            String tableExport = databaseExecutionService.executeStatement(dataSource, sql, true, false, true, true);

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
