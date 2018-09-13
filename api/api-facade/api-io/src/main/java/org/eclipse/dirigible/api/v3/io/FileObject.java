package org.eclipse.dirigible.api.v3.io;

public class FileObject {
	
	private String name;
	
	private String path;
	
	private String type;

	public FileObject(String name, String path, String type) {
		super();
		this.name = name;
		this.path = path;
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

}
