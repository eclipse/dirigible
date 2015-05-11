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

package org.eclipse.dirigible.repository.rcp;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryPath;

/**
 * The DB implementation of {@link ICollection}
 * 
 */
public class RCPCollection extends RCPEntity implements ICollection {

	private static final String THERE_IS_NO_COLLECTION_AT_PATH_0 = Messages.getString("DBCollection.THERE_IS_NO_COLLECTION_AT_PATH_0"); //$NON-NLS-1$
	private static final String COULD_NOT_CREATE_CHILD_DOCUMENT = Messages.getString("DBCollection.COULD_NOT_CREATE_CHILD_DOCUMENT"); //$NON-NLS-1$
	private static final String COULD_NOT_GET_CHILD_RESOURCE_NAMES = Messages.getString("DBCollection.COULD_NOT_GET_CHILD_RESOURCE_NAMES"); //$NON-NLS-1$
	private static final String COULD_NOT_CREATE_CHILD_COLLECTION = Messages.getString("DBCollection.COULD_NOT_CREATE_CHILD_COLLECTION"); //$NON-NLS-1$
	private static final String COULD_NOT_GET_CHILD_COLLECTION_NAMES = Messages.getString("DBCollection.COULD_NOT_GET_CHILD_COLLECTION_NAMES"); //$NON-NLS-1$
	private static final String NOT_IMPLEMENTED = Messages.getString("DBCollection.NOT_IMPLEMENTED"); //$NON-NLS-1$
	private static final String COULD_NOT_DELETE_COLLECTION = Messages.getString("DBCollection.COULD_NOT_DELETE_COLLECTION"); //$NON-NLS-1$
	private static final String COULD_NOT_RENAME_COLLECTION = Messages.getString("DBCollection.COULD_NOT_RENAME_COLLECTION"); //$NON-NLS-1$
	private static final String CANNOT_CREATE_ROOT_COLLECTION = Messages.getString("DBCollection.CANNOT_CREATE_ROOT_COLLECTION"); //$NON-NLS-1$

	public RCPCollection(RCPRepository repository, RepositoryPath path) {
		super(repository, path);
	}

	@Override
	public void create() throws IOException {
		final ICollection parent = getParent();
		if (parent == null) {
			throw new RCPBaseException(CANNOT_CREATE_ROOT_COLLECTION);
		}
		parent.createCollection(getName());
	}

	@Override
	public void delete() throws IOException {
		final RCPFolder folder = getFolderSafe();
		try {
			folder.deleteTree();
		} catch (RCPBaseException ex) {
			throw new IOException(COULD_NOT_DELETE_COLLECTION + this.getName(),
					ex);
		}
	}

	@Override
	public void renameTo(String name) throws IOException {
		final RCPFolder folder = getFolderSafe();
		try {
			folder.renameFolder(RepositoryPath.normalizePath(getParent().getPath(), name));
		} catch (RCPBaseException ex) {
			throw new IOException(COULD_NOT_RENAME_COLLECTION + this.getName(),
					ex);
		}
	}

