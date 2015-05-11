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

package org.eclipse.dirigible.ide.workspace.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.FileInfoMatcherDescription;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceFilterDescription;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.logging.Logger;

public class Container extends Resource implements IContainer {

	private static final String COULD_NOT_GET_MEMBERS = Messages.Container_COULD_NOT_GET_MEMBERS;
	private static final String THE_FOLDER_PATH_MUST_HAVE_AT_LEAST_TWO_SEGMENTS = Messages.Container_THE_FOLDER_PATH_MUST_HAVE_AT_LEAST_TWO_SEGMENTS;
	private static final String THE_FILE_PATH_MUST_HAVE_AT_LEAST_TWO_SEGMENTS = Messages.Container_THE_FILE_PATH_MUST_HAVE_AT_LEAST_TWO_SEGMENTS;
	private String defaultCharset = "UTF-8"; //$NON-NLS-1$

	private static final Logger logger = Logger.getLogger(Container.class);
	
	public Container(IPath path, Workspace workspace) {
		super(path, workspace);
	}

	@Override
	public IEntity getEntity() {
		IRepository repository = getRepository();
		IWorkspaceRoot root = workspace.getRoot();
		IPath fullPath = root.getLocation().append(path);
		return repository.getCollection(fullPath.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	public IResourceFilterDescription createFilter(int type,
			FileInfoMatcherDescription matcherDescription, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		// We do not support filters
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean exists(IPath childPath) {
		IPath location = getLocation().append(childPath);
		return workspace.hasResource(location);
	}

	/**
	 * {@inheritDoc}
	 */
	public IFile[] findDeletedMembersWithHistory(int depth,
			IProgressMonitor monitor) throws CoreException {
		// We do not support rollback.
		return new IFile[0];
	}

	/**
	 * {@inheritDoc}
	 */
	public IResource findMember(String name) {
		return findMember(name, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public IResource findMember(IPath path) {
		return findMember(path, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public IResource findMember(String name, boolean phantom) {
		IPath resourcePath = getLocation().append(name);
		IResource resource = workspace.newResource(resourcePath);
		if (resource == null || !resource.exists()) {
			return null;
		}
		return resource;
	}

	/**
	 * {@inheritDoc}
	 */
	public IResource findMember(IPath path, boolean phantom) {
		IPath resourcePath = getLocation().append(path);
		IResource resource = workspace.newResource(resourcePath);
		if (resource == null || !resource.exists()) {
			return null;
		}
		return resource;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDefaultCharset() throws CoreException {
		return getDefaultCharset(true);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDefaultCharset(boolean checkImplicit) throws CoreException {
		return defaultCharset;
	}

	/**
	 * {@inheritDoc}
	 */
	public IFile getFile(IPath path) {
		IPath resourcePath = this.path.append(path);
		if (resourcePath.segmentCount() < 2) {
			throw new IllegalArgumentException(
					String.format(THE_FILE_PATH_MUST_HAVE_AT_LEAST_TWO_SEGMENTS, resourcePath.toString()));
		}
		return new File(resourcePath, workspace);
	}

	/**
	 * {@inheritDoc}
	 */
	public IResourceFilterDescription[] getFilters() throws CoreException {
		// We support no filters.
		return new IResourceFilterDescription[0];
	}

	/**
	 * {@inheritDoc}
	 */
	public IFolder getFolder(IPath path) {
		IPath resourcePath = this.path.append(path);
		if (resourcePath.segmentCount() < 2) {
			throw new IllegalArgumentException(
					String.format(THE_FOLDER_PATH_MUST_HAVE_AT_LEAST_TWO_SEGMENTS, resourcePath.toString()));
		}
		return new Folder(resourcePath, workspace);
	}

	/**
	 * {@inheritDoc}
	 */
	public IResource[] members() throws CoreException {
		return members(false);
	}

	/**
	 * {@inheritDoc}
	 */
	public IResource[] members(boolean phantom) throws CoreException {
		return members(phantom ? IContainer.INCLUDE_PHANTOMS : IContainer.NONE);
	}

	/**
	 * {@inheritDoc}
	 */
	public IResource[] members(int memberFlags) throws CoreException {
		ICollection collection = (ICollection) getEntity();
		List<String> childNames = new ArrayList<String>();
		try {
			childNames.addAll(collection.getCollectionsNames());
			childNames.addAll(collection.getResourcesNames());
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
			throw new CoreException(createErrorStatus(COULD_NOT_GET_MEMBERS + ex.getMessage())); // NOPMD 
		}
		List<IResource> result = new ArrayList<IResource>();
		for (String childName : childNames) {
			result.add(workspace.newResource(getLocation().append(childName)));
		}
		return result.toArray(new IResource[0]);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeFilter(IResourceFilterDescription arg0, int arg1,
			IProgressMonitor arg2) throws CoreException {
		// We do not support filters.
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDefaultCharset(String charset) throws CoreException {
		setDefaultCharset(charset, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDefaultCharset(String charset, IProgressMonitor monitor)
			throws CoreException {
		defaultCharset = charset;
	}

	@Override
	public IPathVariableManager getPathVariableManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void accept(IResourceProxyVisitor visitor, int depth, int memberFlags)
			throws CoreException {
		// TODO Auto-generated method stub
		
	}

}
