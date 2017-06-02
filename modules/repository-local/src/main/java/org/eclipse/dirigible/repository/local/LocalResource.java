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

import java.util.List;

import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.IResourceVersion;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.api.RepositoryReadException;
import org.eclipse.dirigible.repository.api.RepositoryVersioningException;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.eclipse.dirigible.repository.fs.FileSystemRepository;
import org.eclipse.dirigible.repository.fs.FileSystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The DB implementation of {@link IResource}
 */
public class LocalResource extends LocalEntity implements IResource {
	
	private static final Logger logger = LoggerFactory.getLogger(LocalResource.class);
	
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
		} catch (RepositoryReadException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void create() throws RepositoryWriteException {
		getParent().createResource(getName(), null, false, CONTENT_TYPE_DEFAULT);
	}

	@Override
	public void delete() throws RepositoryWriteException {
		final LocalFile document = getDocumentSafe();
		try {
			document.delete();
		} catch (LocalRepositoryException ex) {
			throw new RepositoryWriteException(format("Could not delete resource {0} ", this.getName()), ex);
		}
	}

	@Override
	public void renameTo(String name) throws RepositoryWriteException {
		final LocalFile document = getDocumentSafe();
		try {
			document.rename(RepositoryPath.normalizePath(getParent().getPath(), name));
		} catch (LocalRepositoryException ex) {
			throw new RepositoryWriteException(format("Could not rename resource {0}", this.getName()), ex);
		}
	}

	@Override
	public void moveTo(String path) throws RepositoryWriteException {
		final LocalFile document = getDocumentSafe();
		try {
			document.rename(path);
		} catch (LocalRepositoryException ex) {
			throw new RepositoryWriteException(format("Could not move resource {0}", this.getName()), ex);
		}
	}

	@Override
	public void copyTo(String path) throws RepositoryWriteException {
		// TODO Auto-generated method stub
		throw new RepositoryWriteException("Not implemented");
	}

	@Override
	public boolean exists() throws RepositoryReadException {
		String repositoryPath = getRepositoryPath().toString();
		String localPath = LocalWorkspaceMapper.getMappedName(getRepository(), repositoryPath);
		return (FileSystemUtils.fileExists(localPath));
	}

	@Override
	public boolean isEmpty() throws RepositoryReadException {
		return (getContent().length == 0);
	}

	@Override
	public byte[] getContent() throws RepositoryReadException {
		final LocalFile document = getDocumentSafe();
		try {
			byte[] bytes = document.getData();
			return bytes;
		} catch (LocalRepositoryException ex) {
			throw new RepositoryReadException("Could not read resource content.", ex);
		}
	}

	@Override
	public void setContent(byte[] content) throws RepositoryWriteException {

		if ((this.contentType == null) || "".equals(this.contentType)) { //$NON-NLS-1$
			this.contentType = IResource.CONTENT_TYPE_DEFAULT;
		}

		if (exists()) {
			final LocalFile document = getDocumentSafe();
			try {
				document.setData(content);
			} catch (LocalRepositoryException ex) {
				throw new RepositoryWriteException("Could not update document.", ex);
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
	protected LocalFile getDocument() throws RepositoryReadException {
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
	 * there is no such object, then an {@link RepositoryReadException} is thrown.
	 */
	protected LocalFile getDocumentSafe() throws RepositoryReadException {
		final LocalFile document = getDocument();
		if (document == null) {
			throw new RepositoryReadException(format("There is no resource at path ''{0}''.", getPath()));
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
	public void setContent(byte[] content, boolean isBinary, String contentType) throws RepositoryWriteException {

		this.binary = isBinary;
		this.contentType = contentType;

		if (!isBinary) {
			setContent(content);
		}

		if (exists()) {
			final LocalFile document = getDocumentSafe();
			try {
				document.setData(content);
			} catch (LocalRepositoryException ex) {
				throw new RepositoryWriteException("Could not update document.", ex);
			}
		} else {
			getParent().createResource(getName(), content, binary, contentType);
		}

	}

	@Override
	public List<IResourceVersion> getResourceVersions() throws RepositoryVersioningException {
		try {
			return getRepository().getRepositoryDAO().getResourceVersionsByPath(getPath());
		} catch (LocalRepositoryException ex) {
			logger.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	public IResourceVersion getResourceVersion(int version) throws RepositoryVersioningException {
		// return new DBResourceVersion(getRepository(), new RepositoryPath(
		// getPath()), version);
		return null;
	}

}
