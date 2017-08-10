package org.eclipse.dirigible.core.workspace.service;

import java.util.List;

import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntityInformation;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.IResourceVersion;
import org.eclipse.dirigible.repository.api.RepositoryReadException;
import org.eclipse.dirigible.repository.api.RepositoryVersioningException;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;

public class File implements IFile {

	private transient IResource internal;

	public File(IResource resource) {
		this.internal = resource;
	}

	@Override
	public IResource getInternal() {
		return internal;
	}

	@Override
	public IRepository getRepository() {
		return internal.getRepository();
	}

	@Override
	public byte[] getContent() throws RepositoryReadException {
		return internal.getContent();
	}

	@Override
	public String getName() {
		return internal.getName();
	}

	@Override
	public String getPath() {
		return internal.getPath();
	}

	@Override
	public void setContent(byte[] content) throws RepositoryWriteException {
		internal.setContent(content);
	}

	@Override
	public void setContent(byte[] content, boolean isBinary, String contentType) throws RepositoryWriteException {
		internal.setContent(content, isBinary, contentType);
	}

	@Override
	public ICollection getParent() {
		return internal.getParent();
	}

	@Override
	public IEntityInformation getInformation() throws RepositoryReadException {
		return internal.getInformation();
	}

	@Override
	public boolean isBinary() {
		return internal.isBinary();
	}

	@Override
	public String getContentType() {
		return internal.getContentType();
	}

	@Override
	public List<IResourceVersion> getResourceVersions() throws RepositoryVersioningException {
		return internal.getResourceVersions();
	}

	@Override
	public IResourceVersion getResourceVersion(int version) throws RepositoryVersioningException {
		return internal.getResourceVersion(version);
	}

	@Override
	public void create() throws RepositoryWriteException {
		internal.create();
	}

	@Override
	public void delete() throws RepositoryWriteException {
		internal.delete();
	}

	@Override
	public void renameTo(String name) throws RepositoryWriteException {
		internal.renameTo(name);
	}

	@Override
	public void moveTo(String path) throws RepositoryWriteException {
		internal.moveTo(path);
	}

	@Override
	public void copyTo(String path) throws RepositoryWriteException {
		internal.copyTo(path);
	}

	@Override
	public boolean exists() throws RepositoryReadException {
		return internal.exists();
	}

	@Override
	public boolean isEmpty() throws RepositoryReadException {
		return internal.isEmpty();
	}

}
