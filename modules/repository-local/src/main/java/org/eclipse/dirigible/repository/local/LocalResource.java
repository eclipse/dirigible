/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.local;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.util.List;

import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.IResourceVersion;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The DB implementation of {@link IResource}
 */
public class LocalResource extends LocalEntity implements IResource {
	
	private static final Logger logger = LoggerFactory.getLogger(LocalResource.class);
	
	private static final String THERE_IS_NO_RESOURCE_AT_PATH_0 = "There is no resource at path ''{0}''."; //$NON-NLS-1$
	private static final String COULD_NOT_UPDATE_DOCUMENT = "Could not update document."; //$NON-NLS-1$
	private static final String COULD_NOT_READ_RESOURCE_CONTENT = "Could not read resource content."; //$NON-NLS-1$
	private static final String NOT_IMPLEMENTED = "Not implemented"; //$NON-NLS-1$
	private static final String COULD_NOT_DELETE_RESOURCE = "Could not delete resource: "; //$NON-NLS-1$
	private static final String COULD_NOT_RENAME_RESOURCE = "Could not rename resource: "; //$NON-NLS-1$

	private boolean binary = false;

	private String contentType;

	public LocalResource(FileSystemRepository repository, RepositoryPath path) {
		super(repository, path);
		try {
			LocalFile localFile = getDocument();
			if (localFile != null) {
				this.binary = localFile.isBinary();
				this.contentType = localFile.getContentType();

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
		final LocalFile document = getDocumentSafe();
		try {
			document.delete();
		} catch (LocalBaseException ex) {
			throw new IOException(COULD_NOT_DELETE_RESOURCE + this.getName(), ex);
		}
	}

	@Override
	public void renameTo(String name) throws IOException {
		final LocalFile document = getDocumentSafe();
		try {
			document.rename(RepositoryPath.normalizePath(getParent().getPath(), name));
		} catch (LocalBaseException ex) {
			throw new IOException(COULD_NOT_RENAME_RESOURCE + this.getName(), ex);
		}
	}

	@Override
	public void moveTo(String path) throws IOException {
		final LocalFile document = getDocumentSafe();
		try {
			document.rename(path);
		} catch (LocalBaseException ex) {
			throw new IOException(COULD_NOT_RENAME_RESOURCE + this.getName(), ex);
		}
	}

	@Override
	public void copyTo(String path) throws IOException {
		// TODO Auto-generated method stub
		throw new IOException(NOT_IMPLEMENTED);
	}

	@Override
	public boolean exists() throws IOException {
		String repositoryPath = getRepositoryPath().toString();
		String localPath = LocalWorkspaceMapper.getMappedName(getRepository(), repositoryPath);
		return (FileSystemUtils.fileExists(localPath));
		// return (getDocument() != null);
	}

	@Override
	public boolean isEmpty() throws IOException {
		return (getContent().length == 0);
	}

	@Override
	public byte[] getContent() throws IOException {
		final LocalFile document = getDocumentSafe();
		try {
			byte[] bytes = document.getData();
			return bytes;
		} catch (LocalBaseException ex) {
			throw new IOException(COULD_NOT_READ_RESOURCE_CONTENT, ex);
		}
	}

	@Override
	public void setContent(byte[] content) throws IOException {

		if ((this.contentType == null) || "".equals(this.contentType)) { //$NON-NLS-1$
			this.contentType = IResource.CONTENT_TYPE_DEFAULT;
		}

		if (exists()) {
			final LocalFile document = getDocumentSafe();
			try {
				document.setData(content);
			} catch (LocalBaseException ex) {
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
		if (!(obj instanceof LocalResource)) {
			return false;
		}
		final LocalResource other = (LocalResource) obj;
		return getPath().equals(other.getPath());
	}

	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

	/**
	 * Returns the {@link LocalFile} object matching this {@link LocalResource}. If
	 * there is no such object, then <code>null</code> is returned.
	 */
	protected LocalFile getDocument() throws IOException {
		final LocalObject object = getLocalObject();
		if (object == null) {
			return null;
		}
		if (!(object instanceof LocalFile)) {
			return null;
		}
		return (LocalFile) object;
	}

	/**
	 * Returns the {@link LocalFile} object matching this {@link LocalResource}. If
	 * there is no such object, then an {@link IOException} is thrown.
	 */
	protected LocalFile getDocumentSafe() throws IOException {
		final LocalFile document = getDocument();
		if (document == null) {
			throw new IOException(format(THERE_IS_NO_RESOURCE_AT_PATH_0, getPath()));
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
	public void setContent(byte[] content, boolean isBinary, String contentType) throws IOException {

		this.binary = isBinary;
		this.contentType = contentType;

		if (!isBinary) {
			setContent(content);
		}

		if (exists()) {
			final LocalFile document = getDocumentSafe();
			try {
				document.setData(content);
			} catch (LocalBaseException ex) {
				throw new IOException(COULD_NOT_UPDATE_DOCUMENT, ex);
			}
		} else {
			getParent().createResource(getName(), content, binary, contentType);
		}

	}

	@Override
	public List<IResourceVersion> getResourceVersions() throws IOException {
		try {
			return getRepository().getRepositoryDAO().getResourceVersionsByPath(getPath());
		} catch (LocalBaseException ex) {
			logger.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	public IResourceVersion getResourceVersion(int version) throws IOException {
		// return new DBResourceVersion(getRepository(), new RepositoryPath(
		// getPath()), version);
		return null;
	}

}
