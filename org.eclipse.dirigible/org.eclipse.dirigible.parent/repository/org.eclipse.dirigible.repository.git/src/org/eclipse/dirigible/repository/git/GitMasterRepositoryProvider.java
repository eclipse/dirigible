/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.git;

import java.util.Map;

import org.eclipse.dirigible.repository.api.IMasterRepository;
import org.eclipse.dirigible.repository.api.IMasterRepositoryProvider;

public class GitMasterRepositoryProvider implements IMasterRepositoryProvider {

	private static final String PARAM_USER = "user";
	private static final String PARAM_GIT_TARGET_FOLDER = "git.target";
	private static final String PARAM_GIT_LOCATION = "git.location";
	private static final String PARAM_GIT_USER = "git.user";
	private static final String PARAM_GIT_PASSWORD = "git.password";
	private static final String PARAM_GIT_BRANCH = "git.branch";

	@Override
	public IMasterRepository createRepository(Map<String, Object> parameters) {
		String user = (String) parameters.get(PARAM_USER);
		String targetFolder = (String) parameters.get(PARAM_GIT_TARGET_FOLDER);
		String gitLocation = (String) parameters.get(PARAM_GIT_LOCATION);
		String gitUser = (String) parameters.get(PARAM_GIT_USER);
		String gitPassword = (String) parameters.get(PARAM_GIT_PASSWORD);
		String gitBranch = (String) parameters.get(PARAM_GIT_BRANCH);

		return new GitMasterRepository(user, targetFolder, gitLocation, gitUser, gitPassword, gitBranch);
	}

}
