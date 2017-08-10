package org.eclipse.dirigible.core.workspace.service;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.core.workspace.api.IFolder;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IEntityInformation;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryReadException;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;

public class Folder implements IFolder {

	private transient ICollection internal;

	public Folder(ICollection collection) {
		this.internal = collection;
	}

	@Override
	public ICollection getInternal() {
		return internal;
	}

	@Override
	public List<ICollection> getCollections() throws RepositoryReadException {
		return internal.getCollections();
	}

	@Override
	public IRepository getRepository() {
		return internal.getRepository();
	}

	@Override
	public String getName() {
		return internal.getName();
	}

	@Override
	public List<String> getCollectionsNames() throws RepositoryReadException {
		return internal.getCollectionsNames();
	}

	@Override
	public String getPath() {
		return internal.getPath();
	}

	@Override
	public ICollection getParent() {
		return internal.getParent();
	}

	@Override
	public ICollection createCollection(String name) throws RepositoryReadException {
		return internal.createCollection(name);
	}

	@Override
	public IEntityInformation getInformation() throws RepositoryReadException {
		return internal.getInformation();
	}

	@Override
	public ICollection getCollection(String name) throws RepositoryReadException {
		return internal.getCollection(name);
	}

	@Override
	public void create() throws RepositoryWriteException {
		internal.create();
	}

	@Override
	public void removeCollection(String name) throws RepositoryWriteException {
		internal.removeCollection(name);
	}

	@Override
	public void removeCollection(ICollection collection) throws RepositoryWriteException {
		collection.removeCollection(collection);
	}

	@Override
	public void delete() throws RepositoryWriteException {
		internal.delete();
	}

	@Override
	public List<IResource> getResources() throws RepositoryReadException {
		return internal.getResources();
	}

	@Override
	public void renameTo(String name) throws RepositoryWriteException {
		internal.renameTo(name);
	}

	@Override
	public List<String> getResourcesNames() throws RepositoryReadException {
		return internal.getResourcesNames();
	}

	@Override
	public void moveTo(String path) throws RepositoryWriteException {
		internal.moveTo(path);
	}

	@Override
	public IResource getResource(String name) throws RepositoryReadException {
		return internal.getResource(name);
	}

	@Override
	public void copyTo(String path) throws RepositoryWriteException {
		internal.copyTo(path);
	}

	@Override
	public void removeResource(String name) throws RepositoryWriteException {
		internal.removeResource(name);
	}

	@Override
	public boolean exists() throws RepositoryReadException {
		return internal.exists();
	}

	@Override
	public void removeResource(IResource resource) throws RepositoryWriteException {
		internal.removeResource(resource);
	}

	@Override
	public boolean isEmpty() throws RepositoryReadException {
		return internal.isEmpty();
	}

	@Override
	public List<IEntity> getChildren() throws RepositoryReadException {
		return internal.getChildren();
	}

	@Override
	public IResource createResource(String name, byte[] content, boolean isBinary, String contentType) throws RepositoryWriteException {
		return internal.createResource(name, content, isBinary, contentType);
	}

	@Override
	public IResource createResource(String name, byte[] content) throws RepositoryWriteException {
		return internal.createResource(name, content);
	}

	@Override
	public IFolder createFolder(String path) {
		String fullPath = this.getPath() + IRepositoryStructure.SEPARATOR + path;
		ICollection collection = this.getRepository().createCollection(fullPath);
		return new Folder(collection);
	}

	@Override
	public IFolder getFolder(String path) {
		String fullPath = this.getPath() + IRepositoryStructure.SEPARATOR + path;
		ICollection collection = this.getRepository().getCollection(fullPath);
		return new Folder(collection);
	}

	@Override
	public List<IFolder> getFolders() {
		List<IFolder> folders = new ArrayList<IFolder>();
		List<ICollection> collections = this.getCollections();
		for (ICollection collection : collections) {
			folders.add(new Folder(collection));
		}
		return folders;
	}

	@Override
	public void deleteFolder(String path) {
		String fullPath = this.getPath() + IRepositoryStructure.SEPARATOR + path;
		this.getRepository().removeCollection(fullPath);
	}

	@Override
	public IFile createFile(String path, byte[] content) {
		String fullPath = this.getPath() + IRepositoryStructure.SEPARATOR + path;
		IResource resource = this.getRepository().createResource(fullPath, content);
		return new File(resource);
	}

	@Override
	public IFile createFile(String path, byte[] content, boolean isBinary, String contentType) {
		String fullPath = this.getPath() + IRepositoryStructure.SEPARATOR + path;
		IResource resource = this.getRepository().createResource(fullPath, content, isBinary, contentType);
		return new File(resource);
	}

	@Override
	public IFile getFile(String path) {
		String fullPath = this.getPath() + IRepositoryStructure.SEPARATOR + path;
		IResource resource = this.getRepository().getResource(fullPath);
		return new File(resource);
	}

	@Override
	public List<IFile> getFiles() {
		List<IFile> files = new ArrayList<IFile>();
		List<IResource> resources = this.getResources();
		for (IResource resource : resources) {
			files.add(new File(resource));
		}
		return files;
	}

	@Override
	public void deleteFile(String path) {
		String fullPath = this.getPath() + IRepositoryStructure.SEPARATOR + path;
		this.getRepository().removeResource(fullPath);
	}

}
