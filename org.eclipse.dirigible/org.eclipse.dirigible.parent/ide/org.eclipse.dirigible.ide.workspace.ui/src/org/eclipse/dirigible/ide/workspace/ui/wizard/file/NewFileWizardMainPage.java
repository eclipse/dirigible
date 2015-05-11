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

package org.eclipse.dirigible.ide.workspace.ui.wizard.file;

import org.eclipse.core.resources.IContainer;
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

public class NewFileWizardMainPage extends FocusableWizardPage {

	private static final long serialVersionUID = 9151517663731999150L;

	private static final String FILE_NAME = Messages.NewFileWizardMainPage_FILE_NAME;

	private static final String PARENT_LOCATION = Messages.NewFileWizardMainPage_PARENT_LOCATION;

	private static final String PAGE_NAME = "Main Page"; //$NON-NLS-1$

	private static final String PAGE_TITLE = Messages.NewFileWizardMainPage_PAGE_TITLE;

	private static final String PAGE_DESCRIPTION = Messages.NewFileWizardMainPage_PAGE_DESCRIPTION;

	private Text parentLocationField = null;

	private Text fileNameField = null;

	private WorkspaceViewer workspaceViewer = null;

	private NewFileWizardModel model = null;

	public NewFileWizardMainPage(NewFileWizardModel model) {
		super(PAGE_NAME);
		setTitle(PAGE_TITLE);
		setDescription(PAGE_DESCRIPTION);
		this.model = model;
	}

	@Override
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
			private static final long serialVersionUID = -7765618126345654222L;

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
						onWorkspaceViewerSelected((IStructuredSelection) event
								.getSelection());
					}
				});

		final Label fileNameLabel = new Label(composite, SWT.NONE);
		fileNameLabel.setText(FILE_NAME);
		fileNameLabel.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false,
				false));

		fileNameField = new Text(composite, SWT.BORDER);
		fileNameField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));
		fileNameField.addModifyListener(new ModifyListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 6513005442028570556L;

			public void modifyText(ModifyEvent event) {
				onFileNameChanged(fileNameField.getText());
			}
		});

		setFocusable(fileNameField);
		initialize();
	}
	
	private void onWorkspaceViewerSelected(IStructuredSelection selection) {
		Object element = selection.getFirstElement();
		if (element instanceof IContainer) {
			onWorkspaceContainerSelected((IContainer) element);
		}
	}

	public void setFileName(String name) {
		if (fileNameField == null || fileNameField.isDisposed()) {
			return;
		}
		if (!areEqual(fileNameField.getText(), name)) {
			fileNameField.setText(name);
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

	@Override
	public void dispose() {
		workspaceViewer.dispose();
		parentLocationField = null;
		fileNameField = null;
		workspaceViewer = null;
		super.dispose();
	}

	public void initialize() {
		this.setFileName(model.getFileName());
		this.setParentLocation(model.getParentLocation());
		revalidate();
	}

	public void onFileNameChanged(String fileName) {
		model.setFileName(fileName);
		revalidate();
	}

	public void onParentLocationChanged(String parentLocation) {
		model.setParentLocation(parentLocation);
		revalidate();
	}

	public void onWorkspaceContainerSelected(IContainer container) {
		final String location = container.getFullPath().toString();
		model.setParentLocation(location);
		this.setParentLocation(location);
		revalidate();
	}

	private void revalidate() {
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
