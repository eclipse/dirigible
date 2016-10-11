package org.eclipse.dirigible.runtime.cmis;

public class ObjectFactory {

	private CmisSession session;

	public ObjectFactory(CmisSession session) {
		super();
		this.session = session;
	}

	/**
	 * Returns a newly created ContentStream object
	 */
	public ContentStream createContentStream() {
		return new ContentStream(this);
	}

}
