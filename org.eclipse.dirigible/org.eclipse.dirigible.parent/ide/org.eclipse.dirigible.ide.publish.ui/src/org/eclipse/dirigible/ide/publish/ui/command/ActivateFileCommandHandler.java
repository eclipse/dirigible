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

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.common.status.StatusLineManagerUtil;
import org.eclipse.dirigible.ide.publish.IPublisher;
import org.eclipse.dirigible.ide.publish.PublishException;
import org.eclipse.dirigible.ide.publish.PublishManager;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler for the Publish command.
 */
public class ActivateFileCommandHandler extends AbstractHandler {

	private static final String NO_PROJECTS_IN_SELECTION_NOTHING_TO_ACTIVATE = PublishCommandMessages.NO_PROJECTS_IN_SELECTION_NOTHING_TO_ACTIVATE;
	private static final String NOTHING_IS_SELECTED_TO_BE_ACTIVATED = PublishCommandMessages.NOTHING_IS_SELECTED_TO_BE_ACTIVATED;
	private static final Logger logger = Logger.getLogger(ActivateFileCommandHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		if (selection.isEmpty()) {
			logger.warn(NOTHING_IS_SELECTED_TO_BE_ACTIVATED);
			return null;
		}

		final IFile[] files = PublishManager.getFiles(selection);
		if (files.length == 0) {
			logger.warn(NO_PROJECTS_IN_SELECTION_NOTHING_TO_ACTIVATE);
			return null;
		}

		boolean success = true;
		String errorMessage = null;
		for (IFile file : files) {
			try {
				activateFile(file);
				StatusLineManagerUtil.setInfoMessage(String.format(StatusLineManagerUtil.ARTIFACT_HAS_BEEN_ACTIVATED, file.getName()));
			} catch (Exception ex) {
				errorMessage = ex.getMessage();
				logger.error(errorMessage, ex);
				success = false;
			}
		}
		if (!success) {
			logger.error(errorMessage);
			MessageDialog.openError(null, PublishCommandMessages.ACTIVATION_FAIL_TITLE, errorMessage);
		}
		return null;
	}

	protected void activateFile(IFile file) throws PublishException {
		final List<IPublisher> publishers = PublishManager.getPublishers();

		for (IPublisher iPublisher : publishers) {
			IPublisher publisher = iPublisher;
			publisher.activateFile(file, CommonIDEParameters.getRequest());
		}
	}

}
