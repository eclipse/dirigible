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
import java.sql.Types;

import org.eclipse.dirigible.repository.datasource.DBSupportedTypesMap;

/**
 * The interface defines the contract between Dirigible and different databases with regards to 
 * SQL variants (dialects) and database characteristics (e.g. NoSQL vs. Relational)
 *
 */
public interface IDialectSpecifier {

	/**
	 * Variable for a dialect specific {@link Types#TIMESTAMP} type.
	 * Used in Dirigible for database agnostic SQL statements that are tailored at runtime to specific dialects.
	 */
	public static final String DIALECT_TIMESTAMP = "$TIMESTAMP$"; //$NON-NLS-1$
	
	/**
	 * Variable for a dialect specific {@link Types#BLOB} type.
	 * Used in Dirigible for database agnostic SQL statements that are tailored at runtime to specific dialects.
	 */
	public static final String DIALECT_BLOB = "$BLOB$"; //$NON-NLS-1$
	
	/**
	 * Variable for a dialect specific {@link Types#CLOB} type.
	 * Used in Dirigible for database agnostic SQL statements that are tailored at runtime to specific dialects.
	 */
	public static final String DIALECT_CLOB = "$CLOB$"; //$NON-NLS-1$
	
	/**
	 * Variable for a function name returning current timestamp.
	 * Used in Dirigible for database agnostic SQL statements that are tailored at runtime to specific dialects.
	 */
	public static final String DIALECT_CURRENT_TIMESTAMP = "$CURRENT_TIMESTAMP$"; //$NON-NLS-1$

	/**
	 * Variable for character sequence type, with sufficient size (1000) used for primary key columns.
	 * Used in Dirigible system database scripts for database agnostic SQL statements that are tailored at runtime to specific dialects. 
	 */
	public static final String DIALECT_KEY_VARCHAR = "$KEY_VARCHAR$"; //$NON-NLS-1$
	
	/**
	 * Variable for the largest variable character sequence type that is not {@link Types#CLOB}.
	 * Used in Dirigible system database scripts for database agnostic SQL statements that are tailored at runtime to specific dialects.
	 */
	public static final String DIALECT_BIG_VARCHAR = "$BIG_VARCHAR$"; //$NON-NLS-1$

	/**
	 * Implementations must take care to replace the potential variables in the input <CODE>sql</CODE> string
	 * with concrete values that are valid for the database that the dialect is designed for.
	 * The possible variables are the constants defined in this interface:
	 * <UL>
	 * 	<LI> {@link IDialectSpecifier#DIALECT_TIMESTAMP}
	 * 	<LI> {@link IDialectSpecifier#DIALECT_BLOB}
	 * 	<LI> {@link IDialectSpecifier#DIALECT_CLOB}
	 * 	<LI> {@link IDialectSpecifier#DIALECT_CURRENT_TIMESTAMP}
	 * 	<LI> {@link IDialectSpecifier#DIALECT_KEY_VARCHAR}
	 * 	<LI> {@link IDialectSpecifier#DIALECT_BIG_VARCHAR}
	 * </UL>
	 * 
	 * A simple reference implementation would be something like this:<BR>
	 * <CODE>
	 * return sql.replace(IDialectSpecifier.DIALECT_TIMESTAMP, "TIMESTAMP").replace(IDialectSpecifier.DIALECT_BLOB, "BLOB").replace(... </CODE> and so on
	 *
	 * @param sql an input SQL statement string potentially containing variables to be replaced 
	 * @return a processed SQL statement string with variables replaced by dialect-specific values
	 */
	String specify(String sql);

	/**
	 * The method returns the most suitable dialect-specific SQL type that corresponds to the provided <CODE>commonType</CODE> parameter.
	 * Different mechanisms in Dirigible make use of this method to translate the common data types they use to dialect specific ones.
	 *   
	 * @param commonType one of the types returned by {@link DBSupportedTypesMap#getSupportedTypes()} (VARCHAR, CHAR, INTEGER, BIGINT, SMALLINT, REAL, DOUBLE, DATE, TIME, TIMESTAMP, BLOB)
	 * @return The database-specific SQL type that corresponds to the commonType parameter, if any, or the provided commonType if it's the same.   
	 */
	String getSpecificType(String commonType);

	/**
	 * Provides a dialect specific <CODE>LIMIT</CODE> and/or <CODE>OFFSET</CODE> syntactic construction that is appended to an SQL query.
	 * Normally, this is alternative to {@link #createTopAndStart(int, int)}.
	 *   
	 * @param limit a limit parameter to insert in the correct place in the <CODE>LIMIT</CODE> syntactic construct part of an SQL statement
	 * @param offset an offset parameter to insert in the correct place in the <CODE>OFFSET</CODE> syntactic construct part of an SQL statement
	 * @return The <CODE>LIMIT</CODE> and <CODE>OFFSET</CODE> part of an SQL statement statement
	 */
	String createLimitAndOffset(int limit, int offset);

