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

package org.eclipse.dirigible.ide.workspace.wizard.project.create;

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

public class NewProjectWizardMainPage extends FocusableWizardPage {

	private static final long serialVersionUID = -2191030355904715681L;
	private static final String ENTER_PROJECT_NAME = Messages.NewProjectWizardMainPage_ENTER_PROJECT_NAME;
	private static final String PAGE_NAME = "Main Page"; //$NON-NLS-1$
	private static final String PAGE_TITLE = Messages.NewProjectWizardMainPage_PAGE_TITLE;
	private static final String PAGE_DESCRIPTION = Messages.NewProjectWizardMainPage_PAGE_DESCRIPTION;

	private final NewProjectWizardModel model;

	private Text projectNameField = null;
	
	public NewProjectWizardMainPage(NewProjectWizardModel model) {
		super(PAGE_NAME);
		setTitle(PAGE_TITLE);
		setDescription(PAGE_DESCRIPTION);
		this.model = model;
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout());

		Label projectNameLabel = new Label(composite, SWT.NONE);
		projectNameLabel.setText(ENTER_PROJECT_NAME);
		projectNameLabel.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM,
				false, false));

		projectNameField = new Text(composite, SWT.BORDER);
		projectNameField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));
		projectNameField.addModifyListener(new ModifyListener() {
			private static final long serialVersionUID = -3384299735086998756L;

			public void modifyText(ModifyEvent event) {
				onProjectNameChanged(projectNameField.getText());
			}
		});
		setFocusable(projectNameField);
		
		this.initialize();
	}

	public String getProjectName() {
		return projectNameField.getText();
	}

	public void setProjectName(String location) {
		if (projectNameField == null || projectNameField.isDisposed()) {
			return;
		}
		if (!areEqual(projectNameField.getText(), location)) {
			projectNameField.setText(location);
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
		projectNameField = null;
		super.dispose();
	}

	public void initialize() {
		this.setProjectName(this.model.getProjectName());
		revalidateModel();
	}

	public void onProjectNameChanged(String location) {
		this.model.setProjectName(location);
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
