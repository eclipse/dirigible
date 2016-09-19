package org.eclipse.dirigible.ide.jgit.command.ui;

import org.eclipse.swt.widgets.Shell;

public class CloneDependenciesCommandDialog extends BaseCommandDialog {

	private static final String CLONING_GIT_REPOSITORY = Messages.CloneDependenciesCommandDialog_CLONING_GIT_REPOSITORY;
	private static final String ENTER_GIT_REPOSITORY_URL = Messages.CloneDependenciesCommandDialog_ENTER_GIT_REPOSITORY_URL;

	public CloneDependenciesCommandDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle(CLONING_GIT_REPOSITORY);
		setMessage(ENTER_GIT_REPOSITORY_URL);
	}

}
