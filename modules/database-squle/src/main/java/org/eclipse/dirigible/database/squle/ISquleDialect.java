package org.eclipse.dirigible.database.squle;

import java.sql.Connection;
import java.sql.SQLException;

public interface ISquleDialect extends ISquleKeywords {
	
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
}
