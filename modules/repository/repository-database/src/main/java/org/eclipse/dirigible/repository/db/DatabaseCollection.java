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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryNotFoundException;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.api.RepositoryReadException;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;

/**
 * The file system based implementation of {@link ICollection}.
 */
public class DatabaseCollection extends DatabaseEntity implements ICollection {

	/**
	 * Instantiates a new local collection.
	 *
	 * @param repository
	 *            the repository
	 * @param path
	 *            the path
	 */
	public DatabaseCollection(DatabaseRepository repository, RepositoryPath path) {
		super(repository, path);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#create()
	 */
	@Override
	public void create() throws RepositoryWriteException {
		final ICollection parent = getParent();
		if (parent == null) {
			throw new DatabaseRepositoryException("Cannot create root collection.");
		}
		parent.createCollection(getName());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#delete()
	 */
	@Override
	public void delete() throws RepositoryWriteException {
		final DatabaseFolder folder = getFolderSafe();
		try {
			folder.deleteTree();
		} catch (DatabaseRepositoryException ex) {
			throw new RepositoryWriteException(format("Could not delete collection {0} ", this.getName()), ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#renameTo(java.lang.String)
	 */
	@Override
	public void renameTo(String name) throws RepositoryWriteException {
		final DatabaseFolder folder = getFolderSafe();
		try {
			folder.renameFolder(RepositoryPath.normalizePath(getParent().getPath(), name));
		} catch (DatabaseRepositoryException ex) {
			throw new RepositoryWriteException(format("Could not rename collection {0}", this.getName()), ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#moveTo(java.lang.String)
	 */
	@Override
	public void moveTo(String path) throws RepositoryWriteException {
		final DatabaseFolder folder = getFolderSafe();
		try {
			folder.renameFolder(path);
		} catch (DatabaseRepositoryException ex) {
			throw new RepositoryWriteException(format("Could not move collection {0}", this.getName()), ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#copyTo(java.lang.String)
	 */
	@Override
	public void copyTo(String path) throws RepositoryWriteException {
		final DatabaseFolder folder = getFolderSafe();
		try {
			folder.copyFolder(path);
		} catch (DatabaseRepositoryException ex) {
			throw new RepositoryWriteException(format("Could not copy collection {0}", this.getName()), ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#exists()
	 */
	@Override
	public boolean exists() throws RepositoryWriteException {
		String repositoryPath = getRepositoryPath().toString();
		if (IRepository.SEPARATOR.equals(repositoryPath)) {
			return true;
		}
		try {
			return getRepository().getRepositoryDao().folderExists(repositoryPath);
		} catch (SQLException e) {
			throw new RepositoryWriteException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#isEmpty()
	 */
	@Override
	public boolean isEmpty() throws RepositoryReadException {
		return getResources().isEmpty() && getCollections().isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#getCollections()
	 */
	@Override
	public List<ICollection> getCollections() throws RepositoryReadException {
		// return new ArrayList<ICollection>(collections.values());
		final List<String> collectionNames = getCollectionsNames();
		final List<ICollection> result = new ArrayList<ICollection>(collectionNames.size());
		for (String collectionName : collectionNames) {
			result.add(getCollection(collectionName));
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#getCollectionsNames()
	 */
	@Override
	public List<String> getCollectionsNames() throws RepositoryReadException {
		final List<String> result = new ArrayList<String>();
		final DatabaseFolder folder = getFolderSafe();
		try {
			for (DatabaseObject child : folder.getChildren()) {
				if (child instanceof DatabaseFolder) {
					result.add(child.getName());
				}
			}
		} catch (DatabaseRepositoryException ex) {
			throw new RepositoryReadException(format("Could not get child collection names {0} ", this.getName()), ex);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#createCollection(java.lang.String)
	 */
	@Override
	public ICollection createCollection(String name) throws RepositoryWriteException {
		createAncestorsAndSelfIfMissing();
		final DatabaseFolder folder = getFolderSafe();
		try {
			folder.createFolder(name);
		} catch (DatabaseRepositoryException ex) {
			throw new RepositoryWriteException(format("Could not create child collection {0}", name), ex);
		}
		return getCollection(name);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#getCollection(java.lang.String)
	 */
	@Override
	public ICollection getCollection(String name) {
		final RepositoryPath path = getRepositoryPath().append(name);
		return new DatabaseCollection(getRepository(), path);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#removeCollection(java.lang.String)
	 */
	@Override
	public void removeCollection(String name) throws RepositoryWriteException {
		final ICollection collection = getCollection(name);
		collection.delete();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#removeCollection(org.eclipse.dirigible.repository.api.
	 * ICollection)
	 */
	@Override
	public void removeCollection(ICollection childCollection) throws RepositoryWriteException {
		removeCollection(childCollection.getName());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#getResources()
	 */
	@Override
	public List<IResource> getResources() throws RepositoryReadException {
		final List<String> resourceNames = getResourcesNames();
		final List<IResource> result = new ArrayList<IResource>(resourceNames.size());
		for (String resourceName : resourceNames) {
			result.add(getResource(resourceName));
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#getResourcesNames()
	 */
	@Override
	public List<String> getResourcesNames() throws RepositoryReadException {
		final List<String> result = new ArrayList<String>();
		final DatabaseFolder folder = getFolderSafe();
		try {
			for (DatabaseObject child : folder.getChildren()) {
				if (child instanceof DatabaseFile) {
					result.add(child.getName());
				}
			}
		} catch (DatabaseRepositoryException ex) {
			throw new RepositoryReadException(format("Could not get child resource names {0}", this.getName()), ex);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#getResource(java.lang.String)
	 */
	@Override
	public IResource getResource(String name) throws RepositoryReadException {
		final RepositoryPath path = getRepositoryPath().append(name);
		return new DatabaseResource(getRepository(), path);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#createResource(java.lang.String, byte[], boolean,
	 * java.lang.String)
	 */
	@Override
	public IResource createResource(String name, byte[] content, boolean isBinary, String contentType) throws RepositoryWriteException {
		createAncestorsAndSelfIfMissing();
		final DatabaseFolder folder = getFolderSafe();
		try {
			folder.createFile(name, content, isBinary, contentType);
		} catch (DatabaseRepositoryException ex) {
			throw new RepositoryWriteException(format("Could not create child document {0}", name), ex);
		}
		return getResource(name);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#createResource(java.lang.String, byte[])
	 */
	@Override
	public IResource createResource(String name, byte[] content) throws RepositoryWriteException {
		String contentType = ContentTypeHelper.getContentType(ContentTypeHelper.getExtension(name));
		boolean isBinary = ContentTypeHelper.isBinary(contentType);
		return createResource(name, content, isBinary, contentType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#removeResource(java.lang.String)
	 */
	@Override
	public void removeResource(String name) throws RepositoryWriteException {
		final IResource resource = getResource(name);
		resource.delete();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.repository.api.ICollection#removeResource(org.eclipse.dirigible.repository.api.IResource)
	 */
	@Override
	public void removeResource(IResource resource) throws RepositoryWriteException {
		removeResource(resource.getName());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#getChildren()
	 */
	@Override
	public List<IEntity> getChildren() throws RepositoryReadException {
		final List<IEntity> result = new ArrayList<IEntity>();
		result.addAll(getCollections());
		result.addAll(getResources());
		return result;
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
		if (!(obj instanceof DatabaseCollection)) {
			return false;
		}
		final DatabaseCollection other = (DatabaseCollection) obj;
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
	 * Gets the folder.
	 *
	 * @return the folder
	 * @throws RepositoryReadException
	 *             the repository read exception
	 */
	protected DatabaseFolder getFolder() throws RepositoryReadException {
		final DatabaseObject object = getLocalObject();
		if (object == null) {
			return null;
		}
		if (!(object instanceof DatabaseFolder)) {
			return null;
		}
		return (DatabaseFolder) object;
	}

	/**
	 * Gets the folder safe.
	 *
	 * @return the folder safe
	 * @throws RepositoryNotFoundException
	 *             the repository not found exception
	 */
	protected DatabaseFolder getFolderSafe() throws RepositoryNotFoundException {
		final DatabaseFolder folder = getFolder();
		if (folder == null) {
			throw new RepositoryNotFoundException(format("There is no collection at path ''{0}''.", getPath()));
		}
		return folder;
	}

}
