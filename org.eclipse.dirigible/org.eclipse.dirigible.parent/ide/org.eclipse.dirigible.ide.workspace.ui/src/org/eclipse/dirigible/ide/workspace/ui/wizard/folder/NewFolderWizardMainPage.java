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

package org.eclipse.dirigible.ide.workspace.ui.wizard.folder;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.dirigible.ide.workspace.ui.shared.FocusableWizardPage;
import org.eclipse.dirigible.ide.workspace.ui.shared.IValidationStatus;
import org.eclipse.dirigible.ide.workspace.ui.viewer.WorkspaceViewer;

public class NewFolderWizardMainPage extends FocusableWizardPage {

	private static final long serialVersionUID = 2122979040935623152L;

	private static final String FOLDER_NAME = Messages.NewFolderWizardMainPage_FOLDER_NAME;

	private static final String PARENT_LOCATION = Messages.NewFolderWizardMainPage_PARENT_LOCATION;

	private static final String PAGE_NAME = "Main Page"; //$NON-NLS-1$

	private static final String PAGE_TITLE = Messages.NewFolderWizardMainPage_PAGE_TITLE;

	private static final String PAGE_DESCRIPTION = Messages.NewFolderWizardMainPage_PAGE_DESCRIPTION;

	private Text folderNameField = null;

	private Text parentLocationField = null;

	private WorkspaceViewer workspaceViewer = null;

	private NewFolderWizardModel model;

	public NewFolderWizardMainPage(NewFolderWizardModel model) {
		super(PAGE_NAME);
		setTitle(PAGE_TITLE);
		setDescription(PAGE_DESCRIPTION);
		this.model = model;
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout(1, false));

		Label parentLocationLabel = new Label(composite, SWT.NONE);
		parentLocationLabel.setText(PARENT_LOCATION);
		parentLocationLabel.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM,
				false, false));

		parentLocationField = new Text(composite, SWT.BORDER);
		parentLocationField.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, false));
		parentLocationField.addModifyListener(new ModifyListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -7855934332806569553L;

			public void modifyText(ModifyEvent event) {
				onParentLocationChanged(parentLocationField.getText());
			}
		});

		workspaceViewer = new WorkspaceViewer(composite, SWT.BORDER
				| SWT.SINGLE);
		workspaceViewer.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		workspaceViewer.getViewer().addSelectionChangedListener(
				new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						onWorkspaceViewerSelection((IStructuredSelection) event
								.getSelection());
					}
				});

		Label folderNameLabel = new Label(composite, SWT.NONE);
		folderNameLabel.setText(FOLDER_NAME);
		folderNameLabel.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false,
				false));

		folderNameField = new Text(composite, SWT.BORDER);
		folderNameField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));
		folderNameField.addModifyListener(new ModifyListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 6720113124205697812L;

			public void modifyText(ModifyEvent event) {
				onFolderNameChanged(folderNameField.getText());
			}
		});
		setFocusable(folderNameField);

		initialize();
	}

	private void onWorkspaceViewerSelection(IStructuredSelection selection) {
		Object element = selection.getFirstElement();
		if (element instanceof IContainer) {
			onWorkspaceContainerSelected((IContainer) element);
		}
	}

	public void setFolderName(String name) {
		if (folderNameField == null || folderNameField.isDisposed()) {
			return;
		}
		if (!areEqual(folderNameField.getText(), name)) {
			folderNameField.setText(name);
		}
	}

	public void setParentLocation(String location) {
		if (parentLocationField == null || parentLocationField.isDisposed()) {
			return;
		}
		if (!areEqual(parentLocationField.getText(), location)) {
			parentLocationField.setText(location);
		}
	}

	private static boolean areEqual(Object a, Object b) {
		if (a == null && b == null) {
			return true;
		}
		if (a != null) {
			return a.equals(b);
		} else {
			return b.equals(a);
		}
	}

	public void setWarningMessage(String message) {
		setMessage(message, WARNING);
	}

	public void setCanFinish(boolean value) {
		setPageComplete(value);
	}

	public void initialize() {
		this.setFolderName(model.getFolderName());
		this.setParentLocation(model.getParentLocation());
		revalidateModel();
	}

	public void onFolderNameChanged(String folderName) {
		model.setFolderName(folderName);
		revalidateModel();
	}

	public void onParentLocationChanged(String parentLocation) {
		model.setParentLocation(parentLocation);
		revalidateModel();
	}

	public void onWorkspaceContainerSelected(IContainer container) {
		IPath containerPath = container.getFullPath();
		model.setParentLocation(containerPath.toString());
		this.setParentLocation(containerPath.toString());
		revalidateModel();
	}

	private void revalidateModel() {
		IValidationStatus status = model.validate();
		if (status.hasErrors()) {
			this.setErrorMessage(status.getMessage());
			this.setWarningMessage(null);
			this.setCanFinish(false);
		} else if (status.hasWarnings()) {
			this.setErrorMessage(null);
			this.setWarningMessage(status.getMessage());
			this.setCanFinish(true);
		} else {
			this.setErrorMessage(null);
			this.setWarningMessage(null);
			this.setCanFinish(true);
		}
	}
}
