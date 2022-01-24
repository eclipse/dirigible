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
package org.eclipse.dirigible.core.workspace.service;

import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntityInformation;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryReadException;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;

/**
 * The Workspace's File.
 */
public class File implements IFile {

	private transient IResource internal;

	/**
	 * Instantiates a new file.
	 *
	 * @param resource
	 *            the resource
	 */
	public File(IResource resource) {
		this.internal = resource;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.workspace.api.IFile#getInternal()
	 */
	@Override
	public IResource getInternal() {
		return internal;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#getRepository()
	 */
	@Override
	public IRepository getRepository() {
		return internal.getRepository();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResource#getContent()
	 */
	@Override
	public byte[] getContent() throws RepositoryReadException {
		return internal.getContent();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#getName()
	 */
	@Override
	public String getName() {
		return internal.getName();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#getPath()
	 */
	@Override
	public String getPath() {
		return internal.getPath();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResource#setContent(byte[])
	 */
	@Override
	public void setContent(byte[] content) throws RepositoryWriteException {
		internal.setContent(content);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResource#setContent(byte[], boolean, java.lang.String)
	 */
	@Override
	public void setContent(byte[] content, boolean isBinary, String contentType) throws RepositoryWriteException {
		internal.setContent(content, isBinary, contentType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#getParent()
	 */
	@Override
	public ICollection getParent() {
		return internal.getParent();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#getInformation()
	 */
	@Override
	public IEntityInformation getInformation() throws RepositoryReadException {
		return internal.getInformation();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResource#isBinary()
	 */
	@Override
	public boolean isBinary() {
		return internal.isBinary();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IResource#getContentType()
	 */
	@Override
	public String getContentType() {
		return internal.getContentType();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#create()
	 */
	@Override
	public void create() throws RepositoryWriteException {
		internal.create();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#delete()
	 */
	@Override
	public void delete() throws RepositoryWriteException {
		internal.delete();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#renameTo(java.lang.String)
	 */
	@Override
	public void renameTo(String name) throws RepositoryWriteException {
		internal.renameTo(name);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#moveTo(java.lang.String)
	 */
	@Override
	public void moveTo(String path) throws RepositoryWriteException {
		internal.moveTo(path);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#copyTo(java.lang.String)
	 */
	@Override
	public void copyTo(String path) throws RepositoryWriteException {
		internal.copyTo(path);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#exists()
	 */
	@Override
	public boolean exists() throws RepositoryReadException {
		return internal.exists();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#isEmpty()
	 */
	@Override
	public boolean isEmpty() throws RepositoryReadException {
		return internal.isEmpty();
	}

}
