package org.eclipse.dirigible.runtime.cmis;

import java.io.IOException;

import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepository;

public class CmisObject {

	private CmisSession session;

	private IEntity internalEntity;

	private boolean typeCollection = false;

	public CmisObject(CmisSession session, String path) throws IOException {
		super();
		this.session = session;
		path = sanitize(path);
		IRepository repository = ((IRepository) session.getCmisRepository().getInternalObject());
		if (repository.hasCollection(path)) {
			this.internalEntity = repository.getCollection(path);
			this.typeCollection = true;
		} else if (repository.hasResource(path)) {
			this.internalEntity = repository.getResource(path);
			this.typeCollection = false;
		}
	}

	protected String sanitize(String path) {
		return path.replace("\\", "");
	}

	public IEntity getInternalEntity() {
		return internalEntity;
	}

	/**
	 * Returns the ID of this CmisObject
	 *
	 * @return
	 */
	public String getId() {
		return this.getInternalEntity().getPath();
	}

	/**
	 * Returns the Name of this CmisObject
	 *
	 * @return
	 */
	public String getName() {
		if ("".equals(this.getInternalEntity().getName())) {
			return "root";
		}
		return this.getInternalEntity().getName();
	}

	/**
	 * Returns the Type of this CmisObject
	 *
	 * @return
	 */
	public ObjectType getType() {
		return this.isCollection() ? ObjectType.FOLDER : ObjectType.DOCUMENT;
	}

	protected boolean isCollection() {
		return typeCollection;
	}

	/**
	 * Returns the Name of this CmisObject
	 *
	 * @return
	 * @throws IOException
	 */
	public void delete() throws IOException {
		this.getInternalEntity().delete();
	}

	/**
	 * Returns the Name of this CmisObject
	 *
	 * @return
	 * @throws IOException
	 */
	public void rename(String newName) throws IOException {
		this.getInternalEntity().renameTo(newName);
	}

}
