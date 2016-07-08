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
import org.eclipse.dirigible.repository.logging.Logger;

/**
 * The Provider of the Git Master Repository
 */
public class GitMasterRepositoryProvider implements IMasterRepositoryProvider {

	private static final Logger logger = Logger.getLogger(GitMasterRepositoryProvider.class);

	private static final String PARAM_USER = "user";
	private static final String PARAM_GIT_TARGET_FOLDER = "masterRepositoryGitTarget"; //$NON-NLS-1$
	private static final String PARAM_GIT_LOCATION = "masterRepositoryGitLocation"; //$NON-NLS-1$
	private static final String PARAM_GIT_USER = "masterRepositoryGitUser"; //$NON-NLS-1$
	private static final String PARAM_GIT_PASSWORD = "masterRepositoryGitPassword"; //$NON-NLS-1$
	private static final String PARAM_GIT_BRANCH = "masterRepositoryGitBranch"; //$NON-NLS-1$
	public static final String TYPE = "git";

	@Override
	public IMasterRepository createRepository(Map<String, Object> parameters) {
		logger.debug("creating Git Master Repository...");
		String user = (String) parameters.get(PARAM_USER);
		String targetFolder = (String) parameters.get(PARAM_GIT_TARGET_FOLDER);
		if (targetFolder == null) {
			targetFolder = System.getProperty(PARAM_GIT_TARGET_FOLDER);
		}
		logger.info("Git Master Repository - Target Folder: " + targetFolder);
		String gitLocation = (String) parameters.get(PARAM_GIT_LOCATION);
		if (gitLocation == null) {
			gitLocation = System.getProperty(PARAM_GIT_LOCATION);
		}
		logger.info("Git Master Repository - Git Location: " + gitLocation);
		String gitUser = (String) parameters.get(PARAM_GIT_USER);
		if (gitUser == null) {
			gitUser = System.getProperty(PARAM_GIT_USER);
		}
		logger.info("Git Master Repository - Git User: " + gitUser);
		String gitPassword = (String) parameters.get(PARAM_GIT_PASSWORD);
		if (gitPassword == null) {
			gitPassword = System.getProperty(PARAM_GIT_PASSWORD);
		}
		logger.info("Git Master Repository - Git Password: " + ((gitPassword == null ? "none" : "***")));
		String gitBranch = (String) parameters.get(PARAM_GIT_BRANCH);
		if (gitBranch == null) {
			gitBranch = System.getProperty(PARAM_GIT_BRANCH);
		}
		logger.info("Git Master Repository - Git Branch: " + gitBranch);

		GitMasterRepository result = new GitMasterRepository(user, targetFolder, gitLocation, gitUser, gitPassword, gitBranch);

		logger.debug("Git Mater Repository created.");
		return result;
	}

	@Override
	public String getType() {
		return TYPE;
	}

}
