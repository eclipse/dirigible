/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.workspace.impl;

import java.io.IOException;
import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dirigible.ide.workspace.impl.event.ResourceChangeEvent;
import org.eclipse.dirigible.repository.logging.Logger;

public class Folder extends Container implements IFolder {

	private static final String LINKED_FOLDERS_ARE_NOT_SUPPORTED2 = Messages.Folder_LINKED_FOLDERS_ARE_NOT_SUPPORTED2;
	private static final String LINKED_FOLDERS_ARE_NOT_SUPPORTED = Messages.Folder_LINKED_FOLDERS_ARE_NOT_SUPPORTED;
	private static final String COULD_NOT_CREATE_FOLDER = Messages.Folder_COULD_NOT_CREATE_FOLDER;
	private static final String PROJECT_IS_NOT_OPEN = Messages.Folder_PROJECT_IS_NOT_OPEN;
	private static final String PARENT_DOES_NOT_EXIST = Messages.Folder_PARENT_DOES_NOT_EXIST;
	private static final String A_RESOURCE_WITH_THIS_PATH_EXISTS = Messages.Folder_A_RESOURCE_WITH_THIS_PATH_EXISTS;

	private static final Logger logger = Logger.getLogger(Folder.class);

	public Folder(IPath path, Workspace workspace) {
		super(path, workspace);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create(boolean force, boolean local, IProgressMonitor monitor) throws CoreException {
		create((force ? IResource.FORCE : IResource.NONE), local, monitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create(int updateFlags, boolean local, IProgressMonitor monitor) throws CoreException {
		monitor = Resource.monitorWrapper(monitor);
		try {
			if (monitor != null) {
				monitor.beginTask("folder creation", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
			}
			IStatus pathValidation = workspace.validatePath(path.toString(), FOLDER);
			if (!pathValidation.isOK()) {
				throw new CoreException(pathValidation);
			}
			if (workspace.hasResource(getLocation())) {
				throw new CoreException(createErrorStatus(A_RESOURCE_WITH_THIS_PATH_EXISTS));
			}
			if (!getParent().exists()) {
				throw new CoreException(createErrorStatus(PARENT_DOES_NOT_EXIST));
			}
			if (!getProject().isOpen()) {
				throw new CoreException(createErrorStatus(PROJECT_IS_NOT_OPEN));
			}
			try {
				getEntity().create();
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
				throw new CoreException(createErrorStatus(COULD_NOT_CREATE_FOLDER)); // NOPMD
			}
			workspace.notifyResourceChanged(new ResourceChangeEvent(this, ResourceChangeEvent.POST_CHANGE));
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createLink(IPath localLocation, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException(LINKED_FOLDERS_ARE_NOT_SUPPORTED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createLink(URI location, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException(LINKED_FOLDERS_ARE_NOT_SUPPORTED2);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		int flags = (keepHistory ? KEEP_HISTORY : IResource.NONE) | (force ? FORCE : IResource.NONE);
		delete(flags, monitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFile getFile(String name) {
		IPath resourcePath = this.path.append(name);
		return new File(resourcePath, workspace);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFolder getFolder(String name) {
		IPath resourcePath = this.path.append(name);
		return new Folder(resourcePath, workspace);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void move(IPath destination, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		int flags = (keepHistory ? KEEP_HISTORY : IResource.NONE) | (force ? FORCE : IResource.NONE);
		move(destination, flags, monitor);
	}

}
