package org.eclipse.dirigible.database.persistence.model;

import java.util.ArrayList;
import java.util.List;

public class PersistenceTableModel {
	
	private String className;
	
	private String tableName;
	
	private String schemaName;
	
	private List<PersistenceTableColumnModel> columns = new ArrayList<PersistenceTableColumnModel>();

	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public List<PersistenceTableColumnModel> getColumns() {
		return columns;
	}

	public void setColumns(List<PersistenceTableColumnModel> columns) {
		this.columns = columns;
	}
	
}
