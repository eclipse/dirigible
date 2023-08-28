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

package org.eclipse.dirigible.components.data.export.endpoint;

import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.data.export.service.DataExportService;
import org.eclipse.dirigible.components.data.management.service.DatabaseExportService;
import org.eclipse.dirigible.components.data.management.service.DatabaseMetadataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;

import static java.text.MessageFormat.format;

/**
 * Front facing REST service serving export data.
 */
@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_DATA + "project")
public class DataExportEndpoint {

    /**
     * The database metadata service.
     */
    private final DatabaseMetadataService databaseMetadataService;

    /**
     * The database metadata service.
     */
    private final DataExportService dataExportService;

    /**
     * Instantiates a new data export endpoint.
     *
     * @param databaseMetadataService the database metadata service
     * @param dataExportService       the data export service
     */
    public DataExportEndpoint(DatabaseMetadataService databaseMetadataService, DataExportService dataExportService) {
        this.databaseMetadataService = databaseMetadataService;
        this.dataExportService = dataExportService;
    }

    /**
     * Export metadata in project as *.schema file.
     *
     * @param datasource the datasource
     * @param schema the schema name
     * @return the response
     * @throws SQLException the SQL exception
     * @throws URISyntaxException the URI syntax exception
     */
    @PutMapping(value = "/metadata/{datasource}/{schema}")
    public ResponseEntity<URI> exportMetadataAsProject(
            @PathVariable("datasource") String datasource,
            @PathVariable("schema") String schema) throws SQLException, URISyntaxException {

        if (!databaseMetadataService.existsDataSourceMetadata(datasource)) {
            String error = format("Datasource {0} does not exist.", datasource);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
        }

        String fileWorkspacePath = dataExportService.exportMetadataAsProject(datasource, schema);

        return ResponseEntity.ok(new URI("/" + BaseEndpoint.PREFIX_ENDPOINT_IDE + "workspaces" + fileWorkspacePath));
    }

    /**
     * Export schema data in project as csvs and csvim.
     *
     * @param datasource the datasource
     * @param schema     the schema name
     * @return the response
     * @throws URISyntaxException the URI syntax exception
     * @throws SQLException the SQL exception
     */
    @PutMapping(value = "/csv/{datasource}/{schema}")
    public ResponseEntity<URI> exportDataAsProject(
            @PathVariable("datasource") String datasource,
            @PathVariable("schema") String schema) throws URISyntaxException, SQLException {

        if (!databaseMetadataService.existsDataSourceMetadata(datasource)) {
            String error = format("Datasource {0} does not exist.", datasource);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
        }

         dataExportService.exportSchemaInCsvs(datasource, schema);

        return ResponseEntity.ok(new URI("/" + BaseEndpoint.PREFIX_ENDPOINT_IDE + "workspaces" + "/" + schema));
    }
}