	@Override
	public void moveTo(String path) throws IOException {
		final RCPFolder folder = getFolderSafe();
		try {
			folder.renameFolder(path);
		} catch (RCPBaseException ex) {
			throw new IOException(COULD_NOT_RENAME_COLLECTION + this.getName(),
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
		if (IRepository.SEPARATOR.equals(getRepositoryPath().toString())) {
			return true;
		}
		return (getFolder() != null);
	}

	@Override
	public boolean isEmpty() throws IOException {
		return getResources().isEmpty() && getCollections().isEmpty();
	}

	@Override
	public List<ICollection> getCollections() throws IOException {
		// return new ArrayList<ICollection>(collections.values());
		final List<String> collectionNames = getCollectionsNames();
		final List<ICollection> result = new ArrayList<ICollection>(
				collectionNames.size());
		for (String collectionName : collectionNames) {
			result.add(getCollection(collectionName));
		}
		return result;
	}

	@Override
	public List<String> getCollectionsNames() throws IOException {
		final List<String> result = new ArrayList<String>();
		final RCPFolder folder = getFolderSafe();
		try {
			for (RCPObject child : folder.getChildren()) {
				if (child instanceof RCPFolder) {
					result.add(child.getName());
				}
			}
		} catch (RCPBaseException ex) {
			throw new IOException(COULD_NOT_GET_CHILD_COLLECTION_NAMES
					+ this.getName(), ex);
		}
		return result;
	}

	@Override
	public ICollection createCollection(String name) throws IOException {
		createAncestorsAndSelfIfMissing();
		final RCPFolder folder = getFolderSafe();
		try {
			folder.createFolder(name);
		} catch (RCPBaseException ex) {
			throw new IOException(COULD_NOT_CREATE_CHILD_COLLECTION + name, ex);
		}
		return getCollection(name);
	}

	@Override
	public ICollection getCollection(String name) {
		final RepositoryPath path = getRepositoryPath().append(name);
		return new RCPCollection(getRepository(), path);
	}

	@Override
	public void removeCollection(String name) throws IOException {
		final ICollection collection = getCollection(name);
		collection.delete();
	}

	@Override
	public void removeCollection(ICollection childCollection)
			throws IOException {
		removeCollection(childCollection.getName());
	}

	@Override
	public List<IResource> getResources() throws IOException {
		final List<String> resourceNames = getResourcesNames();
		final List<IResource> result = new ArrayList<IResource>(
				resourceNames.size());
		for (String resourceName : resourceNames) {
			result.add(getResource(resourceName));
		}
		return result;
	}

	@Override
	public List<String> getResourcesNames() throws IOException {
		final List<String> result = new ArrayList<String>();
		final RCPFolder folder = getFolderSafe();
		try {
			for (RCPObject child : folder.getChildren()) {
				if (child instanceof RCPFile) {
					result.add(child.getName());
				}
			}
		} catch (RCPBaseException ex) {
			throw new IOException(COULD_NOT_GET_CHILD_RESOURCE_NAMES
					+ this.getName(), ex);
		}
		return result;
	}

	@Override
	public IResource getResource(String name) throws IOException {
//		if (name != null
//				&& name.indexOf(IRepository.SEPARATOR) > -1) {
//			name = name.substring(name.indexOf(IRepository.SEPARATOR) + 1);
//		}
		final RepositoryPath path = getRepositoryPath().append(name);
		return new RCPResource(getRepository(), path);
	}

//	@Override
//	public IResource createResource(String name) throws IOException {
//		return createResource(name, null);
//	}
//
//	@Override
//	public IResource createResource(String name, byte[] content)
//			throws IOException {
//		createAncestorsAndSelfIfMissing();
//		final DBFolder folder = getFolderSafe();
//		try {
//			folder.createFile(name, content, false,
//					IResource.CONTENT_TYPE_DEFAULT);
//		} catch (DBBaseException ex) {
//			throw new IOException(COULD_NOT_CREATE_CHILD_DOCUMENT + name, ex);
//		}
//		return getResource(name);
//	}

	@Override
	public IResource createResource(String name, byte[] content,
			boolean isBinary, String contentType) throws IOException {
		createAncestorsAndSelfIfMissing();
		final RCPFolder folder = getFolderSafe();
		try {
			folder.createFile(name, content, isBinary, contentType);
		} catch (RCPBaseException ex) {
			throw new IOException(COULD_NOT_CREATE_CHILD_DOCUMENT + name, ex);
		}
		return getResource(name);
	}

	@Override
	public void removeResource(String name) throws IOException {
		final IResource resource = getResource(name);
		resource.delete();
	}

	@Override
	public void removeResource(IResource resource) throws IOException {
		removeResource(resource.getName());
	}

	@Override
	public List<IEntity> getChildren() throws IOException {
		final List<IEntity> result = new ArrayList<IEntity>();
		result.addAll(getCollections());
		result.addAll(getResources());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof RCPCollection)) {
			return false;
		}
		final RCPCollection other = (RCPCollection) obj;
		return getPath().equals(other.getPath());
	}

	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

	/**
	 * Returns the {@link Folder} object matching this {@link CMISContainer}. If
	 * there is no such object, then <code>null</code> is returned.
	 */
	protected RCPFolder getFolder() throws IOException {
		final RCPObject object = getRCPObject();
		if (object == null) {
			return null;
		}
		if (!(object instanceof RCPFolder)) {
			return null;
		}
		return (RCPFolder) object;
	}

	/**
	 * Returns the {@link RCPFolder} object matching this {@link DBCollection}.
	 * If there is no such object, then an {@link IOException} is thrown.
	 */
	protected RCPFolder getFolderSafe() throws IOException {
		final RCPFolder folder = getFolder();
		if (folder == null) {
			throw new IOException(format(THERE_IS_NO_COLLECTION_AT_PATH_0,
					getPath()));
		}
		return folder;
	}

}
