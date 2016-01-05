/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.datasource.db.dialect;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface IDialectSpecifier {

	public static final String DIALECT_TIMESTAMP = "$TIMESTAMP$"; //$NON-NLS-1$
	public static final String DIALECT_BLOB = "$BLOB$"; //$NON-NLS-1$
	public static final String DIALECT_CLOB = "$CLOB$"; //$NON-NLS-1$
	public static final String DIALECT_CURRENT_TIMESTAMP = "$CURRENT_TIMESTAMP$"; //$NON-NLS-1$

	public static final String DIALECT_KEY_VARCHAR = "$KEY_VARCHAR$"; //$NON-NLS-1$
	public static final String DIALECT_BIG_VARCHAR = "$BIG_VARCHAR$"; //$NON-NLS-1$

	String specify(String sql);

	String getSpecificType(String commonType);

	String createLimitAndOffset(int limit, int offset);

	String createTopAndStart(int limit, int offset);

	boolean isSchemaFilterSupported();

	String getSchemaFilterScript();

	String getAlterAddOpen();

	String getAlterAddOpenEach();

	String getAlterAddClose();

	String getAlterAddCloseEach();

	InputStream getBinaryStream(ResultSet resultSet, String columnName) throws SQLException;

	boolean isCatalogForSchema();

	boolean isSchemaless();

	boolean isDialectForName(String productName);

	/**
	 * Provides a DataSource specific a query string constructed with the catalog, schema and table names arguments.
	 * Generally, for RDB data sources that would most likely be:
	 *
	 * <PRE>
	 * SELECT * FROM <schema-name>.<table-name>
	 * </PRE>
	 *
	 * But for NoSql data sources it could be anything specific to that particular DB's query language. For example,
	 * for MongoDB a generic query to all documents in a collection (which roughly maps to to a RDB Table) is a command
	 * to findAll documents in a collection with no arguments (or empty document) and so the script is either "{}".
	 *
	 * @param catalogName
	 * @param schemaName
	 * @param tableName
	 * @return
	 */
	String getContentQueryScript(String catalogName, String schemaName, String tableName);

}
