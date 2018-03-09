package org.eclipse.dirigible.database.ds.model;

public interface IDataStructureModel {
	
	/** File extension for *.table files */
	public static final String FILE_EXTENSION_TABLE = ".table";
	/** File extension for *.view files */
	public static final String FILE_EXTENSION_VIEW = ".view";
	/** File extension for *.replace files */
	public static final String FILE_EXTENSION_REPLACE = ".replace";
	/** File extension for *.append files */
	public static final String FILE_EXTENSION_APPEND = ".append";
	/** File extension for *.delete files */
	public static final String FILE_EXTENSION_DELETE = ".delete";
	/** File extension for *.update files */
	public static final String FILE_EXTENSION_UPDATE = ".update";
	/** File extension for *.schema files */
	public static final String FILE_EXTENSION_SCHEMA = ".schema";
	
	/** Type table */
	public static final String TYPE_TABLE = "TABLE";
	/** Type view */
	public static final String TYPE_VIEW = "VIEW";
	/** Type replace */
	public static final String TYPE_REPLACE = "REPLACE";
	/** Type append */
	public static final String TYPE_APPEND = "APPEND";
	/** Type delete */
	public static final String TYPE_DELETE = "DELETE";
	/** Type update */
	public static final String TYPE_UPDATE = "UPDATE";
	/** Type schema */
	public static final String TYPE_SCHEMA = "SCHEMA";

}
