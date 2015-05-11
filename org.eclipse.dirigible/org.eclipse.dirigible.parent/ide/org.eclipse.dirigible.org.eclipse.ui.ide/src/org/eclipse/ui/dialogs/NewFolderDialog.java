/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Sebastian Davids <sdavids@gmx.de> - Fix for bug 19346 - Dialog
 *     font should be activated and used by other components.
 *******************************************************************************/

package org.eclipse.ui.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNatureDescriptor;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.eclipse.ui.internal.ide.dialogs.CreateLinkedResourceGroup;

/**
 * The NewFolderDialog is used to create a new folder. The folder can optionally
 * be linked to a file system folder.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @noextend This class is not intended to be subclassed by clients.
 */
@SuppressWarnings("deprecation")
public class NewFolderDialog extends SelectionStatusDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6954485900225325123L;

	private static final String EMPTY_STRING = "";

	// widgets
	private Text folderNameField;

	private Button advancedButton;

	private CreateLinkedResourceGroup linkedResourceGroup;

	private IContainer container;

	private boolean firstLinkCheck = true;

	/**
	 * Parent composite of the advanced widget group for creating linked
	 * resources.
	 */
	private Composite linkedResourceParent;

	/**
	 * Linked resources widget group. Null if advanced section is not visible.
	 */
	private Composite linkedResourceComposite;

	/**
	 * Height of the dialog without the "advanced" linked resource group. Set
	 * when the advanced group is first made visible.
	 */
	private int basicShellHeight = -1;

	/**
	 * Creates a NewFolderDialog
	 * 
	 * @param parentShell
	 *            parent of the new dialog
	 * @param container
	 *            parent of the new folder
	 */
	public NewFolderDialog(Shell parentShell, IContainer container) {
		super(parentShell);
		this.container = container;
		setTitle(IDEWorkbenchMessages.NewFolderDialog_title);
		setStatusLineAboveButtons(true);
	}

	/**
	 * Creates the folder using the name and link target entered by the user.
	 * Sets the dialog result to the created folder.
	 */
	protected void computeResult() {
		// Do nothing here as we
		// need to know the result
	}

	/*
	 * (non-Javadoc) Method declared in Window.
	 */
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(shell, IIDEHelpContextIds.NEW_FOLDER_DIALOG);
	}

	/**
	 * @see org.eclipse.jface.window.Window#create()
	 */
	public void create() {
		super.create();
		// initially disable the ok button since we don't preset the
		// folder name field
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	/**
	 * Creates the widget for advanced options.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	protected void createAdvancedControls(Composite parent) {
		Preferences preferences = ResourcesPlugin.getPlugin()
				.getPluginPreferences();

		if (preferences.getBoolean(ResourcesPlugin.PREF_DISABLE_LINKING) == false
				&& isValidContainer()) {
			linkedResourceParent = new Composite(parent, SWT.NONE);
			linkedResourceParent.setFont(parent.getFont());
			linkedResourceParent.setLayoutData(new GridData(
					GridData.FILL_HORIZONTAL));
			GridLayout layout = new GridLayout();
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			linkedResourceParent.setLayout(layout);

			advancedButton = new Button(linkedResourceParent, SWT.PUSH);
			advancedButton.setFont(linkedResourceParent.getFont());
			advancedButton.setText(IDEWorkbenchMessages.showAdvanced);
			setButtonLayoutData(advancedButton);
			GridData data = (GridData) advancedButton.getLayoutData();
			data.horizontalAlignment = GridData.BEGINNING;
			advancedButton.setLayoutData(data);
			advancedButton.addSelectionListener(new SelectionAdapter() {
				/**
				 * 
				 */
				private static final long serialVersionUID = -3919590128155903940L;

				public void widgetSelected(SelectionEvent e) {
					handleAdvancedButtonSelect();
				}
			});
		}
		linkedResourceGroup = new CreateLinkedResourceGroup(IResource.FOLDER,
				new Listener() {
					/**
					 * 
					 */
					private static final long serialVersionUID = -7851376831063794410L;

					public void handleEvent(Event e) {
						validateLinkedResource();
						firstLinkCheck = false;
					}
				}, new CreateLinkedResourceGroup.IStringValue() {
					public void setValue(String string) {
						folderNameField.setText(string);
					}

					public String getValue() {
						return folderNameField.getText();
					}
				});
	}

	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		createFolderNameGroup(composite);
		createAdvancedControls(composite);
		return composite;
	}

	/**
	 * Creates the folder name specification controls.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createFolderNameGroup(Composite parent) {
		Font font = parent.getFont();
		// project specification group
		Composite folderGroup = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		folderGroup.setLayout(layout);
		folderGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// new folder label
		Label folderLabel = new Label(folderGroup, SWT.NONE);
		folderLabel.setFont(font);
		folderLabel.setText(IDEWorkbenchMessages.NewFolderDialog_nameLabel);

		// new folder name entry field
		folderNameField = new Text(folderGroup, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = IDialogConstants.ENTRY_FIELD_WIDTH;
		folderNameField.setLayoutData(data);
		folderNameField.setFont(font);
		folderNameField.addListener(SWT.Modify, new Listener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 8373094546243626619L;

			public void handleEvent(Event event) {
				validateLinkedResource();
			}
		});
	}

	/**
	 * Creates a folder resource handle for the folder with the given name. The
	 * folder handle is created relative to the container specified during
	 * object creation.
	 * 
	 * @param folderName
	 *            the name of the folder resource to create a handle for
	 * @return the new folder resource handle
	 */
	private IFolder createFolderHandle(String folderName) {
		IWorkspaceRoot workspaceRoot = container.getWorkspace().getRoot();
		IPath folderPath = container.getFullPath().append(folderName);
		IFolder folderHandle = workspaceRoot.getFolder(folderPath);

		return folderHandle;
	}

	/**
	 * Creates a new folder with the given name and optionally linking to the
	 * specified link target.
	 * 
	 * @param folderName
	 *            name of the new folder
	 * @param linkTarget
	 *            name of the link target folder. may be null.
	 * @return IFolder the new folder
	 */
	private IFolder createNewFolder(String folderName, final URI linkTarget) {
		final IFolder folderHandle = createFolderHandle(folderName);

		WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
			public void execute(IProgressMonitor monitor) throws CoreException {
				try {
					monitor.beginTask(
							IDEWorkbenchMessages.NewFolderDialog_progress, 2000);
					if (monitor.isCanceled()) {
						throw new OperationCanceledException();
					}
					if (linkTarget == null) {
						folderHandle.create(false, true, monitor);
					} else {
						folderHandle.createLink(linkTarget,
								IResource.ALLOW_MISSING_LOCAL, monitor);
					}
					if (monitor.isCanceled()) {
						throw new OperationCanceledException();
					}
				} finally {
					monitor.done();
				}
			}
		};
		try {
			PlatformUI.getWorkbench().getProgressService()
					.busyCursorWhile(operation);
		} catch (InterruptedException exception) {
			return null;
		} catch (InvocationTargetException exception) {
			if (exception.getTargetException() instanceof CoreException) {
				ErrorDialog.openError(getShell(),
						IDEWorkbenchMessages.NewFolderDialog_errorTitle, null, // no
																				// special
																				// message
						((CoreException) exception.getTargetException())
								.getStatus());
			} else {
				// CoreExceptions are handled above, but unexpected runtime
				// exceptions and errors may still occur.
				IDEWorkbenchPlugin.log(getClass(),
						"createNewFolder", exception.getTargetException()); //$NON-NLS-1$
				MessageDialog
						.openError(
								getShell(),
								IDEWorkbenchMessages.NewFolderDialog_errorTitle,
								NLS.bind(
										IDEWorkbenchMessages.NewFolderDialog_internalError,
										exception.getTargetException()
												.getMessage()));
			}
			return null;
		}
		return folderHandle;
	}

	/**
	 * Shows/hides the advanced option widgets.
	 */
	protected void handleAdvancedButtonSelect() {
		Shell shell = getShell();
		Point shellSize = shell.getSize();
		Composite composite = (Composite) getDialogArea();

		if (linkedResourceComposite != null) {
			linkedResourceComposite.dispose();
			linkedResourceComposite = null;
			composite.layout();
			shell.setSize(shellSize.x, basicShellHeight);
			advancedButton.setText(IDEWorkbenchMessages.showAdvanced);
		} else {
			if (basicShellHeight == -1) {
				basicShellHeight = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT,
						true).y;
			}
			linkedResourceComposite = linkedResourceGroup
					.createContents(linkedResourceParent);
			shellSize = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
			shell.setSize(shellSize);
			composite.layout();
			advancedButton.setText(IDEWorkbenchMessages.hideAdvanced);
		}
	}

	/**
	 * Returns whether the container specified in the constructor is a valid
	 * parent for creating linked resources.
	 * 
	 * @return boolean <code>true</code> if the container specified in the
	 *         constructor is a valid parent for creating linked resources.
	 *         <code>false</code> if no linked resources may be created with the
	 *         specified container as a parent.
	 */
	private boolean isValidContainer() {
		if (container.getType() != IResource.PROJECT
				&& container.getType() != IResource.FOLDER) {
			return false;
		}

		try {
			IWorkspace workspace = IDEWorkbenchPlugin.getPluginWorkspace();
			IProject project = container.getProject();
			String[] natureIds = project.getDescription().getNatureIds();

			for (int i = 0; i < natureIds.length; i++) {
				IProjectNatureDescriptor descriptor = workspace
						.getNatureDescriptor(natureIds[i]);
				if (descriptor != null
						&& descriptor.isLinkingAllowed() == false) {
					return false;
				}
			}
		} catch (CoreException exception) {
			// project does not exist or is closed
			return false;
		}
		return true;
	}

	/**
	 * Update the dialog's status line to reflect the given status. It is safe
	 * to call this method before the dialog has been opened.
	 */
	protected void updateStatus(IStatus status) {
		if (firstLinkCheck && status != null) {
			// don't show the first validation result as an error.
			// fixes bug 29659
			Status newStatus = new Status(IStatus.OK, status.getPlugin(),
					status.getCode(), status.getMessage(),
					status.getException());
			super.updateStatus(newStatus);
		} else {
			super.updateStatus(status);
		}
	}

	/**
	 * Update the dialog's status line to reflect the given status. It is safe
	 * to call this method before the dialog has been opened.
	 * 
	 * @param severity
	 * @param message
	 */
	private void updateStatus(int severity, String message) {
		updateStatus(new Status(severity, IDEWorkbenchPlugin.IDE_WORKBENCH,
				severity, message, null));
	}

	/**
	 * Checks whether the folder name and link location are valid. Disable the
	 * OK button if the folder name and link location are valid. a message that
	 * indicates the problem otherwise.
	 */
	private void validateLinkedResource() {
		boolean valid = validateFolderName();

		if (valid) {
			IFolder linkHandle = createFolderHandle(folderNameField.getText());
			IStatus status = linkedResourceGroup
					.validateLinkLocation(linkHandle);

			if (status.getSeverity() != IStatus.ERROR) {
				getOkButton().setEnabled(true);
			} else {
				getOkButton().setEnabled(false);
			}

			if (status.isOK() == false) {
				updateStatus(status);
			}
		} else {
			getOkButton().setEnabled(false);
		}
	}

	/**
	 * Checks if the folder name is valid.
	 * 
	 * @return null if the new folder name is valid. a message that indicates
	 *         the problem otherwise.
	 */
	private boolean validateFolderName() {
		String name = folderNameField.getText();
		IWorkspace workspace = container.getWorkspace();
		IStatus nameStatus = workspace.validateName(name, IResource.FOLDER);

		if (EMPTY_STRING.equals(name)) { //$NON-NLS-1$
			updateStatus(IStatus.ERROR,
					IDEWorkbenchMessages.NewFolderDialog_folderNameEmpty);
			return false;
		}
		if (nameStatus.isOK() == false) {
			updateStatus(nameStatus);
			return false;
		}
		IPath path = new Path(name);
		if (container.getFolder(path).exists()
				|| container.getFile(path).exists()) {
			updateStatus(IStatus.ERROR, NLS.bind(
					IDEWorkbenchMessages.NewFolderDialog_alreadyExists, name));
			return false;
		}
		updateStatus(IStatus.OK, EMPTY_STRING); //$NON-NLS-1$
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.dialogs.SelectionStatusDialog#okPressed()
	 */
	protected void okPressed() {
		URI linkTarget = linkedResourceGroup.getLinkTargetURI();
		IFolder folder = createNewFolder(folderNameField.getText(), linkTarget);
		if (folder == null) {
			return;
		}

		setSelectionResult(new IFolder[] { folder });

		super.okPressed();
	}
}
