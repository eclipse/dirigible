/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;

import com.ibm.icu.text.MessageFormat;

/**
 * The ReadOnlyStateChecker is a helper class that takes a set of resource some
 * of which may be read only and queries the user as to whether or not they wish
 * to continue the operation on it.
 */
public class ReadOnlyStateChecker {
	private Shell shell;

	private String titleMessage;

	private String mainMessage;

	private boolean yesToAllSelected = false;

	private boolean cancelSelected = false;

	private boolean ignoreLinkedResources = false;

	private String READ_ONLY_EXCEPTION_MESSAGE = IDEWorkbenchMessages.ReadOnlyCheck_problems;

	/**
	 * Create a new checker that parents the dialog off of parent using the
	 * supplied title and message.
	 * 
	 * @param parent
	 *            the shell used for dialogs
	 * @param title
	 *            the title for dialogs
	 * @param message
	 *            the message for a dialog - this will be prefaced with the name
	 *            of the resource.
	 */
	public ReadOnlyStateChecker(Shell parent, String title, String message) {
		this.shell = parent;
		this.titleMessage = title;
		this.mainMessage = message;
	}

	/**
	 * Check an individual resource to see if it passed the read only query. If
	 * it is a file just add it, otherwise it is a container and the children
	 * need to be checked too. Return true if all items are selected and false
	 * if any are skipped.
	 */
	private boolean checkAcceptedResource(IResource resourceToCheck,
			List<IResource> selectedChildren) throws CoreException {

		if (resourceToCheck.getType() == IResource.FILE) {
			selectedChildren.add(resourceToCheck);
		} else if (getIgnoreLinkedResources() && resourceToCheck.isLinked()) {
			selectedChildren.add(resourceToCheck);
		} else {
			IContainer container = (IContainer) resourceToCheck;
			// if the project is closed, there's no point in checking
			// it's children. bug 99858
			if (container.isAccessible()) {
				// Now check below
				int childCheck = checkReadOnlyResources(container.members(),
						selectedChildren);
				// Add in the resource only if nothing was left out
				if (childCheck == IDialogConstants.YES_TO_ALL_ID) {
					selectedChildren.add(resourceToCheck);
				} else {
					// Something was left out - return false
					return false;
				}
			} else {
				selectedChildren.add(resourceToCheck);
			}
		}
		return true;

	}

	/**
	 * Check the supplied resources to see if they are read only. If so then
	 * prompt the user to see if they can be deleted.Return those that were
	 * accepted.
	 * 
	 * @param itemsToCheck
	 * @return the resulting selected resources
	 */
	public IResource[] checkReadOnlyResources(IResource[] itemsToCheck) {

		List<IResource> selections = new ArrayList<IResource>();
		int result = IDialogConstants.CANCEL_ID;
		try {
			result = checkReadOnlyResources(itemsToCheck, selections);
		} catch (final CoreException exception) {
			shell.getDisplay().syncExec(new Runnable() {
				public void run() {
					ErrorDialog.openError(shell, READ_ONLY_EXCEPTION_MESSAGE,
							null, exception.getStatus());
				}
			});
		}

		if (result == IDialogConstants.CANCEL_ID) {
			return new IResource[0];
		}

		// All were selected so return the original items
		if (result == IDialogConstants.YES_TO_ALL_ID) {
			return itemsToCheck;
		}

		IResource[] returnValue = new IResource[selections.size()];
		selections.toArray(returnValue);
		return returnValue;
	}

	/**
	 * Check the children of the container to see if they are read only.
	 * 
	 * @return int one of YES_TO_ALL_ID - all elements were selected NO_ID - No
	 *         was hit at some point CANCEL_ID - cancel was hit
	 * @param itemsToCheck
	 *            IResource[]
	 * @param allSelected
	 *            the List of currently selected resources to add to.
	 */
	private int checkReadOnlyResources(IResource[] itemsToCheck,
			List<IResource> allSelected) throws CoreException {

		// Shortcut. If the user has already selected yes to all then just
		// return it
		if (yesToAllSelected) {
			return IDialogConstants.YES_TO_ALL_ID;
		}

		boolean noneSkipped = true;
		List<IResource> selectedChildren = new ArrayList<IResource>();

		for (int i = 0; i < itemsToCheck.length; i++) {
			IResource resourceToCheck = itemsToCheck[i];
			ResourceAttributes checkAttributes = resourceToCheck
					.getResourceAttributes();
			if (!yesToAllSelected && shouldCheck(resourceToCheck)
					&& checkAttributes != null && checkAttributes.isReadOnly()) {
				int action = queryYesToAllNoCancel(resourceToCheck);
				if (action == IDialogConstants.YES_ID) {
					boolean childResult = checkAcceptedResource(
							resourceToCheck, selectedChildren);
					if (!childResult) {
						noneSkipped = false;
					}
				}
				if (action == IDialogConstants.NO_ID) {
					noneSkipped = false;
				}
				if (action == IDialogConstants.CANCEL_ID) {
					cancelSelected = true;
					return IDialogConstants.CANCEL_ID;
				}
				if (action == IDialogConstants.YES_TO_ALL_ID) {
					yesToAllSelected = true;
					selectedChildren.add(resourceToCheck);
				}
			} else {
				boolean childResult = checkAcceptedResource(resourceToCheck,
						selectedChildren);
				if (cancelSelected) {
					return IDialogConstants.CANCEL_ID;
				}
				if (!childResult) {
					noneSkipped = false;
				}
			}

		}

		if (noneSkipped) {
			return IDialogConstants.YES_TO_ALL_ID;
		}
		allSelected.addAll(selectedChildren);
		return IDialogConstants.NO_ID;

	}

	/**
	 * Returns whether the given resource should be checked for read-only state.
	 * 
	 * @param resourceToCheck
	 *            the resource to check
	 * @return <code>true</code> to check it, <code>false</code> to skip it
	 */
	private boolean shouldCheck(IResource resourceToCheck) {
		if (ignoreLinkedResources) {
			if (resourceToCheck.isLinked()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Open a message dialog with Yes No, Yes To All and Cancel buttons. Return
	 * the code that indicates the selection.
	 * 
	 * @return int one of YES_TO_ALL_ID YES_ID NO_ID CANCEL_ID
	 * 
	 * @param resource
	 *            - the resource being queried.
	 */
	private int queryYesToAllNoCancel(IResource resource) {

		final MessageDialog dialog = new MessageDialog(this.shell,
				this.titleMessage, null, MessageFormat.format(this.mainMessage,
						new Object[] { resource.getName() }),
				MessageDialog.QUESTION, new String[] {
						IDialogConstants.get().YES_LABEL,
						IDialogConstants.get().YES_TO_ALL_LABEL,
						IDialogConstants.get().NO_LABEL,
						IDialogConstants.get().CANCEL_LABEL }, 0);
		shell.getDisplay().syncExec(new Runnable() {
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

	/**
	 * Returns whether to ignore linked resources.
	 * 
	 * @return <code>true</code> to ignore linked resources, <code>false</code>
	 *         to consider them
	 * @since 3.1
	 */
	public boolean getIgnoreLinkedResources() {
		return ignoreLinkedResources;
	}

	/**
	 * Sets whether to ignore linked resources. The default is
	 * <code>false</code>.
	 * 
	 * @param ignore
	 *            <code>true</code> to ignore linked resources,
	 *            <code>false</code> to consider them
	 * @since 3.1
	 */
	public void setIgnoreLinkedResources(boolean ignore) {
		ignoreLinkedResources = ignore;
	}
}
