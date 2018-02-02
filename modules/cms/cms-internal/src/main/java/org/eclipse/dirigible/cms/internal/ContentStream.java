package org.eclipse.dirigible.cms.internal;

import java.io.InputStream;

public class ContentStream {

	private CmisSession cmisSession;

	private String filename;

	private long length;

	private String mimetype;

	private InputStream inputStream;

	public ContentStream(CmisSession cmisSession, String filename, long length, String mimetype, InputStream inputStream) {
		super();
		this.cmisSession = cmisSession;
		this.filename = filename;
		this.length = length;
		this.mimetype = mimetype;
		this.inputStream = inputStream;
	}

	/**
	 * Returns the InputStream of this ContentStream object
	 *
	 * @return Input Stream
	 */
	public InputStream getStream() {
		return this.inputStream;
	}

	public CmisSession getCmisSession() {
		return cmisSession;
	}

	public void setCmisSession(CmisSession cmisSession) {
		this.cmisSession = cmisSession;
	}

	public String getFilename() {
		return filename;
	}

	public long getLength() {
		return length;
	}

	public String getMimeType() {
		return mimetype;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

}
