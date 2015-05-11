/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation 
 *    Bob Foster <bob@objfac.com>
 *     - Fix for bug 23025 - SaveAsDialog should not assume what is being saved is an IFile
 *    Benjamin Muskalla <b.muskalla@gmx.net>
 *     - Fix for bug 82541 - [Dialogs] SaveAsDialog should better handle closed projects
 *******************************************************************************/
package org.eclipse.ui.dialogs;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.IDEInternalWorkbenchImages;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.eclipse.ui.internal.ide.misc.ResourceAndContainerGroup;

import org.eclipse.dirigible.ide.workspace.RemoteResourcesPlugin;

/**
 * A standard "Save As" dialog which solicits a path from the user. The
 * <code>getResult</code> method returns the path. Note that the folder at the
 * specified path might not exist and might need to be created.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @see org.eclipse.ui.dialogs.ContainerGenerator
 * @noextend This class is not intended to be subclassed by clients.
 */
public class SaveAsDialog extends TitleAreaDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7374646817555995704L;

	private static final String DIALOG_SETTINGS_SECTION = "SaveAsDialogSettings"; //$NON-NLS-1$

	private IFile originalFile = null;

	private String originalName = null;

	private IPath result;

	// widgets
	private ResourceAndContainerGroup resourceGroup;

	private Button okButton;

	/**
	 * Image for title area
	 */
	private Image dlgTitleImage = null;

	/**
	 * Creates a new Save As dialog for no specific file.
	 * 
	 * @param parentShell
	 *            the parent shell
	 */
	public SaveAsDialog(Shell parentShell) {
		super(parentShell);
	}

	/*
	 * (non-Javadoc) Method declared in Window.
	 */
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(IDEWorkbenchMessages.SaveAsDialog_text);
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(shell, IIDEHelpContextIds.SAVE_AS_DIALOG);
	}

	/*
	 * (non-Javadoc) Method declared in Window.
	 */
	protected Control createContents(Composite parent) {

		Control contents = super.createContents(parent);

		initializeControls();
		validatePage();
		resourceGroup.setFocus();
		setTitle(IDEWorkbenchMessages.SaveAsDialog_title);
		dlgTitleImage = IDEInternalWorkbenchImages.getImageDescriptor(
				IDEInternalWorkbenchImages.IMG_DLGBAN_SAVEAS_DLG).createImage();
		setTitleImage(dlgTitleImage);
		setMessage(IDEWorkbenchMessages.SaveAsDialog_message);

		return contents;
	}

	/**
	 * The <code>SaveAsDialog</code> implementation of this <code>Window</code>
	 * method disposes of the banner image when the dialog is closed.
	 */
	public boolean close() {
		if (dlgTitleImage != null) {
			dlgTitleImage.dispose();
		}
		return super.close();
	}

	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		okButton = createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.get().OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.get().CANCEL_LABEL, false);
	}

	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected Control createDialogArea(Composite parent) {
		// top level composite
		Composite parentComposite = (Composite) super.createDialogArea(parent);

		// create a composite with standard margins and spacing
		Composite composite = new Composite(parentComposite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parentComposite.getFont());

		Listener listener = new Listener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -7200838228253756504L;

			public void handleEvent(Event event) {
				setDialogComplete(validatePage());
			}
		};

		resourceGroup = new ResourceAndContainerGroup(composite, listener,
				IDEWorkbenchMessages.SaveAsDialog_fileLabel,
				IDEWorkbenchMessages.SaveAsDialog_file);
		resourceGroup.setAllowExistingResources(true);

		return parentComposite;
	}

	/**
	 * Returns the full path entered by the user.
	 * <p>
	 * Note that the file and container might not exist and would need to be
	 * created. See the <code>IFile.create</code> method and the
	 * <code>ContainerGenerator</code> class.
	 * </p>
	 * 
	 * @return the path, or <code>null</code> if Cancel was pressed
	 */
	public IPath getResult() {
		return result;
	}

	/**
	 * Initializes the controls of this dialog.
	 */
	private void initializeControls() {
		if (originalFile != null) {
			resourceGroup.setContainerFullPath(originalFile.getParent()
					.getFullPath());
			resourceGroup.setResource(originalFile.getName());
		} else if (originalName != null) {
			resourceGroup.setResource(originalName);
		}
		setDialogComplete(validatePage());
	}

	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected void okPressed() {
		// Get new path.
		IPath path = resourceGroup.getContainerFullPath().append(
				resourceGroup.getResource());

		// If the user does not supply a file extension and if the save
		// as dialog was provided a default file name append the extension
		// of the default filename to the new name
		if (path.getFileExtension() == null) {
			if (originalFile != null && originalFile.getFileExtension() != null) {
				path = path.addFileExtension(originalFile.getFileExtension());
			} else if (originalName != null) {
				int pos = originalName.lastIndexOf('.');
				if (++pos > 0 && pos < originalName.length()) {
					path = path.addFileExtension(originalName.substring(pos));
				}
			}
		}

		// If the path already exists then confirm overwrite.
		IFile file = RemoteResourcesPlugin.getWorkspace().getRoot()
				.getFile(path);
		if (file.exists()) {
			String[] buttons = new String[] { IDialogConstants.get().YES_LABEL,
					IDialogConstants.get().NO_LABEL,
					IDialogConstants.get().CANCEL_LABEL };
			String question = NLS.bind(
					IDEWorkbenchMessages.SaveAsDialog_overwriteQuestion,
					path.toString());
			MessageDialog d = new MessageDialog(getShell(),
					IDEWorkbenchMessages.Question, null, question,
					MessageDialog.QUESTION, buttons, 0);
			int overwrite = d.open();
			switch (overwrite) {
			case 0: // Yes
				break;
			case 1: // No
				return;
			case 2: // Cancel
			default:
				cancelPressed();
				return;
			}
		}

		// Store path and close.
		result = path;
		close();
	}

	/**
	 * Sets the completion state of this dialog and adjusts the enable state of
	 * the Ok button accordingly.
	 * 
	 * @param value
	 *            <code>true</code> if this dialog is compelete, and
	 *            <code>false</code> otherwise
	 */
	protected void setDialogComplete(boolean value) {
		okButton.setEnabled(value);
	}

	/**
	 * Sets the original file to use.
	 * 
	 * @param originalFile
	 *            the original file
	 */
	public void setOriginalFile(IFile originalFile) {
		this.originalFile = originalFile;
	}

	/**
	 * Set the original file name to use. Used instead of
	 * <code>setOriginalFile</code> when the original resource is not an IFile.
	 * Must be called before <code>create</code>.
	 * 
	 * @param originalName
	 *            default file name
	 */
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	/**
	 * Returns whether this page's visual components all contain valid values.
	 * 
	 * @return <code>true</code> if valid, and <code>false</code> otherwise
	 */
	private boolean validatePage() {
		if (!resourceGroup.areAllValuesValid()) {
			if (!resourceGroup.getResource().equals("")) { //$NON-NLS-1$
				setErrorMessage(resourceGroup.getProblemMessage());
			} else {
				setErrorMessage(null);
			}
			return false;
		}

		String resourceName = resourceGroup.getResource();
		IWorkspace workspace = RemoteResourcesPlugin.getWorkspace();

		// Do not allow a closed project to be selected
		IPath fullPath = resourceGroup.getContainerFullPath();
		if (fullPath != null) {
			String projectName = fullPath.segment(0);
			IStatus isValidProjectName = workspace.validateName(projectName,
					IResource.PROJECT);
			if (isValidProjectName.isOK()) {
				IProject project = workspace.getRoot().getProject(projectName);
				if (!project.isOpen()) {
					setErrorMessage(IDEWorkbenchMessages.SaveAsDialog_closedProjectMessage);
					return false;
				}
			}
		}

		IStatus result = workspace.validateName(resourceName, IResource.FILE);
		if (!result.isOK()) {
			setErrorMessage(result.getMessage());
			return false;
		}

		setErrorMessage(null);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Dialog#getDialogBoundsSettings()
	 * 
	 * @since 3.2
	 */
	protected IDialogSettings getDialogBoundsSettings() {
		IDialogSettings settings = IDEWorkbenchPlugin.getDefault()
				.getDialogSettings();
		IDialogSettings section = settings.getSection(DIALOG_SETTINGS_SECTION);
		if (section == null) {
			section = settings.addNewSection(DIALOG_SETTINGS_SECTION);
		}
		return section;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#isResizable()
	 */
	protected boolean isResizable() {
		return true;
	}
}
