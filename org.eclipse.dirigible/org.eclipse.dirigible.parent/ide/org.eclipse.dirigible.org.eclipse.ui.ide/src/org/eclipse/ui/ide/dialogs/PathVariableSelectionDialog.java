/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.ide.dialogs;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.eclipse.ui.internal.ide.dialogs.FileFolderSelectionDialog;
import org.eclipse.ui.internal.ide.dialogs.IDEResourceInfoUtils;
import org.eclipse.ui.internal.ide.dialogs.PathVariablesGroup;

/**
 * A selection dialog which shows the path variables defined in the workspace.
 * The <code>getResult</code> method returns the name(s) of the selected path
 * variable(s).
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * <p>
 * Example:
 * 
 * <pre>
 * PathVariableSelectionDialog dialog = new PathVariableSelectionDialog(
 * 		getShell(), IResource.FOLDER);
 * dialog.open();
 * String[] result = (String[]) dialog.getResult();
 * </pre>
 * 
 * </p>
 * 
 * @since 3.1
 */
public final class PathVariableSelectionDialog extends SelectionDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -876325090901428145L;

	private static final int EXTEND_ID = IDialogConstants.CLIENT_ID + 1;

	private PathVariablesGroup pathVariablesGroup;

	private int variableType;

	/**
	 * Creates a path variable selection dialog.
	 * 
	 * @param parentShell
	 *            the parent shell
	 * @param variableType
	 *            the type of variables that are displayed in this dialog.
	 *            <code>IResource.FILE</code> and/or
	 *            <code>IResource.FOLDER</code> logically ORed together.
	 */
	public PathVariableSelectionDialog(Shell parentShell, int variableType) {
		super(parentShell);
		setTitle(IDEWorkbenchMessages.PathVariableSelectionDialog_title);
		this.variableType = variableType;
		pathVariablesGroup = new PathVariablesGroup(false, variableType,
				new Listener() {
					/**
					 * 
					 */
					private static final long serialVersionUID = -1769981136518953196L;

					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse
					 * .swt.widgets.Event)
					 */
					public void handleEvent(Event event) {
						updateExtendButtonState();
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == EXTEND_ID) {
			FileFolderSelectionDialog dialog = new FileFolderSelectionDialog(
					getShell(), false, variableType);
			PathVariablesGroup.PathVariableElement selection = pathVariablesGroup
					.getSelection()[0];
			dialog.setTitle(IDEWorkbenchMessages.PathVariableSelectionDialog_ExtensionDialog_title);
			dialog.setMessage(NLS
					.bind(IDEWorkbenchMessages.PathVariableSelectionDialog_ExtensionDialog_description,
							selection.name));
			// XXX This only works for variables that refer to local file system
			// locations
			try {
				dialog.setInput(EFS.getStore(URIUtil.toURI(selection.path)));
			} catch (CoreException e) {
				ErrorDialog.openError(getShell(), null, null, e.getStatus());
			}
			if (dialog.open() == Window.OK && pathVariablesGroup.performOk()) {
				setExtensionResult(selection,
						(IFileStore) dialog.getResult()[0]);
				super.okPressed();
			}
		} else {
			super.buttonPressed(buttonId);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets
	 * .Shell)
	 */
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		PlatformUI
				.getWorkbench()
				.getHelpSystem()
				.setHelp(shell,
						IIDEHelpContextIds.PATH_VARIABLE_SELECTION_DIALOG);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse
	 * .swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.get().OK_LABEL, true);
		createButton(parent, EXTEND_ID,
				IDEWorkbenchMessages.PathVariableSelectionDialog_extendButton,
				false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.get().CANCEL_LABEL, false);
		updateExtendButtonState();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets
	 * .Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		// create composite
		Composite dialogArea = (Composite) super.createDialogArea(parent);

		pathVariablesGroup.createContents(dialogArea);
		return dialogArea;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#close()
	 */
	public boolean close() {
		pathVariablesGroup.dispose();
		return super.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		// Sets the dialog result to the selected path variable name(s).
		if (pathVariablesGroup.performOk()) {
			PathVariablesGroup.PathVariableElement[] selection = pathVariablesGroup
					.getSelection();
			String[] variableNames = new String[selection.length];

			for (int i = 0; i < selection.length; i++) {
				variableNames[i] = selection[i].name;
			}
			setSelectionResult(variableNames);
		} else {
			setSelectionResult(null);
		}
		super.okPressed();
	}

	/**
	 * Sets the dialog result to the concatenated variable name and extension.
	 * 
	 * @param variable
	 *            variable selected in the variables list and extended by
	 *            <code>extensionFile</code>
	 * @param extensionFile
	 *            file selected to extend the variable.
	 */
	private void setExtensionResult(
			PathVariablesGroup.PathVariableElement variable,
			IFileStore extensionFile) {
		IPath extensionPath = new Path(extensionFile.toString());
		int matchCount = extensionPath.matchingFirstSegments(variable.path);
		IPath resultPath = new Path(variable.name);

		extensionPath = extensionPath.removeFirstSegments(matchCount);
		resultPath = resultPath.append(extensionPath);
		setSelectionResult(new String[] { resultPath.toOSString() });
	}

	/**
	 * Updates the enabled state of the Extend button based on the current
	 * variable selection.
	 */
	private void updateExtendButtonState() {
		PathVariablesGroup.PathVariableElement[] selection = pathVariablesGroup
				.getSelection();
		Button extendButton = getButton(EXTEND_ID);

		if (extendButton == null) {
			return;
		}
		if (selection.length == 1) {
			IFileInfo info = IDEResourceInfoUtils
					.getFileInfo(selection[0].path);
			if (info.exists() && info.isDirectory()) {
				extendButton.setEnabled(true);
			} else {
				extendButton.setEnabled(false);
			}

		} else {
			extendButton.setEnabled(false);
		}
	}

}
