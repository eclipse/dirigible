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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.dirigible.ide.workspace.RemoteResourcesPlugin;
import org.eclipse.dirigible.ide.workspace.impl.event.ResourceChangeEvent;
import org.eclipse.dirigible.repository.api.ContentTypeHelper;
import org.eclipse.dirigible.repository.logging.Logger;

public class File extends Resource implements IFile {

	private static final String ROLLBACK_NOT_SUPPORTED = Messages.File_ROLLBACK_NOT_SUPPORTED;
	private static final String COULD_NOT_WRITE_TO_FILE = Messages.File_COULD_NOT_WRITE_TO_FILE;
	private static final String COULD_NOT_READ_FILE = Messages.File_COULD_NOT_READ_FILE;
	private static final String FILE_DOES_NOT_EXIST = Messages.File_FILE_DOES_NOT_EXIST;
	private static final String COULD_NOT_CREATE_RESOURCE = Messages.File_COULD_NOT_CREATE_RESOURCE;
	private static final String OWNER_PROJECT_IS_NOT_OPEN = Messages.File_OWNER_PROJECT_IS_NOT_OPEN;
	private static final String PARENT_DOES_NOT_EXIST = Messages.File_PARENT_DOES_NOT_EXIST;
	private static final String A_RESOURCE_WITH_THIS_PATH_EXISTS = Messages.File_A_RESOURCE_WITH_THIS_PATH_EXISTS;
	private static final String COULD_NOT_APPEND_CONTENTS = Messages.File_COULD_NOT_APPEND_CONTENTS;
	private static final String RESOURCE_DOES_NOT_EXIST = Messages.File_RESOURCE_DOES_NOT_EXIST;
	private String charset = null;

	private static final Logger logger = Logger.getLogger(File.class);

