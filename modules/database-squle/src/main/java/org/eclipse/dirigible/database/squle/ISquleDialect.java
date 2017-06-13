package org.eclipse.dirigible.database.squle;

public interface ISquleDialect extends ISquleKeywords {
	
	/**
	 * Default implementation returns the direct toString() conversion.
	 * It may get overridden for specific database dialects   
	 */
	public String getDataTypeName(DataType dataType);
	
	/**
	 * Primary key argument for a column for the create table script
	 * Default is "PRIMARY KEY"
	 */
	public String getPrimaryKeyArgument();

}
