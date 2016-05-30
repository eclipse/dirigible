package org.eclipse.dirigible.ide.workspace.wizard.project.getstarted;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.workspace.dual.WorkspaceLocator;
import org.eclipse.dirigible.ide.workspace.wizard.project.commands.GetStartedProjectHandler;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;

public class GetStartedStartup implements IStartup {

	private static final Logger logger = Logger.getLogger(GetStartedStartup.class);

	@Override
	public void earlyStartup() {

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (CommonIDEParameters.get("git") != null) {
					// starting for git import - skip get started
					return;
				}
				// enable/disable get started wizard
				if (WorkspaceLocator.getWorkspace().getRoot().getProjects().length == 0) {
					GetStartedProjectHandler projectHandler = new GetStartedProjectHandler();
					try {
						projectHandler.execute(null);
					} catch (ExecutionException e) {
						logger.error(e.getMessage());
					}
				}
			}
		});

	}

}
