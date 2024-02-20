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
package org.eclipse.dirigible.components.data.export.service;

import com.google.gson.*;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.components.api.platform.WorkspaceFacade;
import org.eclipse.dirigible.components.base.helpers.JsonHelper;
import org.eclipse.dirigible.components.data.csvim.domain.CsvFile;
import org.eclipse.dirigible.components.data.csvim.processor.CsvimProcessor;
import org.eclipse.dirigible.components.data.management.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.components.data.management.service.DatabaseDefinitionService;
import org.eclipse.dirigible.components.data.management.service.DatabaseExecutionService;
import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.eclipse.dirigible.components.ide.workspace.domain.File;
import org.eclipse.dirigible.components.ide.workspace.domain.Project;
import org.eclipse.dirigible.components.ide.workspace.domain.Workspace;
import org.eclipse.dirigible.components.ide.workspace.service.WorkspaceService;
import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.dialects.SqlDialectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;

/**
 * The Class DataImportService.
 */
@Service
public class DataImportService {

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(DataImportService.class);

    /**
     * The data sources manager.
     */
    @Autowired
    private DataSourcesManager datasourceManager;

    /** The csvim processor. */
    @Autowired
    private CsvimProcessor csvimProcessor;

    /**
     * Import csv.
     *
     * @param datasource the datasource
     * @param schema the schema
     * @param table the table
     * @param header the header
     * @param useHeaderNames the use header names
     * @param delimField the delim field
     * @param delimEnclosing the delim enclosing
     * @param sequence the sequence
     * @param distinguishEmptyFromNull the distinguish empty from null
     * @param is the is
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws Exception the exception
     */
    public void importData(String datasource, String schema, String table, Boolean header, Boolean useHeaderNames, String delimField,
            String delimEnclosing, String sequence, Boolean distinguishEmptyFromNull, InputStream is) throws IOException, Exception {



        DataSource dataSource = datasourceManager.getDataSource(datasource);
        try (Connection connection = dataSource.getConnection()) {
            ISqlDialect dialect = SqlDialectFactory.getDialect(connection);
            String productName = connection.getMetaData()
                                           .getDatabaseProductName();
            if ("MongoDB".equals(productName)) {
                dialect.importData(connection, table, is);
                return;
            }
            CsvFile csvFile = new CsvFile(null, table, schema, "import", header, useHeaderNames, delimField, delimEnclosing, sequence,
                    distinguishEmptyFromNull, null);
            csvimProcessor.process(csvFile, is, connection);
        }
    }

    /**
     * Import csv.
     *
     * @param datasource the datasource
     * @param schema the schema
     * @param table the table
     * @param is the is
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws Exception the exception
     */
    public void importData(String datasource, String schema, String table, InputStream is) throws IOException, Exception {
        importData(datasource, schema, table, true, true, ",", "\"", null, false, is);
    }

}
