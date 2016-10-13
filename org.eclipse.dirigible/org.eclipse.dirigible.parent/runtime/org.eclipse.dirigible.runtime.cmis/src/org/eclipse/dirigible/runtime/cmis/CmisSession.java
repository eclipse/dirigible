package org.eclipse.dirigible.runtime.cmis;

import java.io.IOException;

public class CmisSession {

	private CmisRepository cmisRepository;

	public CmisSession(CmisRepository cmisRepository) {
		super();
		this.cmisRepository = cmisRepository;
	}

	public CmisRepository getCmisRepository() {
		return cmisRepository;
	}

	/**
	 * Returns the information about the CMIS repository
	 *
	 * @return
	 */
	public RepositoryInfo getRepositoryInfo() {
		return new RepositoryInfo(this);
	}

	/**
	 * Returns the ObjectFactory utility
	 *
	 * @return
	 */
	public ObjectFactory getObjectFactory() {
		return new ObjectFactory(this);
	}

	/**
	 * Returns the root folder of this repository
	 *
	 * @return
	 * @throws IOException
	 */
	public Folder getRootFolder() throws IOException {
		return new Folder(this);
	}

	/**
	 * Returns a CMIS Object by name
	 *
	 * @return
	 * @throws IOException
	 */
	public CmisObject getObject(String id) throws IOException {
		CmisObject cmisObject = new CmisObject(this, id);
		if (CmisConstants.OBJECT_TYPE_FOLDER.equals(cmisObject.getType().getId())) {
			return new Folder(this, id);
		} else if (CmisConstants.OBJECT_TYPE_DOCUMENT.equals(cmisObject.getType().getId())) {
			return new Document(this, id);
		}
		return cmisObject;
	}

	/**
	 * Returns a CMIS Object by path
	 *
	 * @return
	 * @throws IOException
	 */
	public CmisObject getObjectByPath(String path) throws IOException {
		return getObject(path);
	}

}
