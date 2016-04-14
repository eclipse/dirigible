package org.eclipse.dirigible.runtime.chrome.debugger.models;

public class Location implements Comparable<Location>{
	private Double columnNumber;
	private Double lineNumber;
	private String scriptId;

	public Double getColumnNumber() {
		return this.columnNumber;
	}

	public Double getLineNumber() {
		return this.lineNumber;
	}

	public String getScriptId() {
		return this.scriptId;
	}

	public void setColumnNumber(final Double columnNumber) {
		this.columnNumber = columnNumber;
	}

	public void setLineNumber(final Double lineNumber) {
		this.lineNumber = lineNumber;
	}

	public void setScriptId(final String scriptId) {
		this.scriptId = scriptId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((columnNumber == null) ? 0 : columnNumber.hashCode());
		result = prime * result + ((lineNumber == null) ? 0 : lineNumber.hashCode());
		result = prime * result + ((scriptId == null) ? 0 : scriptId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		if (columnNumber == null) {
			if (other.columnNumber != null)
				return false;
		} else if (!columnNumber.equals(other.columnNumber))
			return false;
		if (lineNumber == null) {
			if (other.lineNumber != null)
				return false;
		} else if (!lineNumber.equals(other.lineNumber))
			return false;
		if (scriptId == null) {
			if (other.scriptId != null)
				return false;
		} else if (!scriptId.equals(other.scriptId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Location [columnNumber=" + columnNumber + ", lineNumber=" + lineNumber + ", scriptId=" + scriptId + "]";
	}

	@Override
	public int compareTo(Location otherLocation) {
		int scriptsCompared = scriptId.compareTo(otherLocation.scriptId);
		if (scriptsCompared == 0) {
			int linesCompared = lineNumber.compareTo(otherLocation.lineNumber);
			if (linesCompared == 0) {
				return columnNumber.compareTo(otherLocation.columnNumber);
			} else {
				return linesCompared;
			}
		} else {
			return scriptsCompared;
		}
	}	
}