/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.repository.db;

import static java.text.MessageFormat.format;

import java.sql.SQLException;
import java.util.List;

import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.IResourceVersion;
import org.eclipse.dirigible.repository.api.RepositoryNotFoundException;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.api.RepositoryReadException;
import org.eclipse.dirigible.repository.api.RepositoryVersioningException;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The file system based implementation of {@link IResource}.
 */
public class DatabaseResource extends DatabaseEntity implements IResource {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseResource.class);

	private boolean binary = false;

	private String contentType;

	/**
	 * Instantiates a new local resource.
	 *
	 * @param repository
	 *            the repository
	 * @param path
	 *            the path
	 */
	public DatabaseResource(DatabaseRepository repository, RepositoryPath path) {
		super(repository, path);
		try {
			DatabaseFile localFile = getDocument();
			if (localFile != null) {
				this.binary = localFile.isBinary();
				this.contentType = localFile.getContentType();

			}
		} catch (RepositoryReadException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#create()
	 */
	@Override
	public void create() throws RepositoryWriteException {
		getParent().createResource(getName(), null, false, CONTENT_TYPE_DEFAULT);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#delete()
	 */
	@Override
	public void delete() throws RepositoryWriteException {
		final DatabaseFile document = getDocumentSafe();
		try {
			document.delete();
		} catch (DatabaseRepositoryException ex) {
			throw new RepositoryWriteException(format("Could not delete resource {0} ", this.getName()), ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#renameTo(java.lang.String)
	 */
	@Override
	public void renameTo(String name) throws RepositoryWriteException {
		final DatabaseFile document = getDocumentSafe();
		try {
			document.rename(RepositoryPath.normalizePath(getParent().getPath(), name));
		} catch (DatabaseRepositoryException ex) {
			throw new RepositoryWriteException(format("Could not rename resource {0}", this.getName()), ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#moveTo(java.lang.String)
	 */
	@Override
	public void moveTo(String path) throws RepositoryWriteException {
		final DatabaseFile document = getDocumentSafe();
		try {
			document.rename(path);
		} catch (DatabaseRepositoryException ex) {
			throw new RepositoryWriteException(format("Could not move resource {0}", this.getName()), ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#copyTo(java.lang.String)
	 */
	@Override
	public void copyTo(String path) throws RepositoryWriteException {
		try {
			String repositorySourcePath = getRepositoryPath().toString();
			String repositoryTargetPath = RepositoryPath.normalizePath(path, getName());
			getRepository().getRepositoryDao().copyFile(repositorySourcePath, repositoryTargetPath);
		} catch (Exception e) {
			throw new RepositoryWriteException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#exists()
	 */
	@Override
	public boolean exists() throws RepositoryReadException {
		String repositoryPath = getRepositoryPath().toString();
		try {
			return (getRepository().getRepositoryDao().fileExists(repositoryPath));
		} catch (SQLException e) {
			throw new RepositoryReadException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#isEmpty()
	 */
	@Override
	public boolean isEmpty() throws RepositoryReadException {
		return (getContent().length == 0);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResource#getContent()
	 */
	@Override
	public byte[] getContent() throws RepositoryReadException {
		final DatabaseFile document = getDocumentSafe();
		try {
			byte[] bytes = document.getData();
			return bytes;
		} catch (DatabaseRepositoryException ex) {
			throw new RepositoryReadException("Could not read resource content.", ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResource#setContent(byte[])
	 */
	@Override
	public void setContent(byte[] content) throws RepositoryWriteException {

		this.contentType = ContentTypeHelper.getContentType(ContentTypeHelper.getExtension(getName()));
		this.binary = ContentTypeHelper.isBinary(contentType);

		if (exists()) {
			final DatabaseFile document = getDocumentSafe();
			try {
				document.setData(content);
			} catch (DatabaseRepositoryException ex) {
				throw new RepositoryWriteException("Could not update document.", ex);
			}
		} else {
			getParent().createResource(getName(), content, this.binary, this.contentType);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.local.LocalEntity#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof DatabaseResource)) {
			return false;
		}
		final DatabaseResource other = (DatabaseResource) obj;
		return getPath().equals(other.getPath());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.local.LocalEntity#hashCode()
	 */
	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

	/**
	 * Returns the {@link DatabaseFile} object matching this {@link DatabaseResource}. If
	 * there is no such object, then <code>null</code> is returned.
	 *
	 * @return the document
	 * @throws RepositoryReadException
	 *             the repository read exception
	 */
	protected DatabaseFile getDocument() throws RepositoryReadException {
		final DatabaseObject object = getLocalObject();
		if (object == null) {
			return null;
		}
		if (!(object instanceof DatabaseFile)) {
			return null;
		}
		return (DatabaseFile) object;
	}

	/**
	 * Returns the {@link DatabaseFile} object matching this {@link DatabaseResource}. If
	 * there is no such object, then an {@link RepositoryNotFoundException} is thrown.
	 *
	 * @return the document safe
	 * @throws RepositoryNotFoundException
	 *             the repository not found exception
	 */
	protected DatabaseFile getDocumentSafe() throws RepositoryNotFoundException {
		final DatabaseFile document = getDocument();
		if (document == null) {
			throw new RepositoryNotFoundException(format("There is no resource at path ''{0}''.", getPath()));
		}
		return document;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResource#isBinary()
	 */
	@Override
	public boolean isBinary() {
		return binary;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResource#getContentType()
	 */
	@Override
	public String getContentType() {
		return contentType;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResource#setContent(byte[], boolean, java.lang.String)
	 */
	@Override
	public void setContent(byte[] content, boolean isBinary, String contentType) throws RepositoryWriteException {

		this.binary = isBinary;
		this.contentType = contentType;

		if (!isBinary) {
			setContent(content);
		}

		if (exists()) {
			final DatabaseFile document = getDocumentSafe();
			try {
				document.setData(content);
			} catch (DatabaseRepositoryException ex) {
				throw new RepositoryWriteException("Could not update document.", ex);
			}
		} else {
			getParent().createResource(getName(), content, binary, contentType);
		}

	}

}
