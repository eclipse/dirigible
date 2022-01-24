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
package org.eclipse.dirigible.database.api.metadata;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper.FunctionColumnsIteratorCallback;

/**
 * The Function Metadata transport object.
 */
public class FunctionMetadata {

	private String name;

	private String type;

	private String remarks;

	private List<ParameterColumnMetadata> columns;
	
	private String kind = "function";

	/**
	 * Instantiates a new function metadata.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param remarks
	 *            the remarks
	 * @param connection
	 *            the connection
	 * @param catalogName
	 *            the catalog name
	 * @param schemaName
	 *            the schema name
	 * @param deep
	 *            whether to populate also the columns
	 * @throws SQLException
	 *             the SQL exception
	 */
	public FunctionMetadata(String name, String type, String remarks, Connection connection, String catalogName, String schemaName, boolean deep) throws SQLException {
		super();
		this.name = name;
		this.type = type;
		this.remarks = remarks;

		this.columns = new ArrayList<ParameterColumnMetadata>();

		if (deep) {
			DatabaseMetadataHelper.iterateFunctionDefinition(connection, catalogName, schemaName, name, new FunctionColumnsIteratorCallback() {
				@Override
				public void onFunctionColumn(String name, int kind, String type, int precision, int length, int scale, int radix, boolean nullable, String remarks) {
					columns.add(new ParameterColumnMetadata(name, kind, type, precision, length, scale, radix, nullable, remarks));
				}
			});
		}
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type
	 *            the new type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the remarks.
	 *
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * Sets the remarks.
	 *
	 * @param remarks
	 *            the new remarks
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * @return the columns
	 */
	public List<ParameterColumnMetadata> getColumns() {
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(List<ParameterColumnMetadata> columns) {
		this.columns = columns;
	}
	
	/**
	 * @return the kind
	 */
	public String getKind() {
		return kind;
	}
	
	/**
	 * @param kind the kind
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}

}
