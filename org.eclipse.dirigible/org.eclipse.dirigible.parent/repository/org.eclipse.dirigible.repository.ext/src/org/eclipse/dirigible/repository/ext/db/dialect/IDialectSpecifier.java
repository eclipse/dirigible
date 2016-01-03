/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.ext.db.dialect;

import java.io.InputStream;
import java.sql.DatabaseMetaData;
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

	/**
	 * Factory class for IDialectInstances.
	 */
	public class Factory {

		public static final String PRODUCT_DERBY = "Apache Derby"; //$NON-NLS-1$
		public static final String PRODUCT_SYBASE = "Adaptive Server Enterprise"; //$NON-NLS-1$
		public static final String PRODUCT_SAP_DB = "SAP DB"; //$NON-NLS-1$
		public static final String PRODUCT_HDB = "HDB"; //$NON-NLS-1$
		public static final String PRODUCT_POSTGRESQL = "PostgreSQL"; //$NON-NLS-1$
		public static final String PRODUCT_MYSQL = "MySQL"; //$NON-NLS-1$
		public static final String PRODUCT_MONGODB = "MongoDB"; //$NON-NLS-1$

		/**
		 * Returns new instance of a dialect specifier corresponding to the provided productName parameter.
		 * If no match was found, a default {@link DerbyDBSpecifier} instance is returned.
		 * The productName is matched against one of the constants defined in this class.
		 * They are the same as the value returned from JDBC API’s {@link DatabaseMetaData#getDatabaseProductName()}
		 * method
		 * for the corrseponding database.
		 *
		 * @param productName
		 *            A database product name matching one of the declared constants in the class or null/random for
		 *            default dialect
		 * @return an implementation of {@link IDialectSpecifier} for the given productName parameter or default dialect
		 *         if none
		 *         matched the given productName
		 */
		public static IDialectSpecifier getInstance(String productName) {
			if (productName != null) {
				if (PRODUCT_HDB.equalsIgnoreCase(productName)) {
					return new HANADBSpecifier();
				} else if (PRODUCT_SAP_DB.equalsIgnoreCase(productName)) {
					return new SAPDBSpecifier();
				} else if (PRODUCT_SYBASE.equalsIgnoreCase(productName)) {
					return new SybaseDBSpecifier();
				} else if (PRODUCT_DERBY.equalsIgnoreCase(productName)) {
					return new DerbyDBSpecifier();
				} else if (PRODUCT_POSTGRESQL.equalsIgnoreCase(productName)) {
					return new PostgreSQLDBSpecifier();
				} else if (PRODUCT_MYSQL.equalsIgnoreCase(productName)) {
					return new MySQLDBSpecifier();
				} else if (PRODUCT_MONGODB.equalsIgnoreCase(productName)) {
					return new MongoDBSpecifier();
				}
			}
			return new DerbyDBSpecifier();
		}

	}

}
