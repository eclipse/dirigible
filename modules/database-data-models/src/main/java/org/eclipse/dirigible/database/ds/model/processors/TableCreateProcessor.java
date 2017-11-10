/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.ds.model.processors;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.eclipse.dirigible.database.ds.model.DataStructureTableColumnModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableConstraintCheckModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableConstraintForeignKeyModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableConstraintUniqueModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.eclipse.dirigible.database.sql.DataType;
import org.eclipse.dirigible.database.sql.ISqlKeywords;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.builders.table.CreateTableBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableCreateProcessor {

	private static final Logger logger = LoggerFactory.getLogger(TableCreateProcessor.class);

	public static void execute(Connection connection, DataStructureTableModel tableModel) throws SQLException {
		logger.info("Processing Create Table: " + tableModel.getName());
		CreateTableBuilder createTableBuilder = SqlFactory.getNative(connection).create().table(tableModel.getName());
		List<DataStructureTableColumnModel> columns = tableModel.getColumns();
		for (DataStructureTableColumnModel columnModel : columns) {
			String name = columnModel.getName();
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
				if (type.equals(DataType.VARCHAR) || type.equals(DataType.CHAR)) {
					args = ISqlKeywords.OPEN + length + ISqlKeywords.CLOSE;
				}
			} else if ((precision != null) && (scale != null)) {
				if (type.equals(DataType.DECIMAL)) {
					args = ISqlKeywords.OPEN + precision + "," + scale + ISqlKeywords.CLOSE;
				}
			}
			if (defaultValue != null) {
				if ("".equals(defaultValue)) {
					if ((type.equals(DataType.VARCHAR) || type.equals(DataType.CHAR))) {
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
			if (tableModel.getConstraints().getForeignKeys() != null) {
				for (DataStructureTableConstraintForeignKeyModel foreignKey : tableModel.getConstraints().getForeignKeys()) {
					createTableBuilder.foreignKey(foreignKey.getName(), foreignKey.getColumns(), foreignKey.getReferencedTable(),
							foreignKey.getReferencedColumns());
				}
			}
			if (tableModel.getConstraints().getUniqueIndices() != null) {
				for (DataStructureTableConstraintUniqueModel uniqueIndex : tableModel.getConstraints().getUniqueIndices()) {
					createTableBuilder.unique(uniqueIndex.getName(), uniqueIndex.getColumns());
				}
			}
			if (tableModel.getConstraints().getChecks() != null) {
				for (DataStructureTableConstraintCheckModel check : tableModel.getConstraints().getChecks()) {
					createTableBuilder.check(check.getName(), check.getExpression());
				}
			}
		}

		final String sql = createTableBuilder.build();
		Statement statement = connection.createStatement();
		try {
			logger.info(sql);
			statement.executeUpdate(sql);
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
