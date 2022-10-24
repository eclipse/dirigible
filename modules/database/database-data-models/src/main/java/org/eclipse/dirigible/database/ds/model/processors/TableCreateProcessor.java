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
package org.eclipse.dirigible.database.ds.model.processors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.api.IDatabase;
import org.eclipse.dirigible.database.ds.model.DataStructureTableColumnModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableConstraintCheckModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableConstraintForeignKeyModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableConstraintUniqueModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableIndexModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.eclipse.dirigible.database.sql.DataType;
import org.eclipse.dirigible.database.sql.ISqlKeywords;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.builders.table.CreateTableBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Table Create Processor.
 */
public class TableCreateProcessor {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(TableCreateProcessor.class);
	
	/**
	 * Execute the corresponding statement.
	 *
	 * @param connection the connection
	 * @param tableModel the table model
	 * @throws SQLException the SQL exception
	 */
	public static void execute(Connection connection, DataStructureTableModel tableModel) throws SQLException {
		execute(connection, tableModel, false);
	}

	/**
	 * Execute the corresponding statement.
	 *
	 * @param connection the connection
	 * @param tableModel the table model
	 * @param skipForeignKeys the skip foreign keys
	 * @throws SQLException the SQL exception
	 */
	public static void execute(Connection connection, DataStructureTableModel tableModel, boolean skipForeignKeys) throws SQLException {
		boolean caseSensitive = Boolean.parseBoolean(Configuration.get(IDatabase.DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE, "false"));
		String tableName = tableModel.getName();
		if (caseSensitive) {
			tableName = "\"" + tableName + "\"";
		}
		if (logger.isInfoEnabled()) {logger.info("Processing Create Table: " + tableName);}
		CreateTableBuilder createTableBuilder = SqlFactory.getNative(connection).create().table(tableName);
		List<DataStructureTableColumnModel> columns = tableModel.getColumns();
		List<DataStructureTableIndexModel> indexes = tableModel.getIndexes();
		for (DataStructureTableColumnModel columnModel : columns) {
			String name = columnModel.getName();
			if (caseSensitive) {
				name = "\"" + name + "\"";
			}
			DataType type = DataType.valueOfByName(columnModel.getType());
			String length = columnModel.getLength();
			boolean isNullable = columnModel.isNullable();
			boolean isPrimaryKey = columnModel.isPrimaryKey();
			boolean isUnique = columnModel.isUnique();
			String defaultValue = columnModel.getDefaultValue();
			String scale = columnModel.getScale();
			String args = "";
			if (length != null) {
				if (type.equals(DataType.VARCHAR) || type.equals(DataType.CHAR) || type.equals(DataType.NVARCHAR) || type.equals(DataType.CHARACTER_VARYING)) {
					args = ISqlKeywords.OPEN + length + ISqlKeywords.CLOSE;
				}
				if (scale != null) {
					if (type.equals(DataType.DECIMAL)) {
						args = ISqlKeywords.OPEN + length + "," + scale + ISqlKeywords.CLOSE;
					}
				}
			}
			if (defaultValue != null) {
				if ("".equals(defaultValue)) {
					if (type.equals(DataType.VARCHAR) || type.equals(DataType.CHAR) || type.equals(DataType.NVARCHAR) || type.equals(DataType.CHARACTER_VARYING)) {
						args += " DEFAULT '" + defaultValue + "' ";
					}
				} else {
					args += " DEFAULT " + defaultValue + " ";
				}

			}
			createTableBuilder.column(name, type, isPrimaryKey, isNullable, isUnique, args);
		}
		if (tableModel.getConstraints() != null) {
			if (tableModel.getConstraints().getPrimaryKey() != null) {
				String[] primaryKeyColumns = new String[tableModel.getConstraints().getPrimaryKey().getColumns().length];
				int i = 0;
				for (String column : tableModel.getConstraints().getPrimaryKey().getColumns()) {
					if (caseSensitive) {
						primaryKeyColumns[i++] = "\"" + column + "\"";
					} else {
						primaryKeyColumns[i++] = column;
					}
				}
				
				createTableBuilder.primaryKey(primaryKeyColumns);
			}
			if (!skipForeignKeys) {
				if (tableModel.getConstraints().getForeignKeys() != null && !tableModel.getConstraints().getForeignKeys().isEmpty()) {
					for (DataStructureTableConstraintForeignKeyModel foreignKey : tableModel.getConstraints().getForeignKeys()) {
						String foreignKeyName = foreignKey.getName();
						if (caseSensitive) {
							foreignKeyName = "\"" + foreignKeyName + "\"";
						}
						String[] foreignKeyColumns = new String[foreignKey.getColumns().length];
						int i = 0;
						for (String column : foreignKey.getColumns()) {
							if (caseSensitive) {
								foreignKeyColumns[i++] = "\"" + column + "\"";
							} else {
								foreignKeyColumns[i++] = column;
							}
						}
						String foreignKeyReferencedTable = foreignKey.getReferencedTable();
						if (caseSensitive) {
							foreignKeyReferencedTable = "\"" + foreignKeyReferencedTable + "\"";
						}
						String[] foreignKeyReferencedColumns = new String[foreignKey.getReferencedColumns().length];
						i = 0;
						for (String column : foreignKey.getReferencedColumns()) {
							if (caseSensitive) {
								foreignKeyReferencedColumns[i++] = "\"" + column + "\"";
							} else {
								foreignKeyReferencedColumns[i++] = column;
							}
						}
						
						createTableBuilder.foreignKey(foreignKeyName, foreignKeyColumns, foreignKeyReferencedTable,
								foreignKeyReferencedColumns);
					}
				}
			}
			if (tableModel.getConstraints().getUniqueIndices() != null) {
				for (DataStructureTableConstraintUniqueModel uniqueIndex : tableModel.getConstraints().getUniqueIndices()) {
					String uniqueIndexName = uniqueIndex.getName();
					if (caseSensitive) {
						uniqueIndexName = "\"" + uniqueIndexName + "\"";
					}
					String[] uniqueIndexColumns = new String[uniqueIndex.getColumns().length];
					int i = 0;
					for (String column : uniqueIndex.getColumns()) {
						if (caseSensitive) {
							uniqueIndexColumns[i++] = "\"" + column + "\"";
						} else {
							uniqueIndexColumns[i++] = column;
						}
					}
					createTableBuilder.unique(uniqueIndexName, uniqueIndexColumns);
				}
			}
			if (tableModel.getConstraints().getChecks() != null) {
				for (DataStructureTableConstraintCheckModel check : tableModel.getConstraints().getChecks()) {
					String checkName = check.getName();
					if (caseSensitive) {
						checkName = "\"" + checkName + "\"";
					}
					createTableBuilder.check(checkName, check.getExpression());
				}
			}
		}
		if(indexes != null){
			for(DataStructureTableIndexModel indexModel : indexes) {
				String name = indexModel.getName();
				String type = indexModel.getType();
				Boolean isUnique = indexModel.isUnique();
				Set<String> indexColumns = indexModel.getColumns();
				createTableBuilder.index(name, isUnique, type, indexColumns);
			}
		}

		final String sql = createTableBuilder.build();
		if (logger.isInfoEnabled()) {logger.info(sql);}
		String[] parts = sql.split(CreateTableBuilder.STATEMENT_DELIMITER);
		for (String part : parts) {
			PreparedStatement statement = connection.prepareStatement(part);
			try {
				statement.executeUpdate();
			} catch (SQLException e) {
				if (logger.isErrorEnabled()) {logger.error(sql);}
				if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
				throw new SQLException(e.getMessage(), e);
			} finally {
				if (statement != null) {
					statement.close();
				}
			}
		}
	}

}