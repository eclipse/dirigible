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
package org.eclipse.dirigible.database.transfer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.api.topology.TopologicalSorter;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.processors.table.PersistenceCreateTableProcessor;
import org.eclipse.dirigible.database.persistence.utils.DatabaseMetadataUtil;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.builders.records.InsertBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataTransferManager {
	
	private static final Logger logger = LoggerFactory.getLogger(DataTransferManager.class);
	
	public static final void transfer(DataSource source, DataSource target, DataTransferConfiguration configuration, IDataTransferCallbackHandler handler) throws DataTransferException {
		
		if (handler != null) {
			handler = new DummyDataTransferCallbackHandler();
		}
		
		handler.transferStarted(configuration);
		
		try (Connection sourceConnection = source.getConnection()) {
			
			try (Connection targetConnection = target.getConnection()) {
				
				List<PersistenceTableModel> tables = reverseTables(source, configuration.getSourceSchema(), handler);
				tables = sortTables(tables, handler);
				sourceConnection.setSchema(configuration.getSourceSchema());
				targetConnection.setSchema(configuration.getTargetSchema());
				transferData(tables, sourceConnection, targetConnection, handler);
				
				handler.transferFinished(tables.size());
				
			} catch (SQLException e) {
				String error = "Error occured when trying to connect to the target database";
				logger.error(error, e);
				handler.transferFailed(error);
				throw new DataTransferException(e);
			}	
			
		} catch (SQLException e) {
			String error = "Error occured when trying to connect to the source database";
			logger.error(error, e);
			handler.transferFailed(error);
			throw new DataTransferException(e);
		}
		
	}

	private static List<PersistenceTableModel> reverseTables(DataSource dataSource, String schemaName, IDataTransferCallbackHandler handler) throws SQLException {
		
		handler.metadataLoadingStarted();
		
		List<PersistenceTableModel> tables = new ArrayList<PersistenceTableModel>();
		DatabaseMetadataUtil databaseMetadataUtil = new DatabaseMetadataUtil();
		
		List<String> tableNames = DatabaseMetadataUtil.getTablesInSchema(dataSource, schemaName);
		for (String tableName : tableNames) {
			PersistenceTableModel persistenceTableModel = databaseMetadataUtil.getTableMetadata(tableName, schemaName, dataSource);
			tables.add(persistenceTableModel);
		}
		
		handler.metadataLoadingFinished(tables.size());
		
		return tables;
	}
	
	private static List<PersistenceTableModel> sortTables(List<PersistenceTableModel> tables, IDataTransferCallbackHandler handler) {
		
		handler.sortingStarted(tables);
		
		// Prepare for sorting
		List<DataTransferSortableTableWrapper> list = new ArrayList<DataTransferSortableTableWrapper>();
		Map<String, DataTransferSortableTableWrapper> wrappers = new HashMap<String, DataTransferSortableTableWrapper>();
		for (PersistenceTableModel tableModel : tables) {
			DataTransferSortableTableWrapper wrapper = new DataTransferSortableTableWrapper(tableModel, wrappers);
			list.add(wrapper);
		}
		
		// Topological sorting by dependencies
		TopologicalSorter<DataTransferSortableTableWrapper> sorter = new TopologicalSorter<>();
		list = sorter.sort(list);
		
		// Prepare result
		List<PersistenceTableModel> result = new ArrayList<PersistenceTableModel>();
		for (DataTransferSortableTableWrapper wrapper : list) {
			PersistenceTableModel tableModel = wrapper.getTableModel();
			result.add(tableModel);
		}
		
		handler.sortingFinished(result);
		
		return result;
	}

	private static void transferData(List<PersistenceTableModel> tables, Connection sourceConnection, Connection targetConnection, IDataTransferCallbackHandler handler) {
		
		handler.dataTransferStarted();
		
		for (PersistenceTableModel tableModel : tables) {
			logger.info(String.format("Data transfer of table %s has been started...", tableModel.getTableName()));
			handler.tableTransferStarted(tableModel.getTableName());
			try {
				
				PersistenceCreateTableProcessor createTableProcessor = new PersistenceCreateTableProcessor(null);
				createTableProcessor.create(targetConnection, tableModel);
				
				String selectSQL = SqlFactory.getNative(sourceConnection)
						.select()
						.column("*")
						.from(tableModel.getTableName())
						.build();
				
				handler.tableSelectSQL(selectSQL);
				
				try (PreparedStatement pstmtSource = sourceConnection.prepareStatement(selectSQL)) {
					try (ResultSet rs = pstmtSource.executeQuery()) {
						ResultSetMetaData resultSetMetaData = rs.getMetaData();
						
						InsertBuilder insertBuilder = SqlFactory.getNative(targetConnection)
								.insert()
								.into(tableModel.getTableName());
						for (int i=1; i<=resultSetMetaData.getColumnCount(); i++) {
							String columnName = resultSetMetaData.getColumnName(i);
							insertBuilder.column(columnName);
						}
						
						String insertSQL = insertBuilder.build();
						handler.tableInsertSQL(insertSQL);
						
						int transfertRecords = 0;
						try (PreparedStatement pstmtTarget = targetConnection.prepareStatement(insertSQL)) {
							while (rs.next()) {
								for (int i=1; i<=resultSetMetaData.getColumnCount(); i++) {
									int type = resultSetMetaData.getColumnType(i);
									switch (type) {
									case java.sql.Types.ARRAY:
										pstmtTarget.setArray(i, rs.getArray(i));
										break;
									case java.sql.Types.BIGINT:
										pstmtTarget.setLong(i, rs.getLong(i));
										break;
									case java.sql.Types.BINARY:
										pstmtTarget.setBinaryStream(i, rs.getBinaryStream(i));
										break;
									case java.sql.Types.BIT:
										pstmtTarget.setBoolean(i, rs.getBoolean(i));
										break;
									case java.sql.Types.BLOB:
										pstmtTarget.setBlob(i, rs.getBlob(i));
										break;
									case java.sql.Types.BOOLEAN:
										pstmtTarget.setBoolean(i, rs.getBoolean(i));
										break;
									case java.sql.Types.CHAR:
										pstmtTarget.setString(i, rs.getString(i));
										break;
									case java.sql.Types.CLOB:
										pstmtTarget.setClob(i, rs.getClob(i));
										break;
									case java.sql.Types.DATE:
										pstmtTarget.setDate(i, rs.getDate(i));
										break;
									case java.sql.Types.DECIMAL:
										pstmtTarget.setBigDecimal(i, rs.getBigDecimal(i));
										break;
									case java.sql.Types.DOUBLE:
										pstmtTarget.setDouble(i, rs.getDouble(i));
										break;
									case java.sql.Types.FLOAT:
										pstmtTarget.setFloat(i, rs.getFloat(i));
										break;
									case java.sql.Types.INTEGER:
										pstmtTarget.setInt(i, rs.getInt(i));
										break;
									case java.sql.Types.LONGNVARCHAR:
										pstmtTarget.setString(i, rs.getString(i));
										break;
									case java.sql.Types.LONGVARBINARY:
										pstmtTarget.setBinaryStream(i, rs.getBinaryStream(i));
										break;
									case java.sql.Types.LONGVARCHAR:
										pstmtTarget.setString(i, rs.getString(i));
										break;
									case java.sql.Types.NCHAR:
										pstmtTarget.setString(i, rs.getString(i));
										break;
									case java.sql.Types.NCLOB:
										pstmtTarget.setString(i, rs.getString(i));
										break;
									case java.sql.Types.NUMERIC:
										pstmtTarget.setDouble(i, rs.getDouble(i));
										break;
									case java.sql.Types.NVARCHAR:
										pstmtTarget.setString(i, rs.getString(i));
										break;
									case java.sql.Types.REAL:
										pstmtTarget.setFloat(i, rs.getFloat(i));
										break;
									case java.sql.Types.SMALLINT:
										pstmtTarget.setShort(i, rs.getShort(i));
										break;
									case java.sql.Types.TIME:
										pstmtTarget.setTime(i, rs.getTime(i));
										break;
									case java.sql.Types.TIME_WITH_TIMEZONE:
										pstmtTarget.setTime(i, rs.getTime(i));
										break;
									case java.sql.Types.TIMESTAMP:
										pstmtTarget.setTimestamp(i, rs.getTimestamp(i));
										break;
									case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
										pstmtTarget.setTimestamp(i, rs.getTimestamp(i));
										break;
									case java.sql.Types.TINYINT:
										pstmtTarget.setByte(i, rs.getByte(i));
										break;
									case java.sql.Types.VARBINARY:
										pstmtTarget.setString(i, rs.getString(i));
										break;
									case java.sql.Types.VARCHAR:
										pstmtTarget.setString(i, rs.getString(i));
										break;
									default:
										pstmtTarget.setObject(i, rs.getObject(i));
										break;
									}
								}
								pstmtTarget.executeUpdate();
								handler.recordTransferFinished(tableModel.getTableName(), ++transfertRecords);
							}
						}
					}
				}
				
				String message = String.format("Data of table %s has been transferred successfully.", tableModel.getTableName());
				logger.info(message);
				
				handler.tableTransferFinished(tableModel.getTableName());
				
			} catch(Exception e) {
				String error = "Error occured while transferring the data for table: " + tableModel.getTableName();
				logger.error(error, e);
				handler.tableTransferFailed(tableModel.getTableName(), error);
			}
		}
		
		handler.dataTransferFinished();
	}


}
