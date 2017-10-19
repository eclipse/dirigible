package org.eclipse.dirigible.database.ds.model;

public class DataStructureTableConstraintModel {

	private String name;
	private String[] modifiers;
	private String[] columns;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getModifiers() {
		return modifiers;
	}

	public void setModifiers(String[] modifiers) {
		this.modifiers = modifiers;
	}

	public String[] getColumns() {
		return columns;
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
	}

}
