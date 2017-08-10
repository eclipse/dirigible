package org.eclipse.dirigible.core.workspace.json;

import java.util.ArrayList;
import java.util.List;

public class Project {

	private String name;

	private String path;

	private String type = "project";

	private List<Folder> folders = new ArrayList<Folder>();

	private List<File> files = new ArrayList<File>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<Folder> getFolders() {
		return folders;
	}

	public void set(List<Folder> folders) {
		this.folders = folders;
	}

	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}

}
