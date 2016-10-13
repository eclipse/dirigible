package org.eclipse.dirigible.runtime.cmis;

public class ObjectType {

	private String type;

	public ObjectType(String type) {
		this.type = type;
	}

	public String getId() {
		return this.type;
	}

	public static final ObjectType FOLDER = new ObjectType(CmisConstants.OBJECT_TYPE_FOLDER);

	public static final ObjectType DOCUMENT = new ObjectType(CmisConstants.OBJECT_TYPE_DOCUMENT);

}