	/**
	 * Provides a dialect specific <CODE>TOP</CODE> and/or <CODE>START</CODE> syntactic construction that is inserted in an SQL query.
	 * Normally, this is alternative to {@link #createLimitAndOffset(int, int)}. 
	 * 
	 * @param limit a limit parameter to insert in the correct place in the <CODE>TOP</CODE> syntactic construct part of an SQL statement
	 * @param offset an offset parameter to insert in the correct place in the <CODE>START</CODE> syntactic construct part of an SQL statement
	 * @return The <CODE>TOP</CODE> and <CODE>START</CODE> part of an SQL statement statement
	 */
	String createTopAndStart(int limit, int offset);

	/**
	 * Checks if the database is capable of schema-level filtering statements (e.g. to reduce the provisioned schemas
	 * down to those that the current user is entitled to see). 
	 * @see IDialectSpecifier#getSchemaFilterScript();
	 * @return true if the feature is supported , false otherwise
	 */
	boolean isSchemaFilterSupported();

	/**
	 * If the database supports schema filtering SQL statements (see {@link #isSchemaFilterSupported()}), this method provides the 
	 * corresponding SQL statement.
	 * 
	 * @return a filtering SQL statement 
	 */
	String getSchemaFilterScript();

	/**
	 * Provides the opening syntactic construct for an <CODE>ALTER TABLE &lt;table-name&gt; ADD</CODE> statement. 
	 * Database dialects vary with some requiring an opening bracket after <CODE>ADD</CODE> and others not. 
	 * This method will return the correct <CODE>ADD</CODE> start (including the <CODE>ADD</CODE>).
	 * Common implementations would be: <CODE>return " ADD ";</CODE> and <CODE>return " ADD(";</CODE>. 
	 *   
	 * @return the opening syntactic construct for an <CODE>ALTER TABLE &lt;table-name&gt; ADD</CODE> statement
	 */
	String getAlterAddOpen();

	/**
	 * Provides the closing syntactic construct for <CODE>ALTER TABLE &lt;table-name&gt; ADD</CODE> statements.
	 * @see #getAlterAddOpen()  
	 * @return the closing syntactic construct for <CODE>ALTER TABLE &lt;table-name&gt; ADD</CODE> statements
	 */
	String getAlterAddClose();

	/**
	 * Similar to {@link #getAlterAddOpen()}, this method provides the opening syntactic construct for 
	 * ALTER TABLE statements, but in cases when the database dialect requires separate ADD statements 
	 * for each column to be added. See <a href="http://www.postgresql.org/docs/9.1/static/sql-altertable.html">
	 * PostgreSQL documentation</a> for example.
	 * A common implementation would be <CODE>return " ADD COLUMN ";</CODE>
	 *   
	 * @return the opening syntactic construct for <CODE>ALTER TABLE</CODE>
	 */
	String getAlterAddOpenEach();

	/**
	 * Provides the closing syntactic construct for <CODE>ALTER TABLE &lt;table-name&gt; ADD COLUMN</CODE>.
	 * @see #getAlterAddOpenEach()
	 * @return the closing syntactic construct for <CODE>ALTER TABLE &lt;table-name&gt; ADD COLUMN</CODE>
	 */
	String getAlterAddCloseEach();

	/**
	 * Implements a database specific JDBC code to get an InputStream from a binary type column (such as <CODE>BLOB</CODE>).
	 * 
	 * @param resultSet
	 * @param columnName
	 * @return a stream to read the contents of the column identified by the <CODE>columnName</CODE> parameter in the supplied <CODE>resultSet</CODE>
	 * @throws SQLException
	 */
	InputStream getBinaryStream(ResultSet resultSet, String columnName) throws SQLException;

	/**
	 * Does this database support catalogs synonymous to schemas.
	 * @return
	 */
	boolean isCatalogForSchema();

	/**
	 * Is this a dialect for a NoSQL or Relational database.
	 * @return true if it is a dialect for NoSQL database, false otherwise
	 */
	boolean isSchemaless();

	/**
	 * Assesses if this dialect is applicable to databases with the supplied <CODE>productName</CODE>.
	 * The <CODE>productName</CODE> parameter argument is the string provided by the database JDBC driver's 
	 * {@link DatabaseMetaData#getDatabaseProductName()} method.
	 *  
	 * @param productName the string provided by the database JDBC driver's {@link DatabaseMetaData#getDatabaseProductName()} method
	 * @return true if this dialect is applicable for databases with the supplied <CODE>productName</CODE> or flase otherwise.
	 */
	boolean isDialectForName(String productName);

	/**
	 * Provides a DataSource specific a query string constructed with the catalog, schema and table names arguments.
	 * Generally, for RDB data sources that would most likely be: <CODE>SELECT * FROM &lt;schema-name&gt;.&lt;table-name&gt;</CODE>.
	 * But for NoSQL data sources it could be anything specific to that particular database query language. For example,
	 * for MongoDB a generic query to all documents in a collection (which roughly maps to a RDB Table) is a command to
	 * findAll documents in a collection with no arguments (or empty document) and the script is <CODE>"{}"</CODE>.
	 *
	 * @param catalogName
	 * @param schemaName
	 * @param tableName
	 * @return an SQL statement that retrieves table contents 
	 */
	String getContentQueryScript(String catalogName, String schemaName, String tableName);

}
