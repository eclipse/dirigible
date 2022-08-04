/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.transfer.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.utils.DatabaseMetadataUtil;
import org.eclipse.dirigible.database.transfer.api.IDataTransferCallbackHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataTransferReverseTableProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(DataTransferReverseTableProcessor.class);
	
	private static DatabaseMetadataUtil databaseMetadataUtil = new DatabaseMetadataUtil();
	
	public static List<PersistenceTableModel> reverseTables(DataSource dataSource, String schemaName, IDataTransferCallbackHandler handler) throws SQLException {
		
		if (handler != null) {
			handler.metadataLoadingStarted();
		}
		
		List<PersistenceTableModel> tables = new ArrayList<PersistenceTableModel>();
		
		List<String> tableNames = DatabaseMetadataUtil.getTablesInSchema(dataSource, schemaName);
		if (tableNames != null) {
			for (String tableName : tableNames) {
				PersistenceTableModel persistenceTableModel = reverseTable(dataSource, schemaName, tableName);
				tables.add(persistenceTableModel);
			}
		} else {
			String error = schemaName + " does not exist in the target database";
			logger.error(error);
			if (handler != null) {
				handler.metadataLoadingError(error);
			}
		}
		
		if (handler != null) {
			handler.metadataLoadingFinished(tables.size());
		}
		
		return tables;
	}

	public static PersistenceTableModel reverseTable(DataSource dataSource, String schemaName,
			String tableName) throws SQLException {
		return databaseMetadataUtil.getTableMetadata(tableName, schemaName, dataSource);
	}

}