	public File(IPath path, Workspace workspace) {
		super(path, workspace);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendContents(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {
		monitor = monitorWrapper(monitor);
		try {
			if (monitor != null) {
				monitor.beginTask("appending file contents", //$NON-NLS-1$
						IProgressMonitor.UNKNOWN);
			}
			if (!exists()) {
				throw new CoreException(createErrorStatus(RESOURCE_DOES_NOT_EXIST));
			}
			try {
				org.eclipse.dirigible.repository.api.IResource resource = (org.eclipse.dirigible.repository.api.IResource) getEntity();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] content = resource.getContent();
				out.write(content);
				copyStream(source, out);
				resource.setContent(out.toByteArray());
			} catch (IOException ex) {
				throw new CoreException(createErrorStatus(COULD_NOT_APPEND_CONTENTS, ex));
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
	public void appendContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		int flags = (keepHistory ? KEEP_HISTORY : IResource.NONE) | (force ? FORCE : IResource.NONE);
		appendContents(source, flags, monitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create(InputStream source, boolean force, IProgressMonitor monitor) throws CoreException {
		int flags = (force ? FORCE : IResource.NONE);
		create(source, flags, monitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {
		monitor = monitorWrapper(monitor);
		try {
			if (monitor != null) {
				monitor.beginTask("file creation", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
			}
			IStatus pathValidation = workspace.validatePath(path.toString(), FILE);
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
				throw new CoreException(createErrorStatus(OWNER_PROJECT_IS_NOT_OPEN));
			}
			try {
				org.eclipse.dirigible.repository.api.IResource resource = (org.eclipse.dirigible.repository.api.IResource) getEntity();
				resource.setContent(readContent(source), isBinary(), getContentType());
			} catch (IOException ex) {
				throw new CoreException(createErrorStatus(COULD_NOT_CREATE_RESOURCE + this.getName(), ex));
			}
			workspace.notifyResourceChanged(new ResourceChangeEvent(this, ResourceChangeEvent.POST_CHANGE));
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}

	private static byte[] readContent(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(in.available());
		byte[] buffer = new byte[1024];
		do {
			int count = in.read(buffer);
			if (count > 0) {
				out.write(buffer, 0, count);
			} else {
				break;
			}
		} while (true);
		return out.toByteArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createLink(IPath localLocation, int updateFlags, IProgressMonitor monitor) throws CoreException {
		// File linking is not supported.
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createLink(URI location, int updateFlags, IProgressMonitor monitor) throws CoreException {
		// File linking is not supported.
		throw new UnsupportedOperationException();
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
	public String getCharset() throws CoreException {
		return getCharset(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCharset(boolean checkImplicit) throws CoreException {
		if (checkImplicit) {
			if (charset != null) {
				return charset;
			} else {
				return getParent().getDefaultCharset();
			}
		} else {
			return exists() ? charset : null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCharsetFor(Reader reader) throws CoreException {
		if (exists()) {
			return getCharset(false);
		} else {
			return getParent().getDefaultCharset();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IContentDescription getContentDescription() throws CoreException {
		// XXX: Could be implemented later on..
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream getContents() throws CoreException {
		return getContents(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream getContents(boolean force) throws CoreException {
		if (!exists()) {
			throw new CoreException(new Status(IStatus.ERROR, RemoteResourcesPlugin.PLUGIN_ID, FILE_DOES_NOT_EXIST));
		}
		try {
			org.eclipse.dirigible.repository.api.IResource resource = (org.eclipse.dirigible.repository.api.IResource) getEntity();
			return new ByteArrayInputStream(resource.getContent());
		} catch (IOException ex) {
			logger.debug(ex.getMessage(), ex);
			throw new CoreException(new Status(IStatus.ERROR, RemoteResourcesPlugin.PLUGIN_ID, // NOPMD
					COULD_NOT_READ_FILE)); // NOPMD
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public int getEncoding() throws CoreException {
		return IFile.ENCODING_UTF_8;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFileState[] getHistory(IProgressMonitor monitor) throws CoreException {
		// We do not support rollback
		return new IFileState[0];
	}

	@Override
	public void move(IPath destination, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		int flags = (keepHistory ? KEEP_HISTORY : IResource.NONE) | (force ? FORCE : IResource.NONE);
		move(destination, flags, monitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public void setCharset(String newCharset) throws CoreException {
		setCharset(newCharset, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCharset(String newCharset, IProgressMonitor monitor) throws CoreException {
		this.charset = newCharset;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setContents(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {
		monitor = monitorWrapper(monitor);
		try {
			if (monitor != null) {
				monitor.beginTask("file content change", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
			}
			if (!exists()) {
				throw new CoreException(new Status(IStatus.ERROR, RemoteResourcesPlugin.PLUGIN_ID, FILE_DOES_NOT_EXIST));
			}
			try {
				org.eclipse.dirigible.repository.api.IResource resource = (org.eclipse.dirigible.repository.api.IResource) getEntity();
				ByteArrayOutputStream out = new ByteArrayOutputStream(source.available());
				copyStream(source, out);
				resource.setContent(out.toByteArray());
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
				throw new CoreException(new Status(IStatus.ERROR, RemoteResourcesPlugin.PLUGIN_ID, COULD_NOT_WRITE_TO_FILE));
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
	public void setContents(IFileState source, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException(ROLLBACK_NOT_SUPPORTED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		int flags = (keepHistory ? KEEP_HISTORY : IResource.NONE) | (force ? FORCE : IResource.NONE);
		setContents(source, flags, monitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setContents(IFileState source, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		int flags = (keepHistory ? KEEP_HISTORY : IResource.NONE) | (force ? FORCE : IResource.NONE);
		setContents(source, flags, monitor);
	}

	private static void copyStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		do {
			int count = in.read(buffer);
			if (count > 0) {
				out.write(buffer, 0, count);
			} else {
				break;
			}
		} while (true);
	}

	private String getContentType() {
		return ContentTypeHelper.getContentType(getFileExtension());
	}

	private boolean isBinary() {
		return ContentTypeHelper.isBinary(ContentTypeHelper.getContentType(getFileExtension()));
	}

	@Override
	public IPathVariableManager getPathVariableManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void accept(IResourceProxyVisitor visitor, int depth, int memberFlags) throws CoreException {
		// TODO Auto-generated method stub

	}

}
