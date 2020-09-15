/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.database.ds.model.processors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.ds.model.DataStructureTableColumnModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableConstraintCheckModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableConstraintForeignKeyModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableConstraintUniqueModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.eclipse.dirigible.database.ds.model.IDataStructureModel;
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
	 * @throws SQLException the SQL exception
	 */
	public static void execute(Connection connection, DataStructureTableModel tableModel, boolean skipForeignKeys) throws SQLException {
		boolean caseSensitive = Boolean.parseBoolean(Configuration.get(IDataStructureModel.DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE, "false"));
		String tableName = tableModel.getName();
		if (caseSensitive) {
			tableName = "\"" + tableName + "\"";
		}
		logger.info("Processing Create Table: " + tableName);
		CreateTableBuilder createTableBuilder = SqlFactory.getNative(connection).create().table(tableName);
		List<DataStructureTableColumnModel> columns = tableModel.getColumns();
		for (DataStructureTableColumnModel columnModel : columns) {
			String name = columnModel.getName();
			if (caseSensitive) {
				name = "\"" + name + "\"";
			}
			DataType type = DataType.valueOf(columnModel.getType());
			String length = columnModel.getLength();
			boolean isNullable = columnModel.isNullable();
			boolean isPrimaryKey = columnModel.isPrimaryKey();
			boolean isUnique = columnModel.isUnique();
			String defaultValue = columnModel.getDefaultValue();
			String precision = columnModel.getPrecision();
			String scale = columnModel.getScale();
			String args = "";
			if (length != null) {
				if (type.equals(DataType.VARCHAR) || type.equals(DataType.CHAR) || type.equals(DataType.NVARCHAR)) {
					args = ISqlKeywords.OPEN + length + ISqlKeywords.CLOSE;
				}
			} else if ((precision != null) && (scale != null)) {
				if (type.equals(DataType.DECIMAL)) {
					args = ISqlKeywords.OPEN + precision + "," + scale + ISqlKeywords.CLOSE;
				}
			}
			if (defaultValue != null) {
				if ("".equals(defaultValue)) {
					if (type.equals(DataType.VARCHAR) || type.equals(DataType.CHAR) || type.equals(DataType.NVARCHAR)) {
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
				createTableBuilder.primaryKey(tableModel.getConstraints().getPrimaryKey().getColumns());
			}
			if (!skipForeignKeys) {
				if (tableModel.getConstraints().getForeignKeys() != null && !tableModel.getConstraints().getForeignKeys().isEmpty()) {
					for (DataStructureTableConstraintForeignKeyModel foreignKey : tableModel.getConstraints().getForeignKeys()) {
						String foreignKeyName = foreignKey.getName();
						if (caseSensitive) {
							foreignKeyName = "\"" + foreignKeyName + "\"";
						}
						createTableBuilder.foreignKey(foreignKeyName, foreignKey.getColumns(), foreignKey.getReferencedTable(),
								foreignKey.getReferencedColumns());
					}
				}
			}
			if (tableModel.getConstraints().getUniqueIndices() != null) {
				for (DataStructureTableConstraintUniqueModel uniqueIndex : tableModel.getConstraints().getUniqueIndices()) {
					String uniqueIndexName = uniqueIndex.getName();
					if (caseSensitive) {
						uniqueIndexName = "\"" + uniqueIndexName + "\"";
					}
					createTableBuilder.unique(uniqueIndexName, uniqueIndex.getColumns());
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

		final String sql = createTableBuilder.build();
		logger.info(sql);
		PreparedStatement statement = connection.prepareStatement(sql);
		try {
			statement.executeUpdate();
		} catch (SQLException e) {
			logger.error(sql);
			logger.error(e.getMessage(), e);
			throw new SQLException(e.getMessage(), e);
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

}
