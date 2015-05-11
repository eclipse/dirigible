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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.eclipse.dirigible.ide.jgit.utils.GitFileUtils;

public class CloneCommandDialog extends BaseCommandDialog {

	private static final long serialVersionUID = -5124345102495879231L;

	private static final String CLONING_GIT_REPOSITORY = Messages.CloneCommandDialog_CLONING_GIT_REPOSITORY;
	private static final String ENTER_GIT_REPOSITORY_URL = Messages.CloneCommandDialog_ENTER_GIT_REPOSITORY_URL;
	private static final String INVALID_GIT_REPOSITORY_URL = Messages.CloneCommandDialog_INVALID_GIT_REPOSITORY_URL;
	private static final String REPOSITORY_URI = Messages.CommandDialog_REPOSITORY_URI;

	private Text textRepositoryURI;

	private String repositoryURI;

	public CloneCommandDialog(Shell parentShell) {
		super(parentShell);
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
}