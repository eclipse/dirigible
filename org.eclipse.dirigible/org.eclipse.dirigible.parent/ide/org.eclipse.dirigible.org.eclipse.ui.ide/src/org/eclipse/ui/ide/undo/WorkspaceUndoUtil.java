/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.ide.undo;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dirigible.ide.workspace.RemoteResourcesPlugin;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.undo.ContainerDescription;
import org.eclipse.ui.internal.ide.undo.FileDescription;
import org.eclipse.ui.internal.ide.undo.UndoMessages;

/**
 * WorkspaceUndoUtil defines common utility methods and constants used by
 * clients who create undoable workspace operations.
 *
 * @since 3.3
 */
@SuppressWarnings("restriction")
public class WorkspaceUndoUtil {

	private static final String EMPTY_STRING = "";

	private static final String BOOKMARKS_CONTEXT = "Bookmarks Context";

	private static final String TASKS_CONTEXT = "Tasks Context";

	private static ObjectUndoContext tasksUndoContext;

	private static ObjectUndoContext bookmarksUndoContext;

	/**
	 * Return the undo context that should be used for workspace-wide operations
	 *
	 * @return the undo context suitable for workspace-level operations.
	 */
	public static IUndoContext getWorkspaceUndoContext() {
		return WorkbenchPlugin.getDefault().getOperationSupport().getUndoContext();
	}

	/**
	 * Return the undo context that should be used for operations involving
	 * tasks.
	 *
	 * @return the tasks undo context
	 */
	public static IUndoContext getTasksUndoContext() {
		synchronized (WorkspaceUndoUtil.class) {
			if (tasksUndoContext == null) {
				tasksUndoContext = new ObjectUndoContext(new Object(), TASKS_CONTEXT);
				tasksUndoContext.addMatch(getWorkspaceUndoContext());
			}
			return tasksUndoContext;
		}
	}

	/**
	 * Return the undo context that should be used for operations involving
	 * bookmarks.
	 *
	 * @return the bookmarks undo context
	 */
	public static IUndoContext getBookmarksUndoContext() {
		synchronized (WorkspaceUndoUtil.class) {
			if (bookmarksUndoContext == null) {
				bookmarksUndoContext = new ObjectUndoContext(new Object(), BOOKMARKS_CONTEXT);
				bookmarksUndoContext.addMatch(getWorkspaceUndoContext());
			}
			return bookmarksUndoContext;
		}
	}

	/**
	 * Make an <code>IAdaptable</code> that adapts to the specified shell,
	 * suitable for passing for passing to any
	 * {@link org.eclipse.core.commands.operations.IUndoableOperation} or
	 * {@link org.eclipse.core.commands.operations.IOperationHistory} method
	 * that requires an {@link org.eclipse.core.runtime.IAdaptable}
	 * <code>uiInfo</code> parameter.
	 *
	 * @param shell
	 *            the shell that should be returned by the IAdaptable when asked
	 *            to adapt a shell. If this parameter is <code>null</code>, the
	 *            returned shell will also be <code>null</code>.
	 * @return an IAdaptable that will return the specified shell.
	 */
	public static IAdaptable getUIInfoAdapter(final Shell shell) {
		return new IAdaptable() {
			@Override
			@SuppressWarnings("rawtypes")
			public Object getAdapter(Class clazz) {
				if (clazz == Shell.class) {
					return shell;
				}
				return null;
			}
		};
	}

	private WorkspaceUndoUtil() {
		// should not construct
	}

