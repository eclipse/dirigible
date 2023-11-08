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
package org.eclipse.dirigible.components.data.csvim.utils;

import org.eclipse.dirigible.components.api.platform.ProblemsFacade;
import org.eclipse.dirigible.components.data.csvim.synchronizer.CsvProcessor;
import org.eclipse.dirigible.components.data.csvim.synchronizer.CsvimProcessor;
import org.eclipse.dirigible.components.data.management.domain.TableMetadata;
import org.eclipse.dirigible.components.data.management.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The Class CsvimUtils.
 */
public class CsvimUtils {

    /** The Constant PROGRAM_DEFAULT. */
    public static final String PROGRAM_DEFAULT = "Eclipse Dirigible";

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(CsvProcessor.class);

    /**
     * Use to log errors from artifact processing.
     *
     * @param errorMessage the error message
     * @param errorType the error type
     * @param location the location
     * @param artifactType the artifact type
     * @param module the module
     */
    public static void logProcessorErrors(String errorMessage, String errorType, String location, String artifactType, String module) {
        try {
            ProblemsFacade.save(location, errorType, "", "", errorMessage, "", artifactType, module, CsvimProcessor.class.getName(),
                    PROGRAM_DEFAULT);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("There is an issue with logging of the Errors.");
            }
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * Gets the table metadata.
     *
     * @param tableName the table name
     * @param connection the connection
     * @return the table metadata
     */
    public static TableMetadata getTableMetadata(String tableName, Connection connection) {
        try {
            return DatabaseMetadataHelper.describeTable(connection, null, DatabaseMetadataHelper.getTableSchema(connection, tableName),
                    tableName);
        } catch (SQLException sqlException) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Error occurred while trying to read table metadata for table with name: %s", tableName),
                        sqlException);
            }
        }
        return null;
    }
}
