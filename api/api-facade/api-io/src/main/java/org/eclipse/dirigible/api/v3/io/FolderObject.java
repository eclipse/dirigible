package org.eclipse.dirigible.api.v3.io;

import java.util.ArrayList;
import java.util.List;

public class FolderObject extends FileObject {
	
	public FolderObject(String name, String path, String type) {
		super(name, path, type);
	}

	private List<FileObject> files = new ArrayList<>();
	
	private List<FolderObject> folders = new ArrayList<>();
	
	public List<FileObject> getFiles() {
		return files;
	}
	
	public List<FolderObject> getFolders() {
		return folders;
	}

}
