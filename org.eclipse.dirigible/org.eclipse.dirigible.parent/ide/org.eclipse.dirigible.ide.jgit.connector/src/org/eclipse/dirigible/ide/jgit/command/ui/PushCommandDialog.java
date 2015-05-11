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

package org.eclipse.dirigible.ide.jgit.command.ui;

import org.eclipse.jgit.util.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PushCommandDialog extends BaseCommandDialog {

	private static final long serialVersionUID = -5124345102495879231L;

	private static final String PUSH_CHANGES_TO_REMOTE_GIT_REPOSITORY = Messages.BaseCommandDialog_PUSH_CHANGES_TO_REMOTE_GIT_REPOSITORY;
	private static final String COMMIT_MESSAGE = Messages.CommandDialog_COMMIT_MESSAGE;
	private static final String EMAIL = Messages.PushCommandDialog_EMAIL;
	private static final String COMMIT_MESSAGE_IS_EMPTY = Messages.PushCommandDialog_COMMIT_MESSAGE_IS_EMPTY;
	private static final String EMAIL_IS_EMPTY = Messages.PushCommandDialog_EMAIL_IS_EMPTY;

	private Text textCommitMessage;
	private Text textEmail;

	private String commitMessage;
	private String email;

	public PushCommandDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle(PUSH_CHANGES_TO_REMOTE_GIT_REPOSITORY);
	}

	@Override
	protected void addWidgets(Composite container) {
		createCommitMessageField(container);
		super.addWidgets(container);
		createEmailField(container);
	}

	private void createCommitMessageField(Composite container) {
		Label labelCommitMessage = new Label(container, SWT.NONE);
		labelCommitMessage.setText(COMMIT_MESSAGE);

		GridData dataCommitMessage = new GridData();
		dataCommitMessage.grabExcessHorizontalSpace = true;
		dataCommitMessage.horizontalAlignment = GridData.FILL;

		textCommitMessage = new Text(container, SWT.BORDER);
		textCommitMessage.setLayoutData(dataCommitMessage);
	}

	private void createEmailField(Composite container) {
		Label labelEmail = new Label(container, SWT.NONE);
		labelEmail.setText(EMAIL);

		GridData dataEmail = new GridData();
		dataEmail.grabExcessHorizontalSpace = true;
		dataEmail.horizontalAlignment = GridData.FILL;

		textEmail = new Text(container, SWT.BORDER);
		textEmail.setLayoutData(dataEmail);
	}

	@Override
	protected boolean validateInput() {
		boolean valid = false;
		if (super.validateInput()) {
			if (StringUtils.isEmptyOrNull(textCommitMessage.getText())) {
				errorMessage = COMMIT_MESSAGE_IS_EMPTY;
			} else if (StringUtils.isEmptyOrNull(textEmail.getText())) {
				errorMessage = EMAIL_IS_EMPTY;
			} else {
				valid = true;
			}
		}
		return valid;
	}

	@Override
	protected void saveInput() {
		super.saveInput();
		commitMessage = textCommitMessage.getText();
		email = textEmail.getText();
	}

	public String getCommitMessage() {
		return commitMessage;
	}

	public String getEmail() {
		return email;
	}
}