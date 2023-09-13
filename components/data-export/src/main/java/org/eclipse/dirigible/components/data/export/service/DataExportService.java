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

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.output.WriterOutputStream;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.components.api.platform.WorkspaceFacade;
import org.eclipse.dirigible.components.base.helpers.JsonHelper;
import org.eclipse.dirigible.components.data.csvim.domain.CsvFile;
import org.eclipse.dirigible.components.data.management.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.components.data.management.service.DatabaseDefinitionService;
import org.eclipse.dirigible.components.data.management.service.DatabaseExecutionService;
import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.eclipse.dirigible.components.data.transfer.service.DataTransferSchemaTopologyService;
import org.eclipse.dirigible.components.ide.workspace.domain.File;
import org.eclipse.dirigible.components.ide.workspace.domain.Project;
import org.eclipse.dirigible.components.ide.workspace.domain.Workspace;
import org.eclipse.dirigible.components.ide.workspace.service.WorkspaceService;
import org.eclipse.dirigible.database.sql.dialects.SqlDialectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * The Class DataExportService.
 */
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
     * The data transfer schema topology service.
     */
    private final DataTransferSchemaTopologyService dataTransferSchemaTopologyService;

    /**
     * Instantiates a new data export service.
     *
     * @param datasourceManager         the datasource manager
     * @param workspaceService          the workspace service
     * @param databaseExecutionService  the database execution service
     * @param databaseDefinitionService the database definition service
     * @param dataTransferSchemaTopologyService the data transfer schema topology service
     */
    @Autowired
    public DataExportService(DataSourcesManager datasourceManager, WorkspaceService workspaceService, DatabaseExecutionService databaseExecutionService,
    		DatabaseDefinitionService databaseDefinitionService, DataTransferSchemaTopologyService dataTransferSchemaTopologyService) {
        this.datasourceManager = datasourceManager;
        this.workspaceService = workspaceService;
        this.databaseExecutionService = databaseExecutionService;
        this.databaseDefinitionService = databaseDefinitionService;
        this.dataTransferSchemaTopologyService = dataTransferSchemaTopologyService;
    }

    /**
     * Export schema in csvs.
     *
     * @param datasource the datasource
     * @param schema     the schema
     */
    public void exportSchemaInCsvs(String datasource, String schema) {
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
                        try(Connection connection = dataSource.getConnection()) {
                        	sql = SqlDialectFactory.getDialect(connection).allQuery(connection,  "\"" + schema + "\".\"" + artifact + "\"");
                        } catch (Exception e) {
                        	logger.error(e.getMessage(), e);
            			}
                        
                        StringWriter sw = new StringWriter();
        				OutputStream output;
        				try {
        					output = WriterOutputStream.builder().setWriter(sw).setCharset(StandardCharsets.UTF_8).get();
        				} catch (IOException e) {
        					throw new SQLException(e);
        				}
        				databaseExecutionService.executeStatement(dataSource, sql, true, false, true, false, output);
                        String tableExport = sw.toString();

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
     * Export metadata as project.
     *
     * @param datasource the datasource
     * @param schema     the schema
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
     * Transform csv files to json.
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
     * Sets the csv file fields.
     *
     * @param csvFile  the csvFile
     * @param schema   the schema
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
    
    /**
     * Export metadata as project.
     *
     * @param datasource the datasource
     * @param schema     the schema
     * @return the workspace path of the file
     * @throws SQLException the SQL exception
     */
    public String exportSchemaTopology(String datasource, String schema) throws SQLException {
    	javax.sql.DataSource dataSource = datasourceManager.getDataSource(datasource);
        if (dataSource != null) {
        	List<String> sorted = dataTransferSchemaTopologyService.sortTopologically(dataSource, schema);
        	return sorted.stream().collect(Collectors.joining("\n"));
        }
        return "DataSource does not exist: " + datasource;
    }
}
