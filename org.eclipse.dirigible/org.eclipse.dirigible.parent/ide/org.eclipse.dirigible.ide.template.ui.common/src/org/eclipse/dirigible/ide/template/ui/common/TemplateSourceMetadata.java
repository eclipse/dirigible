package org.eclipse.dirigible.ide.template.ui.common;

public class TemplateSourceMetadata {

	public static final String ACTION_GENERATE = "generate";

	public static final String ACTION_COPY = "copy";

	private String name;
	private String rootFolder;
	private String packagePath;
	private String action;
	private String rename;

	private String location;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRootFolder() {
		return rootFolder;
	}

	public void setRootFolder(String rootFolder) {
		this.rootFolder = rootFolder;
	}

	public String getPackagePath() {
		return packagePath;
	}

	public void setPackagePath(String packagePath) {
		this.packagePath = packagePath;
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
