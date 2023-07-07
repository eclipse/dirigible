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
package org.eclipse.dirigible.components.data.export.service;

import com.google.gson.*;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.components.api.platform.WorkspaceFacade;
import org.eclipse.dirigible.components.base.helpers.JsonHelper;
import org.eclipse.dirigible.components.data.csvim.domain.CsvFile;
import org.eclipse.dirigible.components.data.management.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.components.data.management.service.DatabaseDefinitionService;
import org.eclipse.dirigible.components.data.management.service.DatabaseExecutionService;
import org.eclipse.dirigible.components.data.management.service.DatabaseExportService;
import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.eclipse.dirigible.components.ide.workspace.domain.File;
import org.eclipse.dirigible.components.ide.workspace.domain.Project;
import org.eclipse.dirigible.components.ide.workspace.domain.Workspace;
import org.eclipse.dirigible.components.ide.workspace.service.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;

import static java.text.MessageFormat.format;

@Service
public class DataExportService {

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(DataExportService.class);

    /**
     * The data sources manager.
     */
    private final DataSourcesManager datasourceManager;

    /**
     * The data sources manager.
     */
    private final DatabaseExportService databaseExportService;

    /**
     * The workspace service.
     */
    private final WorkspaceService workspaceService;

    /**
     * The database execution service.
     */
    private final DatabaseExecutionService databaseExecutionService;

    /**
     * The database execution service.
     */
    private final DatabaseDefinitionService databaseDefinitionService;

    /**
     * Instantiates a new data export service.
     *
     * @param datasourceManager         the datasource manager
     * @param databaseExportService     the database export service
     * @param workspaceService          the workspace service
     * @param databaseExecutionService  the database execution service
     * @param databaseDefinitionService the database definition service
     */
    @Autowired
    public DataExportService(DataSourcesManager datasourceManager, DatabaseExportService databaseExportService, WorkspaceService workspaceService, DatabaseExecutionService databaseExecutionService, DatabaseDefinitionService databaseDefinitionService) {
        this.datasourceManager = datasourceManager;
        this.databaseExportService = databaseExportService;
        this.workspaceService = workspaceService;
        this.databaseExecutionService = databaseExecutionService;
        this.databaseDefinitionService = databaseDefinitionService;
    }

    /**
     *
     * @param datasource the datasource
     * @param schema the schema
     * @throws SQLException the SQL exception
     */
    public void exportSchemaInCsvs(String datasource, String schema) throws SQLException {
        try {
            javax.sql.DataSource dataSource = datasourceManager.getDataSource(datasource);
            if (dataSource != null) {
                Workspace workspace;
                Project project;
                ArrayList<CsvFile> csvFiles = new ArrayList<>();

                String metadata = DatabaseMetadataHelper.getMetadataAsJson(dataSource);
                JsonElement database = GsonHelper.parseJson(metadata);
                JsonArray schemes = database.getAsJsonObject().get("schemas").getAsJsonArray();

                workspace = WorkspaceFacade.createWorkspace(schema);
                project = workspace.createProject(schema);

                for (int i = 0; i < schemes.size(); i++) {
                    JsonObject scheme = schemes.get(i).getAsJsonObject();
                    if (!scheme.get("name").getAsString().equalsIgnoreCase(schema)) {
                        continue;
                    }
                    JsonArray tables = scheme.get("tables").getAsJsonArray();
                    for (int j = 0; j < tables.size(); j++) {
                        File file;
                        CsvFile csvFile = new CsvFile();

                        JsonObject table = tables.get(j).getAsJsonObject();
                        String artifact = table.get("name").getAsString();
                        String sql = "SELECT * FROM \"" + schema + "\".\"" + artifact + "\"";
                        String tableExport = databaseExecutionService.executeStatement(dataSource, sql, true, false, true, false);

                        file = project.createFile(schema + "." + artifact + ".csv", tableExport.getBytes());

                        setCsvFileFields(csvFile, schema, artifact, file.getProjectPath());
                        csvFiles.add(csvFile);
                    }
                }
                JsonObject csvimContent = transformCsvFilesToJson(csvFiles);
                project.createFile(schema + ".csvim", csvimContent.toString().getBytes());

                logger.info(format("Created requested files in Project [{1}] in Workspace [{2}]", project.getName(), workspace.getName()));
            }
        } catch (SQLException e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     *
     * @param datasource the datasource
     * @param schema the schema
     * @return the workspace path of the file
     * @throws SQLException the SQL exception
     */
    public String exportMetadataAsProject(String datasource, String schema) throws SQLException {
        String schemaMetadata = databaseDefinitionService.loadSchemaMetadata(datasource, schema);
        Workspace workspace;
        Project project;
        File file;
        if (!workspaceService.existsWorkspace(schema)) {
            workspace = WorkspaceFacade.createWorkspace(schema);
            project = workspace.createProject(schema);
            file = project.createFile(schema + ".schema", schemaMetadata.getBytes());
            logger.info(format("Created file [{0}] in Project [{1}] in Workspace [{2}]", file.getName(), project.getName(), workspace.getName()));
        } else {
            logger.warn(format("File with name [{0}] in Project [{1}] in Workspace [{2}] already exists and new metadata could not be exported", schema + ".schema", schema, schema));
            project = workspaceService.getProject(schema, schema);
            file = project.find(schema + ".schema").get(0);
        }
        return file.getWorkspacePath();
    }

    /**
     *
     * @param csvFiles the csvFiles
     * @return the transformed csvFiles to Json
     */
    private JsonObject transformCsvFilesToJson(ArrayList<CsvFile> csvFiles) {
        JsonObject csvimContent = new JsonObject();
        csvimContent.add("files", JsonHelper.toJsonTree(csvFiles));

        return csvimContent;
    }

    /**
     *
     * @param csvFile the csvFile
     * @param schema the schema
     * @param artefact the artefact
     * @param filePath the filePath
     */
    private void setCsvFileFields(CsvFile csvFile, String schema, String artefact, String filePath) {
        csvFile.setTable(artefact);
        csvFile.setSchema(schema);
        csvFile.setFile(filePath);
        csvFile.setHeader(true);
        csvFile.setUseHeaderNames(true);
        csvFile.setDelimField(",");
        csvFile.setDelimEnclosing("\"");
        csvFile.setDistinguishEmptyFromNull(true);

    }

}
