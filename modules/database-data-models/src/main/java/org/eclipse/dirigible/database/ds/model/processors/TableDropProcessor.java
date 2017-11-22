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

import static java.text.MessageFormat.format;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Table Drop Processor.
 */
public class TableDropProcessor {

	private static final Logger logger = LoggerFactory.getLogger(TableDropProcessor.class);

	/**
	 * Execute the corresponding statement.
	 *
	 * @param connection the connection
	 * @param tableModel the table model
	 * @throws SQLException the SQL exception
	 */
	public static void execute(Connection connection, DataStructureTableModel tableModel) throws SQLException {
		logger.info("Processing Drop Table: " + tableModel.getName());
		if (SqlFactory.getNative(connection).exists(connection, tableModel.getName())) {
			String sql = SqlFactory.getNative(connection).select().column("COUNT(*)").from(tableModel.getName()).build();
			PreparedStatement statement = connection.prepareStatement(sql);
			try {
				logger.info(sql);
				ResultSet resultSet = statement.executeQuery(sql);
				if (resultSet.next()) {
					int count = resultSet.getInt(1);
					if (count > 0) {
						logger.error(
								format("Drop operation for the non empty Table [{0}] will not be executed. Delete all the records in the table first.",
										tableModel.getName()));
						return;
					}
				}
			} catch (SQLException e) {
				logger.error(sql);
				logger.error(e.getMessage(), e);
			} finally {
				if (statement != null) {
					statement.close();
				}
			}

			sql = SqlFactory.getNative(connection).drop().table(tableModel.getName()).build();
			statement = connection.prepareStatement(sql);
			try {
				logger.info(sql);
				statement.executeUpdate();
			} catch (SQLException e) {
				logger.error(sql);
				logger.error(e.getMessage(), e);
			} finally {
				if (statement != null) {
					statement.close();
				}
			}
		}
	}

}
