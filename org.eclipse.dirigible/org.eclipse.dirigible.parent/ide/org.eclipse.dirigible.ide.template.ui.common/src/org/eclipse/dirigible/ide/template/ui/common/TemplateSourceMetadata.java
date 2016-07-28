package org.eclipse.dirigible.ide.template.ui.common;

public class TemplateSourceMetadata {

	public static final String ACTION_GENERATE = "generate";

	public static final String ACTION_COPY = "copy";

	private String name;

	private String action;

	private String rename;

	private String location;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getRename() {
		return rename;
	}

	public void setRename(String rename) {
		this.rename = rename;
	}

	public boolean isCopy() {
		return ACTION_COPY.equalsIgnoreCase(this.action);
	}

	public boolean isGenerate() {
		return ACTION_GENERATE.equalsIgnoreCase(this.action);
	}

	public boolean isRenaming() {
		return (this.rename != null) && !"".equals(this.rename);
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
