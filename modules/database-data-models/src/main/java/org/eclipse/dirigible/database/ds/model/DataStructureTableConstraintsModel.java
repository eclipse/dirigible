package org.eclipse.dirigible.database.ds.model;

public class DataStructureTableConstraintsModel {

	private DataStructureTableConstraintPrimaryKeyModel primaryKey;

	private DataStructureTableConstraintForeignKeyModel[] foreignKeys;

	private DataStructureTableConstraintUniqueModel[] uniqueIndices;

	private DataStructureTableConstraintCheckModel[] checks;

	public DataStructureTableConstraintPrimaryKeyModel getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(DataStructureTableConstraintPrimaryKeyModel primaryKey) {
		this.primaryKey = primaryKey;
	}

	public DataStructureTableConstraintForeignKeyModel[] getForeignKeys() {
		return foreignKeys;
	}

	public void setForeignKeys(DataStructureTableConstraintForeignKeyModel[] foreignKeys) {
		this.foreignKeys = foreignKeys;
	}

	public DataStructureTableConstraintUniqueModel[] getUniqueIndices() {
		return uniqueIndices;
	}

	public void setUniqueIndices(DataStructureTableConstraintUniqueModel[] uniqueIndices) {
		this.uniqueIndices = uniqueIndices;
	}

	public DataStructureTableConstraintCheckModel[] getChecks() {
		return checks;
	}

	public void setChecks(DataStructureTableConstraintCheckModel[] checks) {
		this.checks = checks;
	}

}
