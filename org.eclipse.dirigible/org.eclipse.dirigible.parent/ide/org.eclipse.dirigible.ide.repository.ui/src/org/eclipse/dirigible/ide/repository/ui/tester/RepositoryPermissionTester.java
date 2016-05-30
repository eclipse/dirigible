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

package org.eclipse.dirigible.ide.repository.ui.tester;

import java.io.IOException;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.ext.security.IRoles;
import org.eclipse.dirigible.repository.logging.Logger;

public class RepositoryPermissionTester extends PropertyTester {
	private static final Logger logger = Logger.getLogger(RepositoryPermissionTester.class);

	private static final String REPOSITORY_COMMAND_COPY = "copy";

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		String operation = (String) expectedValue;
		String currentUser = CommonIDEParameters.getUserName();
		String createdBy = null;
		boolean allowed = false;

		String allowedCopyPath = IRepositoryPaths.DB_DIRIGIBLE_ROOT;
		String allowedWorkspacePath = CommonIDEParameters.getWorkspace();
		String allowedSandboxPath = IRepositoryPaths.DB_DIRIGIBLE_SANDBOX + currentUser;
		
		boolean isOperator = CommonIDEParameters.isUserInRole(IRoles.ROLE_OPERATOR);
		
		IEntity selectedEntity = (IEntity) receiver;
		String selectedEntityPath = selectedEntity.getPath();

		try {
			if (isOperator) {
				allowed = true;
			} else if (operation.equalsIgnoreCase(REPOSITORY_COMMAND_COPY)) {
				allowed = selectedEntityPath.startsWith(allowedCopyPath);
			} else {
				createdBy = selectedEntity.getInformation().getCreatedBy();
				allowed = (selectedEntityPath.startsWith(allowedWorkspacePath) || selectedEntityPath
						.startsWith(allowedSandboxPath)) && currentUser.equalsIgnoreCase(createdBy);
			}

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			allowed = false;
		}
		return allowed;
	}
}
