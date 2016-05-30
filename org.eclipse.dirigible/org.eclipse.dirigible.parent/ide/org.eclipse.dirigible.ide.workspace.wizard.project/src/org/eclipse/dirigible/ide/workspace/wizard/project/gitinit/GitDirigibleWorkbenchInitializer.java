package org.eclipse.dirigible.ide.workspace.wizard.project.gitinit;

import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.ui.rap.api.IDirigibleWorkbenchInitializer;

public class GitDirigibleWorkbenchInitializer implements IDirigibleWorkbenchInitializer {

	public GitDirigibleWorkbenchInitializer() {
		//
	}

	@Override
	public void doInitialization() {
		String gitParam = CommonIDEParameters.get("git");
		if (gitParam != null) {
			gitParam = gitParam.replace('~', '/');
			CommonIDEParameters.set("git", gitParam);
		}
		// check existence of git folders and related artifacts and make clean-up if necessary
	}

}
