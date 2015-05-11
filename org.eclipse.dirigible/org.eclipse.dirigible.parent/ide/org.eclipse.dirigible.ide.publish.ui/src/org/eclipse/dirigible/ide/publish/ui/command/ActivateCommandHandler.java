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

package org.eclipse.dirigible.ide.publish.ui.command;

import org.eclipse.core.resources.IProject;

import org.eclipse.dirigible.ide.common.status.StatusLineManagerUtil;
import org.eclipse.dirigible.ide.publish.PublishException;
import org.eclipse.dirigible.ide.publish.PublishManager;

/**
 * Handler for the Publish command.
 * 
 */
public class ActivateCommandHandler extends PublishCommandHandler {

	@Override
	protected void publishProject(IProject project) throws PublishException {
		PublishManager.activateProject(project);
	}
	
	@Override
	protected String getStatusMessage() {
		return StatusLineManagerUtil.ARTIFACT_HAS_BEEN_ACTIVATED;
	}
	
}
