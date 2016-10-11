package org.eclipse.dirigible.runtime.cmis;

public class RepositoryInfo {

	private CmisSession session;

	public RepositoryInfo(CmisSession session) {
		super();
		this.session = session;
	}

	/**
	 * Returns the ID of the CMIS repository
	 *
	 * @return
	 */
	public String getId() {
		return this.session.getCmisRepository().getInternalObject().getClass().getCanonicalName();
	}

	/**
	 * Returns the Name of the CMIS repository
	 *
	 * @return
	 */
	public String getName() {
		return this.session.getCmisRepository().getInternalObject().getClass().getCanonicalName();
	}

}
