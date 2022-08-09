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
package org.eclipse.dirigible.core.scheduler.quartz;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.database.ds.model.DataStructureModelFactory;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.eclipse.dirigible.database.ds.model.processors.TableCreateProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Abstract Database Layout Initializer.
 */
public abstract class AbstractDatabaseLayoutInitializer {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(AbstractDatabaseLayoutInitializer.class);

//	private DataSource datasource = (DataSource) StaticObjects.get(StaticObjects.DATASOURCE);

	/**
	 * Initialize the database schema for Quartz.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public abstract void initialize() throws SQLException, IOException;

	/**
	 * Creates a table.
	 *
	 * @param connection
	 *            the connection
	 * @param model
	 *            the model
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws SQLException
	 *             the SQL exception
	 */
	protected void createTable(Connection connection, String model) throws IOException, SQLException {
		InputStream in = AbstractDatabaseLayoutInitializer.class.getResourceAsStream(model);
		try {
			String tableFile = IOUtils.toString(in, StandardCharsets.UTF_8);
			DataStructureTableModel tableModel = DataStructureModelFactory.parseTable(tableFile);
			TableCreateProcessor.execute(connection, tableModel);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

}
