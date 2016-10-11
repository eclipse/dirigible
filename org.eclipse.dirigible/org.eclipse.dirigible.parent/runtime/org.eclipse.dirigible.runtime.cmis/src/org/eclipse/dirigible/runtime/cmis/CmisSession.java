package org.eclipse.dirigible.runtime.cmis;

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
	 */
	public Folder getRootFolder() {
		return new Folder(this);
	}

	/**
	 * Returns a CMIS Object by name
	 *
	 * @return
	 */
	public CmisObject getObject(String id) {
		return new CmisObject(this);
	}

	/**
	 * Returns a CMIS Object by path
	 *
	 * @return
	 */
	public CmisObject getObjectByPath(String path) {
		return new CmisObject(this);
	}

}
