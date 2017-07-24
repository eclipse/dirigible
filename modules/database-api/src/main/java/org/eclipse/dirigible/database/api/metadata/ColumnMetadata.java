package org.eclipse.dirigible.database.api.metadata;

public class ColumnMetadata {
	
	private String name;
	
	private String type;
	
	private int size;
	
	private boolean nullable;
	
	private boolean key;

	public ColumnMetadata(String name, String type, int size, boolean nullable, boolean key) {
		super();
		this.name = name;
		this.type = type;
		this.size = size;
		this.nullable = nullable;
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isKey() {
		return key;
	}

	public void setKey(boolean key) {
		this.key = key;
	}
	
	
	
}
