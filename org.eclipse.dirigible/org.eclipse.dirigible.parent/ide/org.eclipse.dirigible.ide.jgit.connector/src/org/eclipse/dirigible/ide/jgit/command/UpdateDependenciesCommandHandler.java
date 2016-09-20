package org.eclipse.dirigible.ide.jgit.command;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dirigible.ide.common.status.DefaultProgressMonitor;
import org.eclipse.dirigible.ide.common.status.StatusLineManagerUtil;
import org.eclipse.dirigible.ide.jgit.command.ui.CloneDependenciesCommandDialog;
import org.eclipse.dirigible.ide.jgit.utils.CommandHandlerUtils;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class UpdateDependenciesCommandHandler extends CloneCommandHandler {

	private static final Logger logger = Logger.getLogger(UpdateDependenciesCommandHandler.class);

	private static final String NO_PROJECT_IS_SELECTED_FOR_CLONE_DEPENDENCIES = Messages.CloneDependenciesCommandHandler_NO_PROJECT_IS_SELECTED_FOR_CLONE_DEPENDENCIES;
	private static final String PLEASE_SELECT_ONE = Messages.PushCommandHandler_PLEASE_SELECT_ONE;

	@Override
	public Object execute(ExecutionEvent event, String git) throws ExecutionException {

		final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		if (selection.isEmpty()) {
			logger.warn(NO_PROJECT_IS_SELECTED_FOR_CLONE_DEPENDENCIES);
			StatusLineManagerUtil.setWarningMessage(NO_PROJECT_IS_SELECTED_FOR_CLONE_DEPENDENCIES);
			MessageDialog.openWarning(null, NO_PROJECT_IS_SELECTED_FOR_CLONE_DEPENDENCIES, PLEASE_SELECT_ONE);
			return null;
		}
		final IProject[] projects = CommandHandlerUtils.getProjects(selection, logger);
		if (projects.length == 0) {
			logger.warn(NO_PROJECT_IS_SELECTED_FOR_CLONE_DEPENDENCIES);
			StatusLineManagerUtil.setWarningMessage(NO_PROJECT_IS_SELECTED_FOR_CLONE_DEPENDENCIES);
			MessageDialog.openWarning(null, NO_PROJECT_IS_SELECTED_FOR_CLONE_DEPENDENCIES, PLEASE_SELECT_ONE);
			return null;
		}

		DefaultProgressMonitor monitor = new DefaultProgressMonitor();
		monitor.beginTask(TASK_CLONING_REPOSITORY, IProgressMonitor.UNKNOWN);

		final Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		CloneDependenciesCommandDialog dialog = new CloneDependenciesCommandDialog(parent);

		switch (dialog.open()) {
			case Window.OK:
				for (IProject selectedProject : projects) {
					try {
						Set<String> clonedProjects = new HashSet<String>();
						cloneDependencies(dialog.getUsername(), dialog.getPassword(), clonedProjects, selectedProject.getName());
						refreshWorkspace();
						publishProjects(clonedProjects);
						StatusLineManagerUtil.setInfoMessage(PROJECT_S_HAS_BEEN_CLONED_SUCCESSFULLY);
					} catch (IOException e) {
						logger.error(WHILE_CLONING_REPOSITORY_ERROR_OCCURED + e.getMessage(), e);
						MessageDialog.openError(null, WHILE_CLONING_REPOSITORY_ERROR_OCCURED, e.getCause().getMessage());
					} catch (CoreException e) {
						logger.error(WHILE_CLONING_REPOSITORY_ERROR_OCCURED + e.getMessage(), e);
						MessageDialog.openError(null, WHILE_CLONING_REPOSITORY_ERROR_OCCURED, e.getCause().getMessage());
					}
					break;
				}
		}

		monitor.done();
		return null;
	}

}
