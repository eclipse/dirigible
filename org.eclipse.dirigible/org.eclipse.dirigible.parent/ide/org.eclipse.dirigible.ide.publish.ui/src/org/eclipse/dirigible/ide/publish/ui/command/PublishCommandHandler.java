/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.publish.ui.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.dirigible.ide.common.status.StatusLineManagerUtil;
import org.eclipse.dirigible.ide.publish.PublishException;
import org.eclipse.dirigible.ide.publish.PublishManager;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler for the Publish command.
 */
public class PublishCommandHandler extends AbstractHandler {

	private static final String NO_PROJECTS_IN_SELECTION_NOTHING_TO_PUBLISH = PublishCommandMessages.NO_PROJECTS_IN_SELECTION_NOTHING_TO_PUBLISH;
	private static final String NOTHING_IS_SELECTED_TO_BE_PUBLISHED = PublishCommandMessages.NOTHING_IS_SELECTED_TO_BE_PUBLISHED;
	private static final Logger logger = Logger.getLogger(PublishCommandHandler.class);

	public PublishCommandHandler() {
		super();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		return executeOnSelection(selection);
	}

	protected Object executeOnSelection(final ISelection selection) {
		if (selection.isEmpty()) {
			logger.warn(NOTHING_IS_SELECTED_TO_BE_PUBLISHED);
			return null;
		}

		final IProject[] projects = PublishManager.getProjects(selection);
		if (projects.length == 0) {
			logger.warn(NO_PROJECTS_IN_SELECTION_NOTHING_TO_PUBLISH);
			return null;
		}

		StatusLineManagerUtil.setInfoMessage(""); //$NON-NLS-1$
		boolean success = true;
		String errorMessage = null;
		for (IProject project : projects) {
			try {
				publishProject(project);
				StatusLineManagerUtil.setInfoMessage(String.format(getStatusMessage(), project.getName()));
			} catch (Exception ex) {
				errorMessage = ex.getMessage();
				logger.error(errorMessage, ex);
				success = false;
			}
		}
		if (!success) {
			logger.error(errorMessage);
			StatusLineManagerUtil.setErrorMessage(errorMessage);
			MessageDialog.openError(null, PublishCommandMessages.PUBLISH_FAIL_TITLE, errorMessage);
		}
		return null;
	}

	protected String getStatusMessage() {
		return StatusLineManagerUtil.ARTIFACT_HAS_BEEN_PUBLISHED;
	}

	protected void publishProject(IProject project) throws PublishException {
		PublishManager.publishProject(project);
	}

}
