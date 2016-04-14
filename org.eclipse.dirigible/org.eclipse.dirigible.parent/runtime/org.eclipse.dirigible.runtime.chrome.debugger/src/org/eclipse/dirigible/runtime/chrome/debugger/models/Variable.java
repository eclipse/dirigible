package org.eclipse.dirigible.runtime.chrome.debugger.models;

import java.util.List;

public class Variable {

	private Boolean configurable = true;
	private Boolean enumerable = false;
	private Boolean isOwn = true;
	private String name;
	private Value value;
	private Boolean writable = true;

	public Boolean getConfigurable() {
		return this.configurable;
	}

	public void setConfigurable(final Boolean configurable) {
		this.configurable = configurable;
	}

	public Boolean getEnumerable() {
		return this.enumerable;
	}

	public void setEnumerable(final Boolean enumerable) {
		this.enumerable = enumerable;
	}

	public Boolean getIsOwn() {
		return this.isOwn;
	}

	public void setIsOwn(final Boolean isOwn) {
		this.isOwn = isOwn;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Value getValue() {
		return this.value;
	}

	public void setValue(final Value value) {
		this.value = value;
	}

	public Boolean getWritable() {
		return this.writable;
	}

	public void setWritable(final Boolean writable) {
		this.writable = writable;
	}

	public static class Value {
		private String className;
		private String description;
		private String objectId;
		private Preview preview;
		private String subtype;
		private String type;

		public String getClassName() {
			return this.className;
		}

		public void setClassName(final String className) {
			this.className = className;
		}

		public String getDescription() {
			return this.description;
		}

		public void setDescription(final String description) {
			this.description = description;
		}

		public String getObjectId() {
			return this.objectId;
		}

		public void setObjectId(final String objectId) {
			this.objectId = objectId;
		}

		public Preview getPreview() {
			return this.preview;
		}

		public void setPreview(final Preview preview) {
			this.preview = preview;
		}

		public String getSubtype() {
			return this.subtype;
		}

		public void setSubtype(final String subtype) {
			this.subtype = subtype;
		}

		public String getType() {
			return this.type;
		}

		public void setType(final String type) {
			this.type = type;
		}
	}

	public static class Preview {
		private String description;
		private Boolean lossless;
		private Boolean overflow;
		private List<Property> properties;
		private String subtype;
		private String type;

		public String getDescription() {
			return this.description;
		}

		public void setDescription(final String description) {
			this.description = description;
		}

		public Boolean getLossless() {
			return this.lossless;
		}

		public void setLossless(final Boolean lossless) {
			this.lossless = lossless;
		}

		public Boolean getOverflow() {
			return this.overflow;
		}

		public void setOverflow(final Boolean overflow) {
			this.overflow = overflow;
		}

		public List<Property> getProperties() {
			return this.properties;
		}

		public void setProperties(final List<Property> properties) {
			this.properties = properties;
		}

		public String getSubtype() {
			return this.subtype;
		}

		public void setSubtype(final String subtype) {
			this.subtype = subtype;
		}

		public String getType() {
			return this.type;
		}

		public void setType(final String type) {
			this.type = type;
		}
	}

	public static class Property {
		private String name;
		private String type;
		private String value;

		public String getName() {
			return this.name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public String getType() {
			return this.type;
		}

		public void setType(final String type) {
			this.type = type;
		}

		public String getValue() {
			return this.value;
		}

		public void setValue(final String value) {
			this.value = value;
		}
	}
}
