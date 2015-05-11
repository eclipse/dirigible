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
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

import org.eclipse.dirigible.ide.common.status.DefaultProgressMonitor;
import org.eclipse.dirigible.ide.workspace.RemoteResourcesPlugin;
import org.eclipse.dirigible.ide.workspace.impl.event.ResourceChangeEvent;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IEntityInformation;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.logging.Logger;

public abstract class Resource implements IResource {

	private static final String OK = Messages.Resource_OK;

	private static final String METHOD_SET_TEAM_PRIVATE_MEMBER_NOT_SUPPORTED = Messages.Resource_METHOD_SET_TEAM_PRIVATE_MEMBER_NOT_SUPPORTED;

	private static final String METHOD_SET_READ_ONLY_NOT_SUPPORTED = Messages.Resource_METHOD_SET_READ_ONLY_NOT_SUPPORTED;

//	private static final String PERSISTANT_PROPERTIES_NOT_SUPPORTED = Messages.Resource_PERSISTANT_PROPERTIES_NOT_SUPPORTED;

	private static final String METHOD_SET_LOCAL_TIME_STAMP_NOT_SUPPORTED = Messages.Resource_METHOD_SET_LOCAL_TIME_STAMP_NOT_SUPPORTED;

	private static final String METHOD_SET_LOCAL_NOT_SUPPORTED = Messages.Resource_METHOD_SET_LOCAL_NOT_SUPPORTED;

	private static final String METHOD_SET_HIDDEN_NOT_SUPPORTED = Messages.Resource_METHOD_SET_HIDDEN_NOT_SUPPORTED;

	private static final String METHOD_SET_DERIVED_NOT_SUPPORTED = Messages.Resource_METHOD_SET_DERIVED_NOT_SUPPORTED;

	private static final String ROOLBACK_NOT_SUPPORTED = Messages.Resource_ROOLBACK_NOT_SUPPORTED;

	private static final String MOVE_IS_STILL_UNSUPPORTED = Messages.Resource_MOVE_IS_STILL_UNSUPPORTED;

	private static final String COULD_NOT_RENAME_RESOURCE = Messages.Resource_COULD_NOT_RENAME_RESOURCE;

	private static final String COULD_NOT_DELETE_RESOURCE = Messages.Resource_COULD_NOT_DELETE_RESOURCE;

	private static final String MARKERS_ARE_NOT_SUPPORTED = "Markers are not supported."; //$NON-NLS-1$

	private static final String RULES_ARE_NOT_SUPPORTED = "Rules are not supported."; //$NON-NLS-1$

	private static final String RESOURCE_PATH_CANNOT_BE_NULL = Messages.Resource_RESOURCE_PATH_CANNOT_BE_NULL;

	private static final Logger logger = Logger.getLogger(Resource.class);
	
	/*
	 * Absolute path relative to the workspace root.
	 */
	protected final IPath path;

	protected final Workspace workspace;

	protected final Map<QualifiedName, String> sessionProperties = new HashMap<QualifiedName, String>();

	public Resource(IPath path, Workspace workspace) {
		if (path == null) {
			throw new IllegalArgumentException(RESOURCE_PATH_CANNOT_BE_NULL);
		}
		this.path = path;
		this.workspace = workspace;
	}

	protected IRepository getRepository() {
		return workspace.getRepository();
	}

	public IEntity getEntity() {
		IWorkspaceRoot root = workspace.getRoot();
		IPath repositoryPath = root.getLocation().append(path);
		IRepository repository = getRepository();
		return repository.getResource(repositoryPath.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(IResourceVisitor visitor) throws CoreException {
		accept(visitor, IResource.DEPTH_INFINITE, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(IResourceProxyVisitor visitor, int memberFlags)
			throws CoreException {
		// FIXME: Implement
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(IResourceVisitor visitor, int depth,
			boolean includePhantoms) throws CoreException {
		accept(visitor, depth, includePhantoms ? IContainer.INCLUDE_PHANTOMS
				: 0);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(IResourceVisitor visitor, int depth, int memberFlags)
			throws CoreException {
		// FIXME: Implement
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public void clearHistory(IProgressMonitor arg0) throws CoreException {
		// We store no history, so we do nothing.
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(ISchedulingRule rule) {
		throw new UnsupportedOperationException(RULES_ARE_NOT_SUPPORTED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void copy(IPath destination, boolean force, IProgressMonitor monitor)
			throws CoreException {
		int updateFlags = force ? IResource.FORCE : IResource.NONE;
		copy(destination, updateFlags, monitor);
	}

	/**
	 * {@inheritDoc}
	 */
	public void copy(IPath destination, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		// FIXME: Implement
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public void copy(IProjectDescription description, boolean force,
			IProgressMonitor monitor) throws CoreException {
		int updateFlags = force ? IResource.FORCE : IResource.NONE;
		copy(description, updateFlags, monitor);
	}

	/**
	 * {@inheritDoc}
	 */
	public void copy(IProjectDescription description, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		// FIXME: Implement
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public IMarker createMarker(String type) throws CoreException {
		throw new UnsupportedOperationException(MARKERS_ARE_NOT_SUPPORTED);
	}

	/**
	 * {@inheritDoc}
	 */
	public IResourceProxy createProxy() {
		// FIXME: Implement
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public void delete(boolean force, IProgressMonitor monitor)
			throws CoreException {
		delete(force ? IResource.FORCE : IResource.NONE, monitor);
	}

	/**
	 * {@inheritDoc}
	 */
	public void delete(int updateFlags, IProgressMonitor monitor)
			throws CoreException {
		monitor = monitorWrapper(monitor);
		try {
			monitor.beginTask("deletion", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
			if (exists()) {
				try {
					getEntity().delete();
				} catch (IOException ex) {
					logger.error(ex.getMessage(), ex);
					throw new CoreException(createErrorStatus(COULD_NOT_DELETE_RESOURCE)); // NOPMD
				}
				workspace.notifyResourceChanged(new ResourceChangeEvent(this,
						ResourceChangeEvent.POST_CHANGE));
			}
		} finally {
			monitor.done();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void deleteMarkers(String arg0, boolean arg1, int arg2)
			throws CoreException {
		// We do not support markers
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Resource) {
			Resource other = (Resource) obj;
			return (getType() == other.getType()) && (path.equals(other.path))
					&& (workspace.equals(other.workspace));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getType() * 72 + path.hashCode() * 72 + workspace.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean exists() {
		IProject project = getProject();
		if (project == null) {
			return (this instanceof IWorkspaceRoot);
		}
		if (!project.isOpen()) {
			return false;
		}
		try {
			return getEntity().exists();
		} catch (IOException ex) {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IMarker findMarker(long id) throws CoreException {
		throw new UnsupportedOperationException(MARKERS_ARE_NOT_SUPPORTED);
	}

	/**
	 * {@inheritDoc}
	 */
	public IMarker[] findMarkers(String type, boolean includeSubTypes, int depth)
			throws CoreException {
		throw new UnsupportedOperationException(MARKERS_ARE_NOT_SUPPORTED);
	}

	/**
	 * {@inheritDoc}
	 */
	public int findMaxProblemSeverity(String type, boolean includeSubtypes,
			int depth) throws CoreException {
		// Not supported. API isn't well documented either.
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getFileExtension() {
		return path.getFileExtension();
	}

	/**
	 * {@inheritDoc}
	 */
	public IPath getFullPath() {
		if (getType() == IResource.ROOT) {
			return Path.ROOT;
		} else {
			return path;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public long getLocalTimeStamp() {
		try {
			IEntityInformation information = getEntity().getInformation();
			Date timeStamp = information.getModifiedAt();
			if (timeStamp != null) {
				return timeStamp.getTime();
			} else {
				return IResource.NULL_STAMP;
			}
		} catch (IOException ex) {
			return IResource.NULL_STAMP;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IPath getLocation() {
		return workspace.getLocation().append(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public URI getLocationURI() {
		return URI.create(getLocation().toString());
	}

	/**
	 * {@inheritDoc}
	 */
	public IMarker getMarker(long arg0) {
		// We do not support markers
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public long getModificationStamp() {
		return getLocalTimeStamp();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		if (this instanceof IWorkspaceRoot) {
			return ""; //$NON-NLS-1$
		}
		return path.lastSegment();
	}

	/**
	 * {@inheritDoc}
	 */
	public IContainer getParent() {
		if (getType() == IResource.ROOT) {
			return null;
		}
		IWorkspaceRoot root = workspace.getRoot();
		if (getType() == IResource.PROJECT) {
			return root;
		}
		IPath parentPath = path.removeLastSegments(1);
		if (parentPath.segmentCount() == 1) {
			return root.getProject(parentPath.lastSegment());
		} else {
			return root.getFolder(parentPath);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<QualifiedName, String> getPersistentProperties() throws CoreException {
		// We do not support persistent properties
//		throw new UnsupportedOperationException();
		return sessionProperties;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getPersistentProperty(QualifiedName key) throws CoreException {
		// We do not support persistent properties
//		throw new UnsupportedOperationException();
		return sessionProperties.get(key);
	}

	/**
	 * {@inheritDoc}
	 */
	public IProject getProject() {
		if (getType() == IResource.ROOT) {
			return null;
		}
		if (getType() == IResource.PROJECT) {
			return (IProject) this;
		}
		String projectName = path.segment(0);
		IWorkspaceRoot root = workspace.getRoot();
		return root.getProject(projectName);
	}

	/**
	 * {@inheritDoc}
	 */
	public IPath getProjectRelativePath() {
		if (getType() == IResource.ROOT) {
			return new Path(""); //$NON-NLS-1$
		}
		if (getType() == IResource.PROJECT) {
			return new Path(""); //$NON-NLS-1$
		}
		return path.removeFirstSegments(1);
	}

	/**
	 * {@inheritDoc}
	 */
	public IPath getRawLocation() {
		return getLocation();
	}

	/**
	 * {@inheritDoc}
	 */
	public URI getRawLocationURI() {
		return getLocationURI();
	}

	/**
	 * {@inheritDoc}
	 */
	public ResourceAttributes getResourceAttributes() {
		ResourceAttributes result = new ResourceAttributes();
		result.setArchive(false);
		result.setHidden(false);
		result.setExecutable(false);
		result.setReadOnly(false);
		result.setSymbolicLink(false); // Not sure...May be wrong.
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<QualifiedName, Object> getSessionProperties() throws CoreException {
		return new HashMap<QualifiedName, Object>(sessionProperties);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getSessionProperty(QualifiedName key) throws CoreException {
		return sessionProperties.get(key);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getType() {
		if (this instanceof IWorkspaceRoot) {
			return IResource.ROOT;
		}
		if (this instanceof IProject) {
			return IResource.PROJECT;
		}
		if (this instanceof IFolder) {
			return IResource.FOLDER;
		}
		if (this instanceof IFile) {
			return IResource.FILE;
		}
		return IResource.FILE;
	}

	/**
	 * {@inheritDoc}
	 */
	public IWorkspace getWorkspace() {
		return workspace;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasFilters() {
		// No filters supported.
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAccessible() {
		return exists();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isConflicting(ISchedulingRule rule) {
		throw new UnsupportedOperationException(RULES_ARE_NOT_SUPPORTED);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDerived() {
		return isDerived(IResource.NONE);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDerived(int options) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isHidden() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isHidden(int arg0) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isLinked() {
		return isLinked(IResource.NONE);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isLinked(int options) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	public boolean isLocal(int options) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isPhantom() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isReadOnly() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSynchronized(int depth) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isTeamPrivateMember() {
		return false; // Not sure this is true.
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isTeamPrivateMember(int options) {
		return false; // Not sure this is true.
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isVirtual() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void move(IPath destination, boolean force, IProgressMonitor monitor)
			throws CoreException {
		move(destination, force ? IResource.FORCE : IResource.NONE, monitor);
	}

	/**
	 * {@inheritDoc}
	 */
	public void move(IPath destination, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		monitor = monitorWrapper(monitor);
		try {
			monitor.beginTask("rename", IProgressMonitor.UNKNOWN); //$NON-NLS-1$

			try {
				IEntity entity = getEntity();
				if (entity instanceof ICollection) {
					ICollection collection = (ICollection) entity;
					collection.renameTo(destination.lastSegment());
				} else if (entity instanceof org.eclipse.dirigible.repository.api.IResource) {
					org.eclipse.dirigible.repository.api.IResource resource = (org.eclipse.dirigible.repository.api.IResource) entity;
					resource.renameTo(destination.lastSegment());
				}
			} catch (IOException ex) {
				throw new CoreException(createErrorStatus(
						COULD_NOT_RENAME_RESOURCE, ex));
			}
			workspace.notifyResourceChanged(new ResourceChangeEvent(this,
					ResourceChangeEvent.POST_CHANGE));
		} finally {
			monitor.done();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void move(IProjectDescription descritpion, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException(MOVE_IS_STILL_UNSUPPORTED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void move(IProjectDescription description, boolean force,
			boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		int updateFlags = force ? IResource.FORCE : IResource.NONE;
		updateFlags |= keepHistory ? IResource.KEEP_HISTORY : IResource.NONE;
		move(description, updateFlags, monitor);
	}

	/**
	 * {@inheritDoc}
	 */
	public void refreshLocal(int depth, IProgressMonitor monitor)
			throws CoreException {
		// XXX: Could implement this later on.
	}

	/**
	 * {@inheritDoc}
	 */
	public void revertModificationStamp(long value) throws CoreException {
		throw new UnsupportedOperationException(ROOLBACK_NOT_SUPPORTED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	public void setDerived(boolean isDerived) throws CoreException {
		logger.error(METHOD_SET_DERIVED_NOT_SUPPORTED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDerived(boolean isDerived, IProgressMonitor monitor)
			throws CoreException {
		logger.error(METHOD_SET_DERIVED_NOT_SUPPORTED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setHidden(boolean arg0) throws CoreException {
		logger.error(METHOD_SET_HIDDEN_NOT_SUPPORTED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	public void setLocal(boolean flag, int depth, IProgressMonitor monitor)
			throws CoreException {
		logger.error(METHOD_SET_LOCAL_NOT_SUPPORTED);
	}

	/**
	 * {@inheritDoc}
	 */
	public long setLocalTimeStamp(long value) throws CoreException {
		logger.error(METHOD_SET_LOCAL_TIME_STAMP_NOT_SUPPORTED);
		return getLocalTimeStamp();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setPersistentProperty(QualifiedName key, String value)
			throws CoreException {
//		throw new UnsupportedOperationException(
//				PERSISTANT_PROPERTIES_NOT_SUPPORTED);
		sessionProperties.put(key, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	public void setReadOnly(boolean readOnly) {
		logger.error(METHOD_SET_READ_ONLY_NOT_SUPPORTED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setResourceAttributes(ResourceAttributes attributes)
			throws CoreException {
		// Resource attributes are not supported.
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setSessionProperty(QualifiedName key, Object value)
			throws CoreException {
		if (value != null) {
			sessionProperties.put(key, value.toString());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTeamPrivateMember(boolean isTeamPrivate)
			throws CoreException {
		logger.error(METHOD_SET_TEAM_PRIVATE_MEMBER_NOT_SUPPORTED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void touch(IProgressMonitor monitor) throws CoreException {
		// FIXME: Implement later on...(dispatch event and maybe something
		// more).
		throw new UnsupportedOperationException();
	}

	protected static IProgressMonitor monitorWrapper(IProgressMonitor monitor) {
		return (monitor == null) ? new DefaultProgressMonitor() : monitor;
	}

	protected static IStatus createOkStatus() {
		return createOkStatus(OK);
	}

	protected static IStatus createOkStatus(String message) {
		return new Status(Status.OK, RemoteResourcesPlugin.PLUGIN_ID, message);
	}

	protected static IStatus createErrorStatus(String message) {
		return new Status(Status.ERROR, RemoteResourcesPlugin.PLUGIN_ID,
				message);
	}

	protected static IStatus createErrorStatus(String message, Throwable ex) {
		return new Status(Status.ERROR, RemoteResourcesPlugin.PLUGIN_ID,
				message, ex);
	}

}
