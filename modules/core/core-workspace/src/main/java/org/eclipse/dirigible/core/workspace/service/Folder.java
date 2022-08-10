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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.core.workspace.api.IFolder;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IEntityInformation;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.eclipse.dirigible.repository.api.RepositoryReadException;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;

/**
 * The Workspace's Folder.
 */
public class Folder implements IFolder {

	/** The internal. */
	private transient ICollection internal;

	/**
	 * Instantiates a new folder.
	 *
	 * @param collection
	 *            the collection
	 */
	public Folder(ICollection collection) {
		this.internal = collection;
	}

	/**
	 * Gets the internal.
	 *
	 * @return the internal
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.workspace.api.IFolder#getInternal()
	 */
	@Override
	public ICollection getInternal() {
		return internal;
	}

	/**
	 * Gets the collections.
	 *
	 * @return the collections
	 * @throws RepositoryReadException the repository read exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#getCollections()
	 */
	@Override
	public List<ICollection> getCollections() throws RepositoryReadException {
		return internal.getCollections();
	}

	/**
	 * Gets the repository.
	 *
	 * @return the repository
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#getRepository()
	 */
	@Override
	public IRepository getRepository() {
		return internal.getRepository();
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#getName()
	 */
	@Override
	public String getName() {
		return internal.getName();
	}

	/**
	 * Gets the collections names.
	 *
	 * @return the collections names
	 * @throws RepositoryReadException the repository read exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#getCollectionsNames()
	 */
	@Override
	public List<String> getCollectionsNames() throws RepositoryReadException {
		return internal.getCollectionsNames();
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#getPath()
	 */
	@Override
	public String getPath() {
		return internal.getPath();
	}

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#getParent()
	 */
	@Override
	public ICollection getParent() {
		return internal.getParent();
	}

	/**
	 * Creates the collection.
	 *
	 * @param name the name
	 * @return the i collection
	 * @throws RepositoryReadException the repository read exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#createCollection(java.lang.String)
	 */
	@Override
	public ICollection createCollection(String name) throws RepositoryReadException {
		return internal.createCollection(name);
	}

	/**
	 * Gets the information.
	 *
	 * @return the information
	 * @throws RepositoryReadException the repository read exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#getInformation()
	 */
	@Override
	public IEntityInformation getInformation() throws RepositoryReadException {
		return internal.getInformation();
	}

	/**
	 * Gets the collection.
	 *
	 * @param name the name
	 * @return the collection
	 * @throws RepositoryReadException the repository read exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#getCollection(java.lang.String)
	 */
	@Override
	public ICollection getCollection(String name) throws RepositoryReadException {
		return internal.getCollection(name);
	}

	/**
	 * Creates the.
	 *
	 * @throws RepositoryWriteException the repository write exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#create()
	 */
	@Override
	public void create() throws RepositoryWriteException {
		internal.create();
	}

	/**
	 * Removes the collection.
	 *
	 * @param name the name
	 * @throws RepositoryWriteException the repository write exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#removeCollection(java.lang.String)
	 */
	@Override
	public void removeCollection(String name) throws RepositoryWriteException {
		internal.removeCollection(name);
	}

	/**
	 * Removes the collection.
	 *
	 * @param collection the collection
	 * @throws RepositoryWriteException the repository write exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#removeCollection(org.eclipse.dirigible.repository.api.
	 * ICollection)
	 */
	@Override
	public void removeCollection(ICollection collection) throws RepositoryWriteException {
		collection.removeCollection(collection);
	}

	/**
	 * Delete.
	 *
	 * @throws RepositoryWriteException the repository write exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#delete()
	 */
	@Override
	public void delete() throws RepositoryWriteException {
		internal.delete();
	}

	/**
	 * Gets the resources.
	 *
	 * @return the resources
	 * @throws RepositoryReadException the repository read exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#getResources()
	 */
	@Override
	public List<IResource> getResources() throws RepositoryReadException {
		return internal.getResources();
	}

	/**
	 * Rename to.
	 *
	 * @param name the name
	 * @throws RepositoryWriteException the repository write exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#renameTo(java.lang.String)
	 */
	@Override
	public void renameTo(String name) throws RepositoryWriteException {
		internal.renameTo(name);
	}

	/**
	 * Gets the resources names.
	 *
	 * @return the resources names
	 * @throws RepositoryReadException the repository read exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#getResourcesNames()
	 */
	@Override
	public List<String> getResourcesNames() throws RepositoryReadException {
		return internal.getResourcesNames();
	}

	/**
	 * Move to.
	 *
	 * @param path the path
	 * @throws RepositoryWriteException the repository write exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#moveTo(java.lang.String)
	 */
	@Override
	public void moveTo(String path) throws RepositoryWriteException {
		internal.moveTo(path);
	}

	/**
	 * Gets the resource.
	 *
	 * @param name the name
	 * @return the resource
	 * @throws RepositoryReadException the repository read exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#getResource(java.lang.String)
	 */
	@Override
	public IResource getResource(String name) throws RepositoryReadException {
		return internal.getResource(name);
	}

	/**
	 * Copy to.
	 *
	 * @param path the path
	 * @throws RepositoryWriteException the repository write exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#copyTo(java.lang.String)
	 */
	@Override
	public void copyTo(String path) throws RepositoryWriteException {
		internal.copyTo(path);
	}

	/**
	 * Removes the resource.
	 *
	 * @param name the name
	 * @throws RepositoryWriteException the repository write exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#removeResource(java.lang.String)
	 */
	@Override
	public void removeResource(String name) throws RepositoryWriteException {
		internal.removeResource(name);
	}

	/**
	 * Exists.
	 *
	 * @return true, if successful
	 * @throws RepositoryReadException the repository read exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#exists()
	 */
	@Override
	public boolean exists() throws RepositoryReadException {
		return internal.exists();
	}

	/**
	 * Removes the resource.
	 *
	 * @param resource the resource
	 * @throws RepositoryWriteException the repository write exception
	 */
	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.dirigible.repository.api.ICollection#removeResource(org.eclipse.dirigible.repository.api.IResource)
	 */
	@Override
	public void removeResource(IResource resource) throws RepositoryWriteException {
		internal.removeResource(resource);
	}

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 * @throws RepositoryReadException the repository read exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#isEmpty()
	 */
	@Override
	public boolean isEmpty() throws RepositoryReadException {
		return internal.isEmpty();
	}

	/**
	 * Gets the children.
	 *
	 * @return the children
	 * @throws RepositoryReadException the repository read exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#getChildren()
	 */
	@Override
	public List<IEntity> getChildren() throws RepositoryReadException {
		return internal.getChildren();
	}

	/**
	 * Creates the resource.
	 *
	 * @param name the name
	 * @param content the content
	 * @param isBinary the is binary
	 * @param contentType the content type
	 * @return the i resource
	 * @throws RepositoryWriteException the repository write exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#createResource(java.lang.String, byte[], boolean,
	 * java.lang.String)
	 */
	@Override
	public IResource createResource(String name, byte[] content, boolean isBinary, String contentType) throws RepositoryWriteException {
		return internal.createResource(name, content, isBinary, contentType);
	}

	/**
	 * Creates the resource.
	 *
	 * @param name the name
	 * @param content the content
	 * @return the i resource
	 * @throws RepositoryWriteException the repository write exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#createResource(java.lang.String, byte[])
	 */
	@Override
	public IResource createResource(String name, byte[] content) throws RepositoryWriteException {
		return internal.createResource(name, content);
	}

	/**
	 * Creates the folder.
	 *
	 * @param path the path
	 * @return the i folder
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.workspace.api.IFolder#createFolder(java.lang.String)
	 */
	@Override
	public IFolder createFolder(String path) {
		String fullPath = constructPath(path);
		ICollection collection = this.getRepository().createCollection(fullPath);
		return new Folder(collection);
	}

	/**
	 * Gets the folder.
	 *
	 * @param path the path
	 * @return the folder
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.workspace.api.IFolder#getFolder(java.lang.String)
	 */
	@Override
	public IFolder getFolder(String path) {
		String fullPath = constructPath(path);
		ICollection collection = this.getRepository().getCollection(fullPath);
		return new Folder(collection);
	}

	/**
	 * Exists folder.
	 *
	 * @param path the path
	 * @return true, if successful
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.workspace.api.IFolder#existsFolder(java.lang.String)
	 */
	@Override
	public boolean existsFolder(String path) {
		String fullPath = constructPath(path);
		ICollection collection = this.getRepository().getCollection(fullPath);
		return collection.exists();
	}

	/**
	 * Gets the folders.
	 *
	 * @return the folders
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.workspace.api.IFolder#getFolders()
	 */
	@Override
	public List<IFolder> getFolders() {
		List<IFolder> folders = new ArrayList<IFolder>();
		List<ICollection> collections = this.getCollections();
		for (ICollection collection : collections) {
			folders.add(new Folder(collection));
		}
		return folders;
	}

	/**
	 * Delete folder.
	 *
	 * @param path the path
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.workspace.api.IFolder#deleteFolder(java.lang.String)
	 */
	@Override
	public void deleteFolder(String path) {
		String fullPath = constructPath(path);
		this.getRepository().removeCollection(fullPath);
	}

	/**
	 * Creates the file.
	 *
	 * @param path the path
	 * @param content the content
	 * @return the i file
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.workspace.api.IFolder#createFile(java.lang.String, byte[])
	 */
	@Override
	public IFile createFile(String path, byte[] content) {
		String fullPath = constructPath(path);
		IResource resource = this.getRepository().createResource(fullPath, content);
		return new File(resource);
	}

	/**
	 * Creates the file.
	 *
	 * @param path the path
	 * @param content the content
	 * @param isBinary the is binary
	 * @param contentType the content type
	 * @return the i file
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.workspace.api.IFolder#createFile(java.lang.String, byte[], boolean,
	 * java.lang.String)
	 */
	@Override
	public IFile createFile(String path, byte[] content, boolean isBinary, String contentType) {
		String fullPath = constructPath(path);
		IResource resource = this.getRepository().createResource(fullPath, content, isBinary, contentType);
		return new File(resource);
	}

	/**
	 * Gets the file.
	 *
	 * @param path the path
	 * @return the file
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.workspace.api.IFolder#getFile(java.lang.String)
	 */
	@Override
	public IFile getFile(String path) {
		String fullPath = constructPath(path);
		IResource resource = this.getRepository().getResource(fullPath);
		return new File(resource);
	}

	/**
	 * Exists file.
	 *
	 * @param path the path
	 * @return true, if successful
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.workspace.api.IFolder#existsFile(java.lang.String)
	 */
	@Override
	public boolean existsFile(String path) {
		String fullPath = constructPath(path);
		IResource resource = this.getRepository().getResource(fullPath);
		return resource.exists();
	}

	/**
	 * Gets the files.
	 *
	 * @return the files
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.workspace.api.IFolder#getFiles()
	 */
	@Override
	public List<IFile> getFiles() {
		List<IFile> files = new ArrayList<IFile>();
		List<IResource> resources = this.getResources();
		for (IResource resource : resources) {
			files.add(new File(resource));
		}
		return files;
	}

	/**
	 * Delete file.
	 *
	 * @param path the path
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.workspace.api.IFolder#deleteFile(java.lang.String)
	 */
	@Override
	public void deleteFile(String path) {
		String fullPath = constructPath(path);
		this.getRepository().removeResource(fullPath);
	}

	/**
	 * Construct path.
	 *
	 * @param path
	 *            the path
	 * @return the string
	 */
	protected String constructPath(String path) {
		return new RepositoryPath(new String[] { this.getPath(), path }).build();
	}

	/**
	 * Search.
	 *
	 * @param term the term
	 * @return the list
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.workspace.api.IFolder#search(java.lang.String)
	 */
	@Override
	public List<IFile> search(String term) {
		List<IEntity> entities = this.getRepository().searchText(term);
		List<IFile> files = new ArrayList<IFile>();
		for (IEntity entity : entities) {
			if (entity instanceof IResource) {
				IResource resource = (IResource) entity;
				if (resource.getPath().startsWith(this.getPath())) {
					if (resource.exists()) {
						files.add(new File(resource));
					}
				}
			}
		}
		return files;
	}
	
	/**
	 * Find.
	 *
	 * @param pattern the pattern
	 * @return the list
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.workspace.api.IFolder#find(java.lang.String)
	 */
	@Override
	public List<IFile> find(String pattern) {
		List<String> entities = this.getRepository().find(this.getPath(), pattern);
		List<IFile> files = new ArrayList<IFile>();
		for (String entity : entities) {
			IResource resource = this.getRepository().getResource(entity);
			if (resource.getPath().startsWith(this.getPath())) {
				if (resource.exists()) {
					files.add(new File(resource));
				}
			}
		}
		return files;
	}

}
