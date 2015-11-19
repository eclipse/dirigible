/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.jgit.command;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.ide.common.status.DefaultProgressMonitor;
import org.eclipse.dirigible.ide.common.status.StatusLineManagerUtil;
import org.eclipse.dirigible.ide.jgit.command.ui.CloneCommandDialog;
import org.eclipse.dirigible.ide.jgit.utils.GitFileUtils;
import org.eclipse.dirigible.ide.jgit.utils.GitProjectProperties;
import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.ide.workspace.ui.commands.AbstractWorkspaceHandler;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.ext.git.JGitConnector;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class CloneCommandHandler extends AbstractWorkspaceHandler {

	private static final String TASK_CLONING_REPOSITORY = Messages.CloneCommandHandler_TASK_CLONING_REPOSITORY;
	private static final String MASTER = "master"; //$NON-NLS-1$
	private static final String PLEASE_CHECK_IF_PROXY_SETTINGS_ARE_SET_PROPERLY = Messages.CloneCommandHandler_MASTER;
	private static final String NO_REMOTE_REPOSITORY_FOR = Messages.CloneCommandHandler_NO_REMOTE_REPOSITORY_FOR;
	private static final String DOT_GIT = ".git"; //$NON-NLS-1$

	private static final String PROJECT_WAS_CLONED = Messages.CloneCommandHandler_PROJECT_WAS_CLONED;
	private static final String WHILE_CLONING_REPOSITORY_ERROR_OCCURED = Messages.CloneCommandHandler_WHILE_CLONING_REPOSITORY_ERROR_OCCURED;
	private static final String SLASH = "/"; //$NON-NLS-1$

	private static final Logger logger = Logger.getLogger(CloneCommandHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		DefaultProgressMonitor monitor = new DefaultProgressMonitor();
		monitor.beginTask(TASK_CLONING_REPOSITORY, IProgressMonitor.UNKNOWN);

		final Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		CloneCommandDialog dialog = new CloneCommandDialog(parent);

		switch (dialog.open()) {
			case Window.OK:
				cloneGitRepository(dialog.getRepositoryURI(), dialog.getUsername(), dialog.getPassword());
				break;
		}

		monitor.done();
		return null;
	}

	private void cloneGitRepository(final String repositoryURI, final String username, final String password) {
		File gitDirectory = null;
		try {
			String user = CommonParameters.getUserName();
			String repositoryName = repositoryURI.substring(repositoryURI.lastIndexOf(SLASH) + 1, repositoryURI.lastIndexOf(DOT_GIT));
			gitDirectory = GitFileUtils.createTempDirectory(GitFileUtils.TEMP_DIRECTORY_PREFIX + repositoryName);

			JGitConnector.cloneRepository(gitDirectory, repositoryURI, username, password);

			Repository gitRepository = JGitConnector.getRepository(gitDirectory.getAbsolutePath());
			JGitConnector jgit = new JGitConnector(gitRepository);

			final String lastSha = jgit.getLastSHAForBranch(MASTER);
			GitProjectProperties gitProperties = new GitProjectProperties(repositoryURI, lastSha);

			IRepository repository = RepositoryFacade.getInstance().getRepository();
			String workspacePath = String.format(GitProjectProperties.DB_DIRIGIBLE_USERS_S_WORKSPACE, user);

			List<String> importedProjects = GitFileUtils.importProject(gitDirectory, repository, workspacePath, user, gitProperties);
			StatusLineManagerUtil.setInfoMessage(String.format(PROJECT_WAS_CLONED, importedProjects));
			refreshWorkspace();
		} catch (InvalidRemoteException e) {
			logger.error(WHILE_CLONING_REPOSITORY_ERROR_OCCURED + e.getMessage(), e);
			String causedBy = NO_REMOTE_REPOSITORY_FOR + e.getCause().getMessage();
			MessageDialog.openError(null, WHILE_CLONING_REPOSITORY_ERROR_OCCURED, causedBy);
		} catch (TransportException e) {
			logger.error(WHILE_CLONING_REPOSITORY_ERROR_OCCURED + e.getMessage(), e);
			Throwable rootCause = e.getCause();

			if (rootCause != null) {
				rootCause = rootCause.getCause();
				if (rootCause instanceof UnknownHostException) {
					String causedBy = PLEASE_CHECK_IF_PROXY_SETTINGS_ARE_SET_PROPERLY;
					MessageDialog.openError(null, WHILE_CLONING_REPOSITORY_ERROR_OCCURED, causedBy);
				} else {
					MessageDialog.openError(null, WHILE_CLONING_REPOSITORY_ERROR_OCCURED, e.getCause().getMessage());
				}
			} else {
				MessageDialog.openError(null, WHILE_CLONING_REPOSITORY_ERROR_OCCURED, e.getMessage());
			}
		} catch (GitAPIException e) {
			logger.error(WHILE_CLONING_REPOSITORY_ERROR_OCCURED + e.getMessage(), e);
			MessageDialog.openError(null, WHILE_CLONING_REPOSITORY_ERROR_OCCURED, e.getCause().getMessage());
		} catch (IOException e) {
			logger.error(WHILE_CLONING_REPOSITORY_ERROR_OCCURED + e.getMessage(), e);
			MessageDialog.openError(null, WHILE_CLONING_REPOSITORY_ERROR_OCCURED, e.getCause().getMessage());
		} finally {
			GitFileUtils.deleteDirectory(gitDirectory);
		}
	}

}
