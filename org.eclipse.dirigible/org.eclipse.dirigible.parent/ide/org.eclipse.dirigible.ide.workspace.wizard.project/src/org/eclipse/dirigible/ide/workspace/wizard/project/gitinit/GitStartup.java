package org.eclipse.dirigible.ide.workspace.wizard.project.gitinit;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.jgit.command.CloneCommandHandler;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;

public class GitStartup implements IStartup {

	private static final Logger logger = Logger.getLogger(GitStartup.class);

	@Override
	public void earlyStartup() {

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				String git = CommonIDEParameters.get("git");
				if (git != null) {
					CloneCommandHandler cloneCommandHandler = new CloneCommandHandler();
					try {
						cloneCommandHandler.execute(null, git);
					} catch (ExecutionException e) {
						logger.error(e.getMessage());
					}
				}
			}
		});

	}

}
