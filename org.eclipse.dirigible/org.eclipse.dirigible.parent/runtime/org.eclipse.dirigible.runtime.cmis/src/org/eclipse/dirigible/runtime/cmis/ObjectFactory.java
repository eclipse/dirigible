package org.eclipse.dirigible.runtime.cmis;

import java.io.InputStream;

public class ObjectFactory {

	private CmisSession session;

	public ObjectFactory(CmisSession session) {
		super();
		this.session = session;
	}

	public ContentStream createContentStream(String filename, long length, String mimetype, InputStream inputStream) {
		return new ContentStream(this.session, filename, length, mimetype, inputStream);
	}

}
