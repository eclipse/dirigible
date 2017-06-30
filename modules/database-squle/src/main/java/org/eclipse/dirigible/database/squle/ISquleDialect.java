package org.eclipse.dirigible.database.squle;

import java.sql.Connection;
import java.sql.SQLException;

public interface ISquleDialect extends ISqule, ISquleKeywords {
	
	/**
	 * Default implementation returns the direct toString() conversion.
	 * It may get overridden for specific database dialects   
	 */
	public String getDataTypeName(DataType dataType);
	
	/**
	 * Primary Key argument for a column for the create table script
	 * Default is "PRIMARY KEY"
	 */
	public String getPrimaryKeyArgument();
	
	/**
	 * Not Null argument for a column for the create table script
	 * Default is "NOT NULL"
	 */
	public String getNotNullArgument();

	/**
	 * Check existence of a table
	 * 
	 * @param connection the current connection
	 * @param table the table name
	 * @return true if the table exists and false otherwise
	 */
	public boolean exists(Connection connection, String table) throws SQLException;
	
	/**
	 * Checks if the database is capable of schema-level filtering statements (e.g. to reduce the provisioned schemas
	 * down to those that the current user is entitled to see).
	 *
	 * @see IDialectSpecifier#getSchemaFilterScript()
	 * @return true if the feature is supported , false otherwise
	 */
	public boolean isSchemaFilterSupported();

	/**
	 * If the database supports schema filtering SQL statements (see {@link #isSchemaFilterSupported()}), this method
	 * provides the
	 * corresponding SQL statement.
	 *
	 * @return a filtering SQL statement
	 */
	public String getSchemaFilterScript();
	
	/**
	 * Does this database support catalogs synonymous to schemas.
	 *
	 * @return whether it is a catalog for schema
	 */
	boolean isCatalogForSchema();
	
}
