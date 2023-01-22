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
package org.eclipse.dirigible.components.data.structures.synchronizer.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.data.structures.domain.Table;
import org.eclipse.dirigible.components.data.structures.domain.TableConstraintForeignKey;
import org.eclipse.dirigible.components.database.DatabaseParameters;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.builders.table.AlterTableBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class TableForeignKeysDropProcessor.
 */
public class TableForeignKeysDropProcessor {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(TableForeignKeysDropProcessor.class);

	/**
	 * Execute the corresponding statement.
	 *
	 * @param connection the connection
	 * @param tableModel the table model
	 * @throws SQLException the SQL exception
	 */
	public static void execute(Connection connection, Table tableModel) throws SQLException {
		boolean caseSensitive = Boolean.parseBoolean(Configuration.get(DatabaseParameters.DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE, "false"));
		String tableName = tableModel.getName();
		if (caseSensitive) {
			tableName = "\"" + tableName + "\"";
		}
		
		if (tableModel.getConstraints() != null) {
			if (tableModel.getConstraints().getForeignKeys() != null && !tableModel.getConstraints().getForeignKeys().isEmpty()) {
				if (logger.isInfoEnabled()) {logger.info("Processing Alter Table Create Foreign Keys Table: " + tableName);}
				AlterTableBuilder alterTableBuilder = SqlFactory.getNative(connection).alter().table(tableName);
				for (TableConstraintForeignKey foreignKey : tableModel.getConstraints().getForeignKeys()) {
					String foreignKeyName = foreignKey.getName();
					if (caseSensitive) {
						foreignKeyName = "\"" + foreignKeyName + "\"";
					}
					alterTableBuilder.drop().foreignKey(foreignKeyName, foreignKey.getColumns(), 
							foreignKey.getReferencedTable(), foreignKey.getReferencedColumns());
				}
				
				final String sql = alterTableBuilder.build();
				if (logger.isInfoEnabled()) {logger.info(sql);}
				PreparedStatement statement = connection.prepareStatement(sql);
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

}
