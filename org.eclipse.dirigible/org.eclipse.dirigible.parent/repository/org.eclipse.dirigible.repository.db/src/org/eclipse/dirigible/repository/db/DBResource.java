/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.db;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.util.List;

import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.IResourceVersion;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.db.dao.DBFile;
import org.eclipse.dirigible.repository.db.dao.DBObject;
import org.eclipse.dirigible.repository.logging.Logger;

/**
 * The DB implementation of {@link IResource}
 * 
 */
public class DBResource extends DBEntity implements IResource {

	private static final String THERE_IS_NO_RESOURCE_AT_PATH_0 = Messages.getString("DBResource.THERE_IS_NO_RESOURCE_AT_PATH_0"); //$NON-NLS-1$
	private static final String COULD_NOT_UPDATE_DOCUMENT = Messages.getString("DBResource.COULD_NOT_UPDATE_DOCUMENT"); //$NON-NLS-1$
	private static final String COULD_NOT_READ_RESOURCE_CONTENT = Messages.getString("DBResource.COULD_NOT_READ_RESOURCE_CONTENT"); //$NON-NLS-1$
	private static final String NOT_IMPLEMENTED = Messages.getString("DBResource.NOT_IMPLEMENTED"); //$NON-NLS-1$
	private static final String COULD_NOT_DELETE_RESOURCE = Messages.getString("DBResource.COULD_NOT_DELETE_RESOURCE"); //$NON-NLS-1$
	private static final String COULD_NOT_RENAME_RESOURCE = Messages.getString("DBResource.COULD_NOT_RENAME_RESOURCE"); //$NON-NLS-1$

	private static final Logger logger = Logger.getLogger(DBResource.class);

	private boolean binary = false;

	private String contentType;

	public DBResource(DBRepository repository, RepositoryPath path) {
		super(repository, path);
		try {
			DBFile dbFile = getDocument();
			if (dbFile != null) {
				this.binary = dbFile.isBinary();
				this.contentType = dbFile.getContentType();

			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void create() throws IOException {
		getParent().createResource(getName(), null, false, CONTENT_TYPE_DEFAULT);
	}

	@Override
	public void delete() throws IOException {
		final DBFile document = getDocumentSafe();
		try {
			document.delete();
		} catch (DBBaseException ex) {
			throw new IOException(COULD_NOT_DELETE_RESOURCE + this.getName(), ex);
		}
	}

	@Override
	public void renameTo(String name) throws IOException {
		 final DBFile document = getDocumentSafe();
		 try {
			 document.rename(RepositoryPath.normalizePath(getParent().getPath(), name));
		 } catch (DBBaseException ex) {
			 throw new IOException(COULD_NOT_RENAME_RESOURCE + this.getName(),
						ex);
		 }
	}

	@Override
	public void moveTo(String path) throws IOException {
		final DBFile document = getDocumentSafe();
		 try {
			 document.rename(path);
		 } catch (DBBaseException ex) {
			 throw new IOException(COULD_NOT_RENAME_RESOURCE + this.getName(),
						ex);
		 }
	}

	@Override
	public void copyTo(String path) throws IOException {
		// TODO Auto-generated method stub
		throw new IOException(NOT_IMPLEMENTED);
	}

	@Override
	public boolean exists() throws IOException {
		return (getDocument() != null);
	}

	@Override
	public boolean isEmpty() throws IOException {
		return (getContent().length == 0);
	}

	@Override
	public byte[] getContent() throws IOException {
		final DBFile document = getDocumentSafe();
		try {
			byte[] bytes = document.getData();
			return bytes;
		} catch (DBBaseException ex) {
			throw new IOException(COULD_NOT_READ_RESOURCE_CONTENT, ex);
		}
	}

	@Override
	public void setContent(byte[] content) throws IOException {

		if (this.contentType == null || "".equals(this.contentType)) { //$NON-NLS-1$
			this.contentType = IResource.CONTENT_TYPE_DEFAULT;
		}

		if (exists()) {
			final DBFile document = getDocumentSafe();
			try {
				document.setData(content);
			} catch (DBBaseException ex) {
				throw new IOException(COULD_NOT_UPDATE_DOCUMENT, ex);
			}
		} else {
			getParent().createResource(getName(), content, this.binary, this.contentType);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof DBResource)) {
			return false;
		}
		final DBResource other = (DBResource) obj;
		return getPath().equals(other.getPath());
	}

	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

	/**
	 * Returns the {@link DBFile} object matching this {@link DBResource}. If
	 * there is no such object, then <code>null</code> is returned.
	 */
	protected DBFile getDocument() throws IOException {
		final DBObject object = getDBObject();
		if (object == null) {
			return null;
		}
		if (!(object instanceof DBFile)) {
			return null;
		}
		return (DBFile) object;
	}

	/**
	 * Returns the {@link DBFile} object matching this {@link DBResource}. If
	 * there is no such object, then an {@link IOException} is thrown.
	 */
	protected DBFile getDocumentSafe() throws IOException {
		final DBFile document = getDocument();
		if (document == null) {
			throw new IOException(format(THERE_IS_NO_RESOURCE_AT_PATH_0,
					getPath()));
		}
		return document;
	}

	@Override
	public boolean isBinary() {
		return binary;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public void setContent(byte[] content, boolean isBinary, String contentType)
			throws IOException {

		this.binary = isBinary;
		this.contentType = contentType;

		if (!isBinary) {
			setContent(content);
		}

		if (exists()) {
			final DBFile document = getDocumentSafe();
			try {
				document.setData(content);
			} catch (DBBaseException ex) {
				throw new IOException(COULD_NOT_UPDATE_DOCUMENT, ex);
			}
		} else {
			getParent().createResource(getName(), content, binary, contentType);
		}

	}

	@Override
	public List<IResourceVersion> getResourceVersions() throws IOException {
		try {
			return getRepository().getRepositoryDAO()
					.getResourceVersionsByPath(getPath());
		} catch (DBBaseException ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public IResourceVersion getResourceVersion(int version) throws IOException {
		return new DBResourceVersion(getRepository(), new RepositoryPath(
				getPath()), version);
	}

}
