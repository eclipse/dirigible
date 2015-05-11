/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.ide.undo;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.mapping.IResourceChangeDescriptionFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * A CreateResourcesOperation represents an undoable operation for creating
 * resources in the workspace. Clients may call the public API from a background
 * thread.
 * 
 * This class is not intended to be subclassed by clients.
 * 
 * @since 3.3
 * 
 */
abstract class AbstractCreateResourcesOperation extends
		AbstractResourcesOperation {

	/**
	 * Create an AbstractCreateResourcesOperation.
	 * 
	 * @param resourceDescriptions
	 *            the resourceDescriptions describing resources to be created
	 * @param label
	 *            the label of the operation
	 */
	AbstractCreateResourcesOperation(
			ResourceDescription[] resourceDescriptions, String label) {
		super(resourceDescriptions, label);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * This implementation creates resources from the known resource
	 * descriptions.
	 * 
	 * @see
	 * org.eclipse.ui.ide.undo.AbstractWorkspaceOperation#doExecute(org.eclipse
	 * .core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
	protected void doExecute(IProgressMonitor monitor, IAdaptable uiInfo)
			throws CoreException {
		recreate(monitor, uiInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * This implementation deletes resources.
	 * 
	 * @see
	 * org.eclipse.ui.ide.undo.AbstractWorkspaceOperation#doUndo(org.eclipse
	 * .core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
	protected void doUndo(IProgressMonitor monitor, IAdaptable uiInfo)
			throws CoreException {
		delete(monitor, uiInfo, false); // never delete content
	}

	/*
	 * (non-Javadoc)
	 * 
	 * This implementation documents the impending create or delete.
	 * 
	 * @see org.eclipse.ui.ide.undo.AbstractWorkspaceOperation#
	 * updateResourceChangeDescriptionFactory
	 * (org.eclipse.core.resources.mapping.IResourceChangeDescriptionFactory,
	 * int)
	 */
	protected boolean updateResourceChangeDescriptionFactory(
			IResourceChangeDescriptionFactory factory, int operation) {
		boolean modified = false;
		if (operation == UNDO) {
			for (int i = 0; i < resources.length; i++) {
				IResource resource = resources[i];
				factory.delete(resource);
				modified = true;
			}
		} else {
			for (int i = 0; i < resourceDescriptions.length; i++) {
				if (resourceDescriptions[i] != null) {
					IResource resource = resourceDescriptions[i]
							.createResourceHandle();
					factory.create(resource);
					modified = true;
				}
			}
		}
		return modified;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.ide.undo.AbstractWorkspaceOperation#getExecuteSchedulingRule
	 * ()
	 */
	protected ISchedulingRule getExecuteSchedulingRule() {
		return super.computeCreateSchedulingRule();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.ide.undo.AbstractWorkspaceOperation#getUndoSchedulingRule
	 * ()
	 */
	protected ISchedulingRule getUndoSchedulingRule() {
		return super.computeDeleteSchedulingRule();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * This implementation computes the status for creating resources.
	 * 
	 * @see
	 * org.eclipse.ui.ide.undo.AbstractWorkspaceOperation#computeExecutionStatus
	 * (org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus computeExecutionStatus(IProgressMonitor monitor) {
		IStatus status = super.computeExecutionStatus(monitor);
		if (status.isOK()) {
			status = computeCreateStatus(true);
		}
		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * This implementation computes the status for deleting resources.
	 * 
	 * @see
	 * org.eclipse.ui.ide.undo.AbstractWorkspaceOperation#computeUndoableStatus
	 * (org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus computeUndoableStatus(IProgressMonitor monitor) {
		IStatus status = super.computeUndoableStatus(monitor);
		if (status.isOK()) {
			status = computeDeleteStatus();
		}
		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * This implementation computes the status for creating resources.
	 * 
	 * @see
	 * org.eclipse.ui.ide.undo.AbstractWorkspaceOperation#computeRedoableStatus
	 * (org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus computeRedoableStatus(IProgressMonitor monitor) {
		IStatus status = super.computeRedoableStatus(monitor);
		if (status.isOK()) {
			status = computeCreateStatus(true);
		}
		return status;
	}
}
