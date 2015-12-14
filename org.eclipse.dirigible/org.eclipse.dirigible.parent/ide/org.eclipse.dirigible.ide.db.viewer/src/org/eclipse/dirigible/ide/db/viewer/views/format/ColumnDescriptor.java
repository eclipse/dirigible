package org.eclipse.dirigible.ide.db.viewer.views.format;

public class ColumnDescriptor {

	private String name;
	private String label;
	private int sqlType;
	private int displaySize;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public int getSqlType() {
		return sqlType;
	}
	public void setSqlType(int sqlType) {
		this.sqlType = sqlType;
	}
	public int getDisplaySize() {
		return displaySize;
	}
	public void setDisplaySize(int displaySize) {
		this.displaySize = displaySize;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + displaySize;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + sqlType;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ColumnDescriptor))
			return false;
		ColumnDescriptor other = (ColumnDescriptor) obj;
		if (displaySize != other.displaySize)
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (sqlType != other.sqlType)
			return false;
		return true;
	}

}