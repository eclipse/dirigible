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
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFilterMatcherDescriptor;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNatureDescriptor;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.ISynchronizer;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
// import org.eclipse.core.resources.WorkspaceLock;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.ide.workspace.RemoteResourcesPlugin;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.api.RepositoryException;
import org.eclipse.dirigible.repository.logging.Logger;

@SuppressWarnings("deprecation")
public class Workspace implements IWorkspace {

	private static final String WORKSPACE_CHANGE_LISTENERS_FLAGS = "workspace.changeListenersFlags";

	private static final String WORKSPACE_CHANGE_LISTENERS = "workspace.changeListeners";

	private static final String INVALID_ROOT_PATH = Messages.Workspace_INVALID_ROOT_PATH;

	private static final String FILE_PATH_MUST_HAVE_ATLEAST_2_SEGMENTS = Messages.Workspace_FILE_PATH_MUST_HAVE_ATLEAST_2_SEGMENTS;

	private static final String FOLDER_PATH_MUST_HAVE_ATLEAST_2_SEGMENTS = Messages.Workspace_FOLDER_PATH_MUST_HAVE_ATLEAST_2_SEGMENTS;

	private static final String PROJECT_PATH_MUST_HAVE_1_SEGMENT = Messages.Workspace_PROJECT_PATH_MUST_HAVE_1_SEGMENT;

	private static final String PATH_MAY_NOT_CONTAIN_DEVICE_ID = Messages.Workspace_PATH_MAY_NOT_CONTAIN_DEVICE_ID;

	private static final String PATH_MUST_BE_ABSOLUTE = Messages.Workspace_PATH_MUST_BE_ABSOLUTE;

	private static final String PATH_MAY_NOT_CONTAIN = Messages.Workspace_PATH_MAY_NOT_CONTAIN;

	private static final String PATH_MAY_NOT_BE_EMPTY = Messages.Workspace_PATH_MAY_NOT_BE_EMPTY;

	private static final String PATH_MAY_NOT_BE_NULL = Messages.Workspace_PATH_MAY_NOT_BE_NULL;

	private static final String ROOT_NAME_IS_ALWAYS_INVALID = Messages.Workspace_ROOT_NAME_IS_ALWAYS_INVALID;

	private static final String NAME_MAY_NOT_BE_NULL = Messages.Workspace_NAME_MAY_NOT_BE_NULL;

	private static final String NATURES_ARE_NOT_SUPPORTED = Messages.Workspace_NATURES_ARE_NOT_SUPPORTED;

	private static final String WORKSPACE_STATE_NOT_SUPPORTED = Messages.Workspace_WORKSPACE_STATE_NOT_SUPPORTED;

	private static final String AUTO_BUILDING_IS_NOT_SUPPORTED = Messages.Workspace_AUTO_BUILDING_IS_NOT_SUPPORTED;

	private static final String WORKSPACE_IS_NOT_INITIALIZED = Messages.Workspace_WORKSPACE_IS_NOT_INITIALIZED;

	private static final String MARKERS_ARE_NOT_SUPPORTED = Messages.Workspace_MARKERS_ARE_NOT_SUPPORTED;

	private static final String ONE_OR_MORE_RESOURCES_WERE_NOT_DELETED = Messages.Workspace_ONE_OR_MORE_RESOURCES_WERE_NOT_DELETED;

	private static final String PROJECT_BUILDING_IS_NOT_SUPPORTED = Messages.Workspace_PROJECT_BUILDING_IS_NOT_SUPPORTED;

	private static final String LISTENER_MAY_NOT_BE_NULL = Messages.Workspace_LISTENER_MAY_NOT_BE_NULL;

	private static final String WORKSPACE_NOT_INITIALIZED = Messages.Workspace_WORKSPACE_NOT_INITIALIZED;

	private static final String COULD_NOT_CREATE_WORKSPACE_ROOT = Messages.Workspace_COULD_NOT_CREATE_WORKSPACE_ROOT;

	private static final String COULD_NOT_CREATE_REPOSITORY_HANDLER = Messages.Workspace_COULD_NOT_CREATE_REPOSITORY_HANDLER;

	private static final String WORKSPACE = "/workspace"; //$NON-NLS-1$

	private static final String DB_DIRIGIBLE_USERS = IRepositoryPaths.DB_DIRIGIBLE_USERS;

	private static final Logger logger = Logger.getLogger(Workspace.class);

	private final IRepository repository;

	private WorkspaceRoot root = null;

	private IPath location = null;

	private String username = null;

	public Workspace() throws RepositoryException {
		repository = RepositoryFacade.getInstance().getRepository();

		if (repository == null) {
			throw new RepositoryException(COULD_NOT_CREATE_REPOSITORY_HANDLER);
		}
	}

	public Workspace(HttpServletRequest request) throws RepositoryException {
		repository = RepositoryFacade.getInstance().getRepository(request);

		if (repository == null) {
			throw new RepositoryException(COULD_NOT_CREATE_REPOSITORY_HANDLER);
		}
	}

	private Map<IResourceChangeListener, Integer> lookupChangeListenersFlags() {
		Map<IResourceChangeListener, Integer> changeListenersFlags = (Map<IResourceChangeListener, Integer>) CommonIDEParameters
				.getObject(WORKSPACE_CHANGE_LISTENERS_FLAGS);
		if (changeListenersFlags == null) {
			changeListenersFlags = new HashMap<IResourceChangeListener, Integer>();
			CommonIDEParameters.setObject(WORKSPACE_CHANGE_LISTENERS_FLAGS, changeListenersFlags);
		}
		return changeListenersFlags;
	}

	private Set<IResourceChangeListener> lookupChangeListeners() {
		Set<IResourceChangeListener> changeListeners = (Set<IResourceChangeListener>) CommonIDEParameters.getObject(WORKSPACE_CHANGE_LISTENERS);
		if (changeListeners == null) {
			changeListeners = new HashSet<IResourceChangeListener>();
			CommonIDEParameters.setObject(WORKSPACE_CHANGE_LISTENERS, changeListeners);
		}
		return changeListeners;
	}

	public Workspace(IRepository repository) {
		this.repository = repository;
	}

	/**
	 * Use this method to initialize the workspace for the given username. If
	 * this is not done, the workspace will use a default shared workspace
	 * location.
	 *
	 * @param username
	 *            the username for which this workspace is active.
	 */
	public void initialize(String username) {
		this.username = username;
		String path = getRepositoryPathForWorkspace(username);
		this.location = new Path(path);
		try {
			repository.createCollection(path);
			root = new WorkspaceRoot(Path.ROOT, this);
		} catch (IOException ex) {
			throw new RuntimeException(COULD_NOT_CREATE_WORKSPACE_ROOT, ex);
		}
	}

	public String getRepositoryPathForWorkspace(String username) {
		return DB_DIRIGIBLE_USERS + username + WORKSPACE;
	}

	public IPath getLocation() {
		if (location == null) {
			throw new IllegalStateException(WORKSPACE_NOT_INITIALIZED);
		}
		return location;
	}

	public IRepository getRepository() {
		return repository;
	}

