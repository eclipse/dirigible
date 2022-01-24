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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.workspace.api.IFolder#getInternal()
	 */
	@Override
	public ICollection getInternal() {
		return internal;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#getCollections()
	 */
	@Override
	public List<ICollection> getCollections() throws RepositoryReadException {
		return internal.getCollections();
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
	 * @see org.eclipse.dirigible.repository.api.IEntity#getName()
	 */
	@Override
	public String getName() {
		return internal.getName();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#getCollectionsNames()
	 */
	@Override
	public List<String> getCollectionsNames() throws RepositoryReadException {
		return internal.getCollectionsNames();
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
	 * @see org.eclipse.dirigible.repository.api.IEntity#getParent()
	 */
	@Override
	public ICollection getParent() {
		return internal.getParent();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#createCollection(java.lang.String)
	 */
	@Override
	public ICollection createCollection(String name) throws RepositoryReadException {
		return internal.createCollection(name);
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
	 * @see org.eclipse.dirigible.repository.api.ICollection#getCollection(java.lang.String)
	 */
	@Override
	public ICollection getCollection(String name) throws RepositoryReadException {
		return internal.getCollection(name);
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
	 * @see org.eclipse.dirigible.repository.api.ICollection#removeCollection(java.lang.String)
	 */
	@Override
	public void removeCollection(String name) throws RepositoryWriteException {
		internal.removeCollection(name);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#removeCollection(org.eclipse.dirigible.repository.api.
	 * ICollection)
	 */
	@Override
	public void removeCollection(ICollection collection) throws RepositoryWriteException {
		collection.removeCollection(collection);
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
	 * @see org.eclipse.dirigible.repository.api.ICollection#getResources()
	 */
	@Override
	public List<IResource> getResources() throws RepositoryReadException {
		return internal.getResources();
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
	 * @see org.eclipse.dirigible.repository.api.ICollection#getResourcesNames()
	 */
	@Override
	public List<String> getResourcesNames() throws RepositoryReadException {
		return internal.getResourcesNames();
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
	 * @see org.eclipse.dirigible.repository.api.ICollection#getResource(java.lang.String)
	 */
	@Override
	public IResource getResource(String name) throws RepositoryReadException {
		return internal.getResource(name);
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
	 * @see org.eclipse.dirigible.repository.api.ICollection#removeResource(java.lang.String)
	 */
	@Override
	public void removeResource(String name) throws RepositoryWriteException {
		internal.removeResource(name);
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
	 * @see
	 * org.eclipse.dirigible.repository.api.ICollection#removeResource(org.eclipse.dirigible.repository.api.IResource)
	 */
	@Override
	public void removeResource(IResource resource) throws RepositoryWriteException {
		internal.removeResource(resource);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.IEntity#isEmpty()
	 */
	@Override
	public boolean isEmpty() throws RepositoryReadException {
		return internal.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#getChildren()
	 */
	@Override
	public List<IEntity> getChildren() throws RepositoryReadException {
		return internal.getChildren();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#createResource(java.lang.String, byte[], boolean,
	 * java.lang.String)
	 */
	@Override
	public IResource createResource(String name, byte[] content, boolean isBinary, String contentType) throws RepositoryWriteException {
		return internal.createResource(name, content, isBinary, contentType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.api.ICollection#createResource(java.lang.String, byte[])
	 */
	@Override
	public IResource createResource(String name, byte[] content) throws RepositoryWriteException {
		return internal.createResource(name, content);
	}

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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.workspace.api.IFolder#deleteFolder(java.lang.String)
	 */
	@Override
	public void deleteFolder(String path) {
		String fullPath = constructPath(path);
		this.getRepository().removeCollection(fullPath);
	}

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
