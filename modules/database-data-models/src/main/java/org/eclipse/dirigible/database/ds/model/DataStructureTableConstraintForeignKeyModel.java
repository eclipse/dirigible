package org.eclipse.dirigible.database.ds.model;

public class DataStructureTableConstraintForeignKeyModel extends DataStructureTableConstraintModel {

	private String referencedTable;
	private String[] referencedColumns;

	public String getReferencedTable() {
		return referencedTable;
	}

	public void setReferencedTable(String referencedTable) {
		this.referencedTable = referencedTable;
	}

	public String[] getReferencedColumns() {
		return referencedColumns;
	}

	public void setReferencedColumns(String[] referencedColumns) {
		this.referencedColumns = referencedColumns;
	}

}
