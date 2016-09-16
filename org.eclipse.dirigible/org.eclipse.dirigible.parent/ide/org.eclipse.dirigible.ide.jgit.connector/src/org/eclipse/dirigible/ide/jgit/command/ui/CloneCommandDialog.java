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

import org.eclipse.dirigible.ide.jgit.utils.GitFileUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CloneCommandDialog extends BaseCommandDialog {

	private static final long serialVersionUID = -5124345102495879231L;

	private static final String CLONING_GIT_REPOSITORY = Messages.CloneCommandDialog_CLONING_GIT_REPOSITORY;
	private static final String ENTER_GIT_REPOSITORY_URL = Messages.CloneCommandDialog_ENTER_GIT_REPOSITORY_URL;
	private static final String INVALID_GIT_REPOSITORY_URL = Messages.CloneCommandDialog_INVALID_GIT_REPOSITORY_URL;
	private static final String REPOSITORY_URI = Messages.CommandDialog_REPOSITORY_URI;
	private static final String REPOSITORY_BRANCH = Messages.CommandDialog_REPOSITORY_BRANCH;

	private Text textRepositoryURI;

	private Text textRepositoryBranch;

	private String repositoryURI;

	private String repositoryBranch;

	public CloneCommandDialog(Shell parentShell, String git) {
		super(parentShell);
		this.repositoryURI = git;
		this.repositoryBranch = MASTER;
	}

	@Override
	public void create() {
		super.create();
		setTitle(CLONING_GIT_REPOSITORY);
		setMessage(ENTER_GIT_REPOSITORY_URL);
	}

	@Override
	protected void addWidgets(Composite container) {
		createRepositoryURIField(container);
		createRepositoryBranchField(container);
		super.addWidgets(container);
	}

	private void createRepositoryURIField(Composite container) {
		Label labelRepositoryURI = new Label(container, SWT.NONE);
		labelRepositoryURI.setText(REPOSITORY_URI);

		GridData dataRepositoryURI = new GridData();
		dataRepositoryURI.grabExcessHorizontalSpace = true;
		dataRepositoryURI.horizontalAlignment = GridData.FILL;

		textRepositoryURI = new Text(container, SWT.BORDER);
		textRepositoryURI.setLayoutData(dataRepositoryURI);
		if (this.repositoryURI != null) {
			textRepositoryURI.setText(this.repositoryURI);
		}
	}

	private void createRepositoryBranchField(Composite container) {
		Label labelRepositoryBranch = new Label(container, SWT.NONE);
		labelRepositoryBranch.setText(REPOSITORY_BRANCH);

		GridData dataRepositoryBranch = new GridData();
		dataRepositoryBranch.grabExcessHorizontalSpace = true;
		dataRepositoryBranch.horizontalAlignment = GridData.FILL;

		textRepositoryBranch = new Text(container, SWT.BORDER);
		textRepositoryBranch.setLayoutData(dataRepositoryBranch);
		if (this.repositoryBranch != null) {
			textRepositoryBranch.setText(this.repositoryBranch);
		}
	}

	@Override
	protected boolean validateInput() {
		boolean valid = GitFileUtils.isValidRepositoryURI(textRepositoryURI.getText());
		if (!valid) {
			errorMessage = INVALID_GIT_REPOSITORY_URL;
		}
		return valid;
	}

	@Override
	protected void saveInput() {
		super.saveInput();
		repositoryURI = textRepositoryURI.getText();
	}

	public String getRepositoryURI() {
		return repositoryURI;
	}

	public String getRepositoryBranch() {
		return repositoryBranch;
	}
}