	/**
	 * Creates a new resource instance with the specified type.
	 * <p>
	 * The instance will be of the proper type: {@link IWorkspaceRoot},
	 * {@link IProject}, {@link Folder} and {@link IFile}. <br>
	 * If the resource if none of these types, or it does not exist on the local
	 * system, <code>null</code> will be returned.
	 */
	public IResource newResource(IPath path) {
		if (!root.getLocation().isPrefixOf(path)) {
			return null;
		}
		IPath extractedPath = path.removeFirstSegments(root.getLocation().segmentCount()).makeAbsolute();
		// Root
		if (extractedPath.equals(Path.ROOT)) {
			return root;
		}
		if (extractedPath.segmentCount() <= 0) {
			return root;
		}
		// Project
		if (extractedPath.segmentCount() == 1) {
			IProject project = root.getProject(extractedPath.segment(0));
			if (project.exists()) {
				return project;
			} else {
				return null;
			}
		}
		// Folder or File
		try {
			if (repository.hasCollection(path.toString())) {
				return new Folder(extractedPath, this);
			}
			if (repository.hasResource(path.toString())) {
				return new File(extractedPath, this);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public boolean hasResource(IPath path) {
		try {
			String location = path.toString();
			return repository.hasCollection(location) || repository.hasResource(location);
		} catch (IOException ex) {
			return true;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addResourceChangeListener(IResourceChangeListener listener) {
		final int flags = IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.POST_CHANGE;
		addResourceChangeListener(listener, flags);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addResourceChangeListener(IResourceChangeListener listener, int eventMask) {
		if (listener == null) {
			throw new IllegalArgumentException(LISTENER_MAY_NOT_BE_NULL);
		}
		lookupChangeListeners().add(listener);
		lookupChangeListenersFlags().put(listener, eventMask);
	}

	public void notifyResourceChanged(IResourceChangeEvent event) {
		for (IResourceChangeListener listener : new HashSet<IResourceChangeListener>(lookupChangeListeners())) {
			int flags = lookupChangeListenersFlags().get(listener);
			if ((event.getType() & flags) != 0) {
				listener.resourceChanged(event);
			}
		}
	}

	@Override
	public ISavedState addSaveParticipant(Plugin arg0, ISaveParticipant arg1) throws CoreException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ISavedState addSaveParticipant(String arg0, ISaveParticipant arg1) throws CoreException {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void build(int kind, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException(PROJECT_BUILDING_IS_NOT_SUPPORTED);
	}

	@Override
	public void checkpoint(boolean arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IProject[][] computePrerequisiteOrder(IProject[] arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ProjectOrder computeProjectOrder(IProject[] arg0) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus copy(IResource[] resources, IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {
		int flags = (force ? IResource.FORCE : IResource.NONE);
		return copy(resources, destination, flags, monitor);
	}

	@Override
	public IStatus copy(IResource[] arg0, IPath arg1, int arg2, IProgressMonitor arg3) throws CoreException {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus delete(IResource[] resources, boolean force, IProgressMonitor monitor) throws CoreException {
		int flags = IResource.KEEP_HISTORY | (force ? IResource.FORCE : IResource.NONE);
		return delete(resources, flags, monitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus delete(IResource[] resources, int updateFlags, IProgressMonitor monitor) throws CoreException {
		boolean deleteFailed = false;
		for (IResource resource : resources) {
			if (!resource.exists()) {
				continue;
			}
			resource.delete(updateFlags, monitor);
			if (resource.exists()) {
				deleteFailed = true;
			}
		}
		if (deleteFailed) {
			return createErrorStatus(ONE_OR_MORE_RESOURCES_WERE_NOT_DELETED);
		} else {
			return createOkStatus();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteMarkers(IMarker[] marksers) throws CoreException {
		throw new UnsupportedOperationException(MARKERS_ARE_NOT_SUPPORTED);
	}

	@Override
	public void forgetSavedTree(String arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<IProject, IProject[]> getDanglingReferences() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IWorkspaceDescription getDescription() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IFilterMatcherDescriptor getFilterMatcherDescriptor(String arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IFilterMatcherDescriptor[] getFilterMatcherDescriptors() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IProjectNatureDescriptor getNatureDescriptor(String arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IProjectNatureDescriptor[] getNatureDescriptors() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IPathVariableManager getPathVariableManager() {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IWorkspaceRoot getRoot() {
		if (root == null) {
			throw new IllegalStateException(WORKSPACE_IS_NOT_INITIALIZED);
		}
		return root;
	}

	@Override
	public IResourceRuleFactory getRuleFactory() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ISynchronizer getSynchronizer() {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAutoBuilding() {
		logger.error(AUTO_BUILDING_IS_NOT_SUPPORTED);
		return false;
	}

	@Override
	public boolean isTreeLocked() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IProjectDescription loadProjectDescription(IPath arg0) throws CoreException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IProjectDescription loadProjectDescription(InputStream arg0) throws CoreException {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus move(IResource[] resources, IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {
		int flags = IResource.KEEP_HISTORY | (force ? IResource.FORCE : IResource.NONE);
		return move(resources, destination, flags, monitor);
	}

	@Override
	public IStatus move(IResource[] resources, IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IProjectDescription newProjectDescription(String arg0) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeResourceChangeListener(IResourceChangeListener listener) {
		if (listener != null) {
			lookupChangeListeners().remove(listener);
			lookupChangeListenersFlags().remove(listener);
		}
	}

	@Override
	public void removeSaveParticipant(Plugin arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeSaveParticipant(String arg0) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run(IWorkspaceRunnable action, IProgressMonitor monitor) throws CoreException {
		run(action, getRoot(), IWorkspace.AVOID_UPDATE, monitor);
	}

	@Override
	public void run(IWorkspaceRunnable action, ISchedulingRule rule, int flags, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IStatus save(boolean full, IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException(WORKSPACE_STATE_NOT_SUPPORTED);
	}

	@Override
	public void setDescription(IWorkspaceDescription arg0) throws CoreException {
		throw new UnsupportedOperationException();
	}

	// @Override
	// @Deprecated
	// public void setWorkspaceLock(WorkspaceLock arg0) {
	// throw new UnsupportedOperationException();
	// }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] sortNatureSet(String[] natureIds) {
		throw new UnsupportedOperationException(NATURES_ARE_NOT_SUPPORTED);
	}

	@Override
	public IStatus validateEdit(IFile[] arg0, Object arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IStatus validateLinkLocation(IResource resource, IPath location) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus validateLinkLocationURI(IResource resource, URI location) {
		return validateLinkLocation(resource, new Path(location.toString()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus validateName(String segment, int typeMask) {
		// Specification
		if (segment == null) {
			throw new IllegalArgumentException(NAME_MAY_NOT_BE_NULL);
		}
		if (maskContainsFlag(typeMask, IResource.ROOT)) {
			return createErrorStatus(ROOT_NAME_IS_ALWAYS_INVALID);
		}

		if (maskContainsFlag(typeMask, IResource.PROJECT)) {
			return getValidationStatusForPath(segment);
		} else if (maskContainsFlag(typeMask, IResource.FOLDER)) {
			return getValidationStatusForPath(segment);
		} else {
			return getValidationStatusForFile(segment);
		}
	}

	private IStatus getValidationStatusForPath(String segment) {
		IStatus status;
		String projectRegExPattern = "([a-z][a-z_0-9]*)*[a-zA-Z_]($[A-Z_]|[\\w_])*"; //$NON-NLS-1$
		if (Pattern.matches(projectRegExPattern, segment)) {
			status = createOkStatus();
		} else {
			status = createErrorStatus(Messages.PROJECT_AND_FOLDER_NAME_FORMAT);
		}
		return status;
	}

	private IStatus getValidationStatusForFile(String segment) {
		IStatus status;
		String fileRegExPattern = "([a-zA-Z_0-9.-]+([a-zA-Z_0-9]+))$"; //$NON-NLS-1$
		if (Pattern.matches(fileRegExPattern, segment)) {

			if (segment.lastIndexOf("..") != -1) {
				return status = createErrorStatus(Messages.FILE_NAME_FORMAT);
			} else if (segment.lastIndexOf("--") != -1) {
				return status = createErrorStatus(Messages.FILE_NAME_FORMAT);
			} else if (segment.lastIndexOf(".-") != -1) {
				return status = createErrorStatus(Messages.FILE_NAME_FORMAT);
			} else if (segment.lastIndexOf("-.") != -1) {
				return status = createErrorStatus(Messages.FILE_NAME_FORMAT);
			}

			status = createOkStatus();
		} else {
			status = createErrorStatus(Messages.FILE_NAME_FORMAT);
		}
		return status;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus validateNatureSet(String[] natureIds) {
		throw new UnsupportedOperationException(NATURES_ARE_NOT_SUPPORTED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus validatePath(String path, int typeMask) {
		if (path == null) {
			throw new IllegalArgumentException(PATH_MAY_NOT_BE_NULL);
		}
		if (path.isEmpty()) {
			return createErrorStatus(PATH_MAY_NOT_BE_EMPTY);
		}
		if (path.contains("\\")) { //$NON-NLS-1$
			return createErrorStatus(PATH_MAY_NOT_CONTAIN);
		}
		if (maskContainsFlag(typeMask, IResource.ROOT)) {
			IStatus status = validateRootPath(path);
			if (!status.isOK()) {
				return status;
			}
		}
		Path wrapperPath = new Path(path);
		if (!wrapperPath.isAbsolute()) {
			return createErrorStatus(PATH_MUST_BE_ABSOLUTE);
		}
		if (wrapperPath.getDevice() != null) {
			return createErrorStatus(PATH_MAY_NOT_CONTAIN_DEVICE_ID);
		}
		if (maskContainsFlag(typeMask, IResource.FILE)) {
			IStatus status = validateFilePath(wrapperPath);
			if (!status.isOK()) {
				return status;
			}
		}
		if (maskContainsFlag(typeMask, IResource.FOLDER)) {
			IStatus status = validateFolderPath(wrapperPath);
			if (!status.isOK()) {
				return status;
			}
		}
		if (maskContainsFlag(typeMask, IResource.PROJECT)) {
			IStatus status = validateProjectPath(wrapperPath);
			if (!status.isOK()) {
				return status;
			}
		}
		return createOkStatus();
	}

	private IStatus validateProjectPath(Path path) {
		if (path.segmentCount() != 1) {
			return createErrorStatus(PROJECT_PATH_MUST_HAVE_1_SEGMENT);
		}
		return validateName(path.lastSegment(), IResource.PROJECT);
	}

	private IStatus validateFolderPath(Path path) {
		if (path.segmentCount() < 2) {
			return createErrorStatus(FOLDER_PATH_MUST_HAVE_ATLEAST_2_SEGMENTS);
		}
		IStatus status = validateName(path.segment(0), IResource.PROJECT);
		for (int i = 1; i < path.segmentCount(); ++i) {
			status = validateName(path.segment(i), IResource.FOLDER);
			if (!status.isOK()) {
				return status;
			}
		}
		return createOkStatus();
	}

	private IStatus validateFilePath(Path path) {
		if (path.segmentCount() < 2) {
			return createErrorStatus(FILE_PATH_MUST_HAVE_ATLEAST_2_SEGMENTS);
		}
		IStatus status = validateName(path.segment(0), IResource.PROJECT);
		for (int i = 1; i < (path.segmentCount() - 1); ++i) {
			status = validateName(path.segment(i), IResource.FOLDER);
			if (!status.isOK()) {
				return status;
			}
		}
		return validateName(path.lastSegment(), IResource.FILE);
	}

	private IStatus validateRootPath(String path) {
		if (path.equals(getRoot().getLocation().toString())) {
			return createOkStatus();
		} else {
			return createErrorStatus(INVALID_ROOT_PATH + path);
		}
	}

	@Override
	public IStatus validateProjectLocation(IProject project, IPath location) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus validateProjectLocationURI(IProject project, URI location) {
		return validateProjectLocation(project, new Path(location.toString()));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Workspace) {
			Workspace other = (Workspace) obj;
			if (username == null) {
				return (other.username == null);
			}
			return username.equals(other.username);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return username.hashCode();
	}

	private static IStatus createOkStatus() {
		return createOkStatus(Messages.Workspace_OK);
	}

	private static IStatus createOkStatus(String message) {
		return new Status(Status.OK, RemoteResourcesPlugin.PLUGIN_ID, message);
	}

	private static IStatus createErrorStatus(String message) {
		return new Status(Status.ERROR, RemoteResourcesPlugin.PLUGIN_ID, message);
	}

	private static boolean maskContainsFlag(int mask, int flag) {
		return (mask & flag) == flag;
	}

	@Override
	public IStatus validateFiltered(IResource arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void build(IBuildConfiguration[] buildConfigs, int kind, boolean buildReferences, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public IBuildConfiguration newBuildConfig(String projectName, String configName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run(ICoreRunnable action, ISchedulingRule rule, int flags, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
	}

	@Override
	public void run(ICoreRunnable action, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
	}

}
