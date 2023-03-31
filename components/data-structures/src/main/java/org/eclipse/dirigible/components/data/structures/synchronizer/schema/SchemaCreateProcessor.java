/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.data.structures.synchronizer.schema;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.dirigible.components.data.structures.domain.Schema;
import org.eclipse.dirigible.components.data.structures.domain.Table;
import org.eclipse.dirigible.components.data.structures.domain.View;
import org.eclipse.dirigible.components.data.structures.synchronizer.table.TableCreateProcessor;
import org.eclipse.dirigible.components.data.structures.synchronizer.table.TableForeignKeysCreateProcessor;
import org.eclipse.dirigible.components.data.structures.synchronizer.view.ViewCreateProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Schema Create Processor.
 */
public class SchemaCreateProcessor {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(SchemaCreateProcessor.class);

	/**
	 * Execute the corresponding statement.
	 *
	 * @param connection the connection
	 * @param schemaModel the schema model
	 * @throws SQLException the SQL exception
	 */
	public static void execute(Connection connection, Schema schemaModel) throws SQLException {
		for (Table tableModel : schemaModel.getTables()) {
			TableCreateProcessor.execute(connection, tableModel, true);
		}
		for (Table tableModel : schemaModel.getTables()) {
			TableForeignKeysCreateProcessor.execute(connection, tableModel);
		}
		for (View viewModel : schemaModel.getViews()) {
			ViewCreateProcessor.execute(connection, viewModel);
		}
	}

}
