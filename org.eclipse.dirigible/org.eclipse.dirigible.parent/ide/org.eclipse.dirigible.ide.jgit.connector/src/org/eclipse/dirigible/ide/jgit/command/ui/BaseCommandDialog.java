/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.jgit.command.ui;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jgit.util.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public abstract class BaseCommandDialog extends TitleAreaDialog {

	private static final long serialVersionUID = -5124345102495879231L;

	private static final String USERNAME = Messages.BaseCommandDialog_USERNAME;
	private static final String PASSWORD = Messages.BaseCommandDialog_PASSWORD;
	private static final String USERNAME_IS_EMPTY = Messages.BaseCommandDialog_USERNAME_IS_EMPTY;
	private static final String PASSWORD_IS_EMPTY = Messages.BaseCommandDialog_PASSWORD_IS_EMPTY;

	protected static final String MASTER = "master"; //$NON-NLS-1$

	private Text textUsername;
	private Text textPassword;

	private String username;
	private String password;

	protected String errorMessage;

	public BaseCommandDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2, false);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(layout);
		addWidgets(container);
		return area;
	}

	protected void addWidgets(Composite container) {
		createUsernameField(container);
		createPasswordField(container);
	}

	private void createUsernameField(Composite container) {
		Label labelUsername = new Label(container, SWT.NONE);
		labelUsername.setText(USERNAME);

		GridData dataUsername = new GridData();
		dataUsername.grabExcessHorizontalSpace = true;
		dataUsername.horizontalAlignment = GridData.FILL;

		textUsername = new Text(container, SWT.BORDER);
		textUsername.setLayoutData(dataUsername);
	}

	private void createPasswordField(Composite container) {
		Label labelPassword = new Label(container, SWT.NONE);
		labelPassword.setText(PASSWORD);

		GridData dataPassword = new GridData();
		dataPassword.grabExcessHorizontalSpace = true;
		dataPassword.horizontalAlignment = GridData.FILL;

		textPassword = new Text(container, SWT.BORDER | SWT.PASSWORD);
		textPassword.setLayoutData(dataPassword);
	}

	protected boolean validateInput() {
		boolean valid = false;

		if (StringUtils.isEmptyOrNull(textUsername.getText())) {
			errorMessage = USERNAME_IS_EMPTY;
		} else if (StringUtils.isEmptyOrNull(textPassword.getText())) {
			errorMessage = PASSWORD_IS_EMPTY;
		} else {
			valid = true;
		}
		return valid;
	}

	protected void saveInput() {
		username = textUsername.getText();
		password = textPassword.getText();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		if (validateInput()) {
			errorMessage = null;
			saveInput();
			super.okPressed();
		}
		setErrorMessage(errorMessage);
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}