	/**
	 * Delete all of the specified resources, returning resource descriptions
	 * that can be used to restore them.
	 *
	 * @param resourcesToDelete
	 *            an array of resources to be deleted
	 * @param monitor
	 *            the progress monitor to use to show the operation's progress
	 * @param uiInfo
	 *            the IAdaptable (or <code>null</code>) provided by the caller
	 *            in order to supply UI information for prompting the user if
	 *            necessary. When this parameter is not <code>null</code>, it
	 *            contains an adapter for the
	 *            org.eclipse.swt.widgets.Shell.class
	 * @param deleteContent
	 *            a boolean indicating whether project content should be deleted
	 *            when a project resource is to be deleted
	 * @return an array of ResourceDescriptions that can be used to restore the
	 *         deleted resources.
	 * @throws CoreException
	 *             propagates any CoreExceptions thrown from the resources API
	 */
	static ResourceDescription[] delete(IResource[] resourcesToDelete, IProgressMonitor monitor, IAdaptable uiInfo, boolean deleteContent)
			throws CoreException {
		final List<CoreException> exceptions = new ArrayList<CoreException>();
		boolean forceOutOfSyncDelete = false;
		ResourceDescription[] returnedResourceDescriptions = new ResourceDescription[resourcesToDelete.length];
		monitor.beginTask(EMPTY_STRING, resourcesToDelete.length);
		monitor.setTaskName(UndoMessages.AbstractResourcesOperation_DeleteResourcesProgress);
		try {
			for (int i = 0; i < resourcesToDelete.length; ++i) {
				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}
				IResource resource = resourcesToDelete[i];
				try {
					returnedResourceDescriptions[i] = delete(resource, new SubProgressMonitor(monitor, 1), uiInfo, forceOutOfSyncDelete,
							deleteContent);
				} catch (CoreException e) {
					if (resource.getType() == IResource.FILE) {
						IStatus[] children = e.getStatus().getChildren();
						if ((children.length == 1) && (children[0].getCode() == IResourceStatus.OUT_OF_SYNC_LOCAL)) {
							int result = queryDeleteOutOfSync(resource, uiInfo);

							if (result == IDialogConstants.YES_ID) {
								// retry the delete with a force out of sync
								delete(resource, new SubProgressMonitor(monitor, 1), uiInfo, true, deleteContent);
							} else if (result == IDialogConstants.YES_TO_ALL_ID) {
								// all future attempts should force out of
								// sync
								forceOutOfSyncDelete = true;
								delete(resource, new SubProgressMonitor(monitor, 1), uiInfo, forceOutOfSyncDelete, deleteContent);
							} else if (result == IDialogConstants.CANCEL_ID) {
								throw new OperationCanceledException(); // NOPMD
							} else {
								exceptions.add(e);
							}
						} else {
							exceptions.add(e);
						}
					} else {
						exceptions.add(e);
					}
				}
			}
			IStatus result = createResult(exceptions);
			if (!result.isOK()) {
				throw new CoreException(result);
			}
		} finally {
			monitor.done();
		}
		return returnedResourceDescriptions;
	}

	/**
	 * Copies the resources to the given destination. This method can be called
	 * recursively to merge folders during folder copy.
	 *
	 * @param resources
	 *            the resources to be copied
	 * @param destination
	 *            the destination path for the resources, relative to the
	 *            workspace
	 * @param resourcesAtDestination
	 *            A list used to record the new copies.
	 * @param monitor
	 *            the progress monitor used to show progress
	 * @param uiInfo
	 *            the IAdaptable (or <code>null</code>) provided by the caller
	 *            in order to supply UI information for prompting the user if
	 *            necessary. When this parameter is not <code>null</code>, it
	 *            contains an adapter for the
	 *            org.eclipse.swt.widgets.Shell.class
	 * @param pathIncludesName
	 *            a boolean that indicates whether the specified path includes
	 *            the resource's name at the destination. If this value is
	 *            <code>true</code>, the destination will contain the desired
	 *            name of the resource (usually only desired when only one
	 *            resource is being copied). If this value is <code>false</code>
	 *            , each resource's name will be appended to the destination.
	 * @return an array of ResourceDescriptions describing any resources that
	 *         were overwritten by the copy operation
	 * @throws CoreException
	 *             propagates any CoreExceptions thrown from the resources API
	 */
	static ResourceDescription[] copy(IResource[] resources, IPath destination, List<IResource> resourcesAtDestination, IProgressMonitor monitor,
			IAdaptable uiInfo, boolean pathIncludesName) throws CoreException {

		monitor.beginTask(EMPTY_STRING, resources.length);
		monitor.setTaskName(UndoMessages.AbstractResourcesOperation_CopyingResourcesProgress);
		List<ResourceDescription> overwrittenResources = new ArrayList<ResourceDescription>();
		for (IResource source : resources) {
			IPath destinationPath;
			if (pathIncludesName) {
				destinationPath = destination;
			} else {
				destinationPath = destination.append(source.getName());
			}
			IWorkspaceRoot workspaceRoot = getWorkspaceRoot();
			IResource existing = workspaceRoot.findMember(destinationPath);
			if ((source.getType() == IResource.FOLDER) && (existing != null)) {
				// The resource is a folder and it exists in the destination.
				// Copy its children to the existing destination.
				if (source.isLinked() == existing.isLinked()) {
					IResource[] children = ((IContainer) source).members();
					ResourceDescription[] overwritten = copy(children, destinationPath, resourcesAtDestination, new SubProgressMonitor(monitor, 1),
							uiInfo, false);
					// We don't record the copy since this recursive call will
					// do so. Just record the overwrites.
					for (ResourceDescription element : overwritten) {
						overwrittenResources.add(element);
					}
				} else {
					// delete the destination folder, copying a linked folder
					// over an unlinked one or vice versa. Fixes bug 28772.
					ResourceDescription[] deleted = delete(new IResource[] { existing }, new SubProgressMonitor(monitor, 0), uiInfo, false);
					source.copy(destinationPath, IResource.SHALLOW, new SubProgressMonitor(monitor, 1));
					// Record the copy
					resourcesAtDestination.add(getWorkspace().getRoot().findMember(destinationPath));
					for (ResourceDescription element : deleted) {
						overwrittenResources.add(element);
					}
				}
			} else {
				if (existing != null) {
					if (source.isLinked() == existing.isLinked()) {
						overwrittenResources.add(copyOverExistingResource(source, existing, new SubProgressMonitor(monitor, 1), uiInfo, false));
						// Record the "copy"
						resourcesAtDestination.add(existing);
					} else {
						// Copying a linked resource over unlinked or vice
						// versa. Can't use setContents here. Fixes bug 28772.
						ResourceDescription[] deleted = delete(new IResource[] { existing }, new SubProgressMonitor(monitor, 0), uiInfo, false);
						source.copy(destinationPath, IResource.SHALLOW, new SubProgressMonitor(monitor, 1));
						// Record the copy
						resourcesAtDestination.add(getWorkspace().getRoot().findMember(destinationPath));
						for (ResourceDescription element : deleted) {
							overwrittenResources.add(element);
						}
					}
				} else {
					// no resources are being overwritten
					// ensure the destination path exists
					IPath parentPath = destination;
					if (pathIncludesName) {
						parentPath = destination.removeLastSegments(1);
					}
					IContainer generatedParent = generateContainers(parentPath);
					source.copy(destinationPath, IResource.SHALLOW, new SubProgressMonitor(monitor, 1));
					// Record the copy. If we had to generate a parent
					// folder, that should be recorded as part of the copy
					if (generatedParent == null) {
						resourcesAtDestination.add(getWorkspace().getRoot().findMember(destinationPath));
					} else {
						resourcesAtDestination.add(generatedParent);
					}
				}

				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}
			}
		}
		monitor.done();
		return overwrittenResources.toArray(new ResourceDescription[overwrittenResources.size()]);

	}

	/**
	 * Moves the resources to the given destination. This method can be called
	 * recursively to merge folders during folder move.
	 *
	 * @param resources
	 *            the resources to be moved
	 * @param destination
	 *            the destination path for the resources, relative to the
	 *            workspace
	 * @param resourcesAtDestination
	 *            A list used to record each moved resource.
	 * @param reverseDestinations
	 *            A list used to record each moved resource's original location
	 * @param monitor
	 *            the progress monitor used to show progress
	 * @param uiInfo
	 *            the IAdaptable (or <code>null</code>) provided by the caller
	 *            in order to supply UI information for prompting the user if
	 *            necessary. When this parameter is not <code>null</code>, it
	 *            contains an adapter for the
	 *            org.eclipse.swt.widgets.Shell.class
	 * @param pathIncludesName
	 *            a boolean that indicates whether the specified path includes
	 *            the resource's name at the destination. If this value is
	 *            <code>true</code>, the destination will contain the desired
	 *            name of the resource (usually only desired when only one
	 *            resource is being moved). If this value is <code>false</code>,
	 *            each resource's name will be appended to the destination.
	 * @return an array of ResourceDescriptions describing any resources that
	 *         were overwritten by the move operation
	 * @throws CoreException
	 *             propagates any CoreExceptions thrown from the resources API
	 */
	static ResourceDescription[] move(IResource[] resources, IPath destination, List<IResource> resourcesAtDestination,
			List<IPath> reverseDestinations, IProgressMonitor monitor, IAdaptable uiInfo, boolean pathIncludesName) throws CoreException {

		monitor.beginTask(EMPTY_STRING, resources.length);
		monitor.setTaskName(UndoMessages.AbstractResourcesOperation_MovingResources);
		List<ResourceDescription> overwrittenResources = new ArrayList<ResourceDescription>();
		for (IResource source : resources) {
			IPath destinationPath;
			if (pathIncludesName) {
				destinationPath = destination;
			} else {
				destinationPath = destination.append(source.getName());
			}
			IWorkspaceRoot workspaceRoot = getWorkspaceRoot();
			IResource existing = workspaceRoot.findMember(destinationPath);
			if ((source.getType() == IResource.FOLDER) && (existing != null)) {
				// The resource is a folder and it exists in the destination.
				// Move its children to the existing destination.
				if (source.isLinked() == existing.isLinked()) {
					IResource[] children = ((IContainer) source).members();
					ResourceDescription[] overwritten = move(children, destinationPath, resourcesAtDestination, reverseDestinations,
							new SubProgressMonitor(monitor, 1), uiInfo, false);
					// We don't record the moved resources since the recursive
					// call has done so. Just record the overwrites.
					for (ResourceDescription element : overwritten) {
						overwrittenResources.add(element);
					}
					// Delete the source. No need to record it since it
					// will get moved back.
					delete(source, monitor, uiInfo, false, false);
				} else {
					// delete the destination folder, moving a linked folder
					// over an unlinked one or vice versa. Fixes bug 28772.
					ResourceDescription[] deleted = delete(new IResource[] { existing }, new SubProgressMonitor(monitor, 0), uiInfo, false);
					// Record the original path
					reverseDestinations.add(source.getFullPath());
					source.move(destinationPath, IResource.SHALLOW | IResource.KEEP_HISTORY, new SubProgressMonitor(monitor, 1));
					// Record the resource at its destination
					resourcesAtDestination.add(getWorkspace().getRoot().findMember(destinationPath));
					for (ResourceDescription element : deleted) {
						overwrittenResources.add(element);
					}
				}
			} else {
				if (existing != null) {
					if (source.isLinked() == existing.isLinked()) {
						// Record the original path
						reverseDestinations.add(source.getFullPath());
						overwrittenResources.add(copyOverExistingResource(source, existing, new SubProgressMonitor(monitor, 1), uiInfo, true));
						resourcesAtDestination.add(existing);
					} else {
						// Moving a linked resource over unlinked or vice
						// versa. Can't use setContents here. Fixes bug 28772.
						ResourceDescription[] deleted = delete(new IResource[] { existing }, new SubProgressMonitor(monitor, 0), uiInfo, false);
						reverseDestinations.add(source.getFullPath());
						source.move(destinationPath, IResource.SHALLOW | IResource.KEEP_HISTORY, new SubProgressMonitor(monitor, 1));
						// Record the resource at its destination
						resourcesAtDestination.add(getWorkspace().getRoot().findMember(destinationPath));
						for (ResourceDescription element : deleted) {
							overwrittenResources.add(element);
						}
					}
				} else {
					// No resources are being overwritten.
					// First record the source path
					reverseDestinations.add(source.getFullPath());
					// ensure the destination path exists
					IPath parentPath = destination;
					if (pathIncludesName) {
						parentPath = destination.removeLastSegments(1);
					}

					IContainer generatedParent = generateContainers(parentPath);
					source.move(destinationPath, IResource.SHALLOW | IResource.KEEP_HISTORY, new SubProgressMonitor(monitor, 1));
					// Record the move. If we had to generate a parent
					// folder, that should be recorded as part of the copy
					if (generatedParent == null) {
						resourcesAtDestination.add(getWorkspace().getRoot().findMember(destinationPath));
					} else {
						resourcesAtDestination.add(generatedParent);
					}
				}

				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}
			}
		}
		monitor.done();
		return overwrittenResources.toArray(new ResourceDescription[overwrittenResources.size()]);

	}

	/**
	 * Recreate the resources from the specified resource descriptions.
	 *
	 * @param resourcesToRecreate
	 *            the ResourceDescriptions describing resources to be recreated
	 * @param monitor
	 *            the progress monitor used to show progress
	 * @param uiInfo
	 *            the IAdaptable (or <code>null</code>) provided by the caller
	 *            in order to supply UI information for prompting the user if
	 *            necessary. When this parameter is not <code>null</code>, it
	 *            contains an adapter for the
	 *            org.eclipse.swt.widgets.Shell.class
	 * @return an array of resources that were created
	 * @throws CoreException
	 *             propagates any CoreExceptions thrown from the resources API
	 */
	static IResource[] recreate(ResourceDescription[] resourcesToRecreate, IProgressMonitor monitor, IAdaptable uiInfo) throws CoreException {
		final List<CoreException> exceptions = new ArrayList<CoreException>();
		IResource[] resourcesToReturn = new IResource[resourcesToRecreate.length];
		monitor.beginTask(EMPTY_STRING, resourcesToRecreate.length);
		monitor.setTaskName(UndoMessages.AbstractResourcesOperation_CreateResourcesProgress);
		try {
			for (int i = 0; i < resourcesToRecreate.length; ++i) {
				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}
				try {
					resourcesToReturn[i] = resourcesToRecreate[i].createResource(new SubProgressMonitor(monitor, 1));
				} catch (CoreException e) {
					exceptions.add(e);
				}
			}
			IStatus result = WorkspaceUndoUtil.createResult(exceptions);
			if (!result.isOK()) {
				throw new CoreException(result);
			}
		} finally {
			monitor.done();
		}
		return resourcesToReturn;
	}

	/**
	 * Delete the specified resources, returning a resource description that can
	 * be used to restore it.
	 *
	 * @param resourceToDelete
	 *            the resource to be deleted
	 * @param monitor
	 *            the progress monitor to use to show the operation's progress
	 * @param uiInfo
	 *            the IAdaptable (or <code>null</code>) provided by the caller
	 *            in order to supply UI information for prompting the user if
	 *            necessary. When this parameter is not <code>null</code>, it
	 *            contains an adapter for the
	 *            org.eclipse.swt.widgets.Shell.class
	 * @param forceOutOfSyncDelete
	 *            a boolean indicating whether a resource should be deleted even
	 *            if it is out of sync with the file system
	 * @param deleteContent
	 *            a boolean indicating whether project content should be deleted
	 *            when a project resource is to be deleted
	 * @return a ResourceDescription that can be used to restore the deleted
	 *         resource.
	 * @throws CoreException
	 *             propagates any CoreExceptions thrown from the resources API
	 */
	static ResourceDescription delete(IResource resourceToDelete, IProgressMonitor monitor, IAdaptable uiInfo, boolean forceOutOfSyncDelete,
			boolean deleteContent) throws CoreException {
		ResourceDescription resourceDescription = ResourceDescription.fromResource(resourceToDelete);
		if (resourceToDelete.getType() == IResource.PROJECT) {
			// it is a project
			monitor.setTaskName(UndoMessages.AbstractResourcesOperation_DeleteResourcesProgress);
			IProject project = (IProject) resourceToDelete;
			project.delete(deleteContent, forceOutOfSyncDelete, monitor);
		} else {
			// if it's not a project, just delete it
			monitor.beginTask(EMPTY_STRING, 2);
			monitor.setTaskName(UndoMessages.AbstractResourcesOperation_DeleteResourcesProgress);
			int updateFlags;
			if (forceOutOfSyncDelete) {
				updateFlags = IResource.KEEP_HISTORY | IResource.FORCE;
			} else {
				updateFlags = IResource.KEEP_HISTORY;
			}
			resourceToDelete.delete(updateFlags, new SubProgressMonitor(monitor, 1));
			resourceDescription.recordStateFromHistory(resourceToDelete, new SubProgressMonitor(monitor, 1));
			monitor.done();
		}

		return resourceDescription;
	}

	/*
	 * Copy the content of the specified resource to the existing resource,
	 * returning a ResourceDescription that can be used to restore the original
	 * content. Do nothing if the resources are not files.
	 */
	@SuppressWarnings("null")
	private static ResourceDescription copyOverExistingResource(IResource source, IResource existing, IProgressMonitor monitor, IAdaptable uiInfo,
			boolean deleteSourceFile) throws CoreException {
		if (!((source instanceof IFile) && (existing instanceof IFile))) {
			return null;
		}
		IFile file = (IFile) source;
		IFile existingFile = (IFile) existing;
		monitor.beginTask(UndoMessages.AbstractResourcesOperation_CopyingResourcesProgress, 3);
		if ((file != null) && (existingFile != null)) {
			if (validateEdit(file, existingFile, getShell(uiInfo))) {
				// Remember the state of the existing file so it can be
				// restored.
				FileDescription fileDescription = new FileDescription(existingFile);
				// Reset the contents to that of the file being moved
				existingFile.setContents(file.getContents(), IResource.KEEP_HISTORY, new SubProgressMonitor(monitor, 1));
				fileDescription.recordStateFromHistory(existingFile, new SubProgressMonitor(monitor, 1));
				// Now delete the source file if requested
				// We don't need to remember anything about it, because
				// any undo involving this operation will move the original
				// content back to it.
				if (deleteSourceFile) {
					file.delete(IResource.KEEP_HISTORY, new SubProgressMonitor(monitor, 1));
				}
				monitor.done();
				return fileDescription;
			}
		}
		monitor.done();
		return null;
	}

	/*
	 * Check for existence of the specified path and generate any containers
	 * that do not yet exist. Return any generated containers, or null if no
	 * container had to be generated.
	 */
	private static IContainer generateContainers(IPath path) throws CoreException {
		IResource container;
		if (path.segmentCount() == 0) {
			// nothing to generate
			return null;
		}
		container = getWorkspaceRoot().findMember(path);
		// Nothing to generate because container exists
		if (container != null) {
			return null;
		}

		// Now make a non-existent handle representing the desired container
		if (path.segmentCount() == 1) {
			container = RemoteResourcesPlugin.getWorkspace().getRoot().getProject(path.segment(0));
		} else {
			container = RemoteResourcesPlugin.getWorkspace().getRoot().getFolder(path);
		}
		ContainerDescription containerDescription = ContainerDescription.fromContainer((IContainer) container);
		container = containerDescription.createResourceHandle();
		containerDescription.createExistentResourceFromHandle(container, new NullProgressMonitor());
		return (IContainer) container;
	}

	/*
	 * Ask the user whether the given resource should be deleted despite being
	 * out of sync with the file system.
	 * Return one of the IDialogConstants constants indicating which of the Yes,
	 * Yes to All, No, Cancel options has been selected by the user.
	 */
	private static int queryDeleteOutOfSync(IResource resource, IAdaptable uiInfo) {
		Shell shell = getShell(uiInfo);
		final MessageDialog dialog = new MessageDialog(shell, UndoMessages.AbstractResourcesOperation_deletionMessageTitle, null,
				NLS.bind(UndoMessages.AbstractResourcesOperation_outOfSyncQuestion, resource.getName()), MessageDialog.QUESTION,
				new String[] { IDialogConstants.get().YES_LABEL, IDialogConstants.get().YES_TO_ALL_LABEL, IDialogConstants.get().NO_LABEL,
						IDialogConstants.get().CANCEL_LABEL },
				0);
		shell.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				dialog.open();
			}
		});
		int result = dialog.getReturnCode();
		if (result == 0) {
			return IDialogConstants.YES_ID;
		}
		if (result == 1) {
			return IDialogConstants.YES_TO_ALL_ID;
		}
		if (result == 2) {
			return IDialogConstants.NO_ID;
		}
		return IDialogConstants.CANCEL_ID;
	}

	/*
	 * Creates and return a result status appropriate for the given list of
	 * exceptions.
	 */
	private static IStatus createResult(List<CoreException> exceptions) {
		if (exceptions.isEmpty()) {
			return Status.OK_STATUS;
		}
		final int exceptionCount = exceptions.size();
		if (exceptionCount == 1) {
			return exceptions.get(0).getStatus();
		}
		CoreException[] children = exceptions.toArray(new CoreException[exceptionCount]);
		boolean outOfSync = false;
		for (CoreException element : children) {
			if (element.getStatus().getCode() == IResourceStatus.OUT_OF_SYNC_LOCAL) {
				outOfSync = true;
				break;
			}
		}
		String title = outOfSync ? UndoMessages.AbstractResourcesOperation_outOfSyncError
				: UndoMessages.AbstractResourcesOperation_deletionExceptionMessage;
		final MultiStatus multi = new MultiStatus(IDEWorkbenchPlugin.IDE_WORKBENCH, 0, title, null);
		for (int i = 0; i < exceptionCount; i++) {
			CoreException exception = children[i];
			IStatus status = exception.getStatus();
			multi.add(new Status(status.getSeverity(), status.getPlugin(), status.getCode(), status.getMessage(), exception));
		}
		return multi;
	}

	/*
	 * Return the workspace.
	 */
	private static IWorkspace getWorkspace() {
		return RemoteResourcesPlugin.getWorkspace();
	}

	/*
	 * Return the workspace root.
	 */
	private static IWorkspaceRoot getWorkspaceRoot() {
		return getWorkspace().getRoot();
	}

	/*
	 * Validate the destination file if it is read-only and additionally the
	 * source file if both are read-only. Returns true if both files could be
	 * made writeable.
	 */
	private static boolean validateEdit(IFile source, IFile destination, Shell shell) {
		if (destination.isReadOnly()) {
			IWorkspace workspace = WorkspaceUndoUtil.getWorkspace();
			IStatus status;
			if (source.isReadOnly()) {
				status = workspace.validateEdit(new IFile[] { source, destination }, shell);
			} else {
				status = workspace.validateEdit(new IFile[] { destination }, shell);
			}
			return status.isOK();
		}
		return true;
	}

	/**
	 * Return the shell described by the specified adaptable, or the active
	 * shell if no shell has been specified in the adaptable.
	 *
	 * @param uiInfo
	 *            the IAdaptable (or <code>null</code>) provided by the caller
	 *            in order to supply UI information for prompting the user if
	 *            necessary. When this parameter is not <code>null</code>, it
	 *            contains an adapter for the
	 *            org.eclipse.swt.widgets.Shell.class
	 * @return the Shell that can be used to show information
	 */
	public static Shell getShell(IAdaptable uiInfo) {
		if (uiInfo != null) {
			Shell shell = uiInfo.getAdapter(Shell.class);
			if (shell != null) {
				return shell;
			}
		}
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}
}
