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

package org.eclipse.dirigible.ide.jgit.property.tester;

import java.io.IOException;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;

import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.jgit.utils.GitProjectProperties;
import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.logging.Logger;

public class GitProjectPropertyTest extends PropertyTester {
	private static final Logger logger = Logger.getLogger(GitProjectProperties.class);

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		boolean result = false;
		boolean inverseForm = false;
		if (expectedValue instanceof Boolean) {
			inverseForm = !(Boolean) expectedValue;
		}
		if (receiver instanceof IProject) {
			IProject project = (IProject) receiver;
			IRepository repository = RepositoryFacade.getInstance().getRepository();

			String user = CommonIDEParameters.getUserName();
			String projectName = project.getName();
			String gitFilePath = String.format(GitProjectProperties.GIT_PROPERTY_FILE_LOCATION,
					user, projectName);
			try {
				result = repository.hasResource(gitFilePath);
			} catch (IOException e) {
				result = false;
				logger.error(e.getMessage(), e);
			}
		}
		if (inverseForm) {
			result = !result;
		}
		return result;
	}

}
