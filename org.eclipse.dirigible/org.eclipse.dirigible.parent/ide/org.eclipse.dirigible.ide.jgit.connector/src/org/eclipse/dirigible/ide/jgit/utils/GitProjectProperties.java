/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.jgit.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;

public class GitProjectProperties {
	private final Properties gitProperties;
	public static final String PROJECT_GIT_PROPERTY = "git.property";
	public static final String PROPERTY_LAST_COMMIT_SHA = "last.commit.sha";
	public static final String PROPERTY_GIT_REPOSITORY_URL = "git.repository.url";
	public static final String DB_DIRIGIBLE_USERS_S_GIT_S_REPOSITORY = IRepositoryPaths.DB_DIRIGIBLE_ROOT + "users/%s/git/%s";
	public static final String DB_DIRIGIBLE_USERS_S_WORKSPACE = IRepositoryPaths.DB_DIRIGIBLE_ROOT + "users/%s/workspace/";
	public static final String GIT_PROPERTY_FILE_LOCATION = DB_DIRIGIBLE_USERS_S_GIT_S_REPOSITORY + "/" + PROJECT_GIT_PROPERTY;

	public GitProjectProperties(String URL, String SHA) {
		gitProperties = new Properties();
		gitProperties.setProperty(GitProjectProperties.PROPERTY_GIT_REPOSITORY_URL, URL);
		gitProperties.setProperty(GitProjectProperties.PROPERTY_LAST_COMMIT_SHA, SHA);
	}

	public GitProjectProperties(InputStream in) throws IOException {
		gitProperties = new Properties();
		load(in);
	}

	public void load(InputStream in) throws IOException {
		gitProperties.load(in);
	}

	public String getSHA() {
		return gitProperties.getProperty(GitProjectProperties.PROPERTY_LAST_COMMIT_SHA);
	}

	public String getURL() {
		return gitProperties.getProperty(GitProjectProperties.PROPERTY_GIT_REPOSITORY_URL);
	}

	public GitProjectProperties setSHA(String SHA) {
		gitProperties.setProperty(GitProjectProperties.PROPERTY_LAST_COMMIT_SHA, SHA);
		return this;
	}

	public GitProjectProperties setURL(String URL) {
		gitProperties.setProperty(GitProjectProperties.PROPERTY_GIT_REPOSITORY_URL, URL);
		return this;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(GitProjectProperties.PROPERTY_GIT_REPOSITORY_URL + "=" + getURL() + "\n");
		result.append(GitProjectProperties.PROPERTY_LAST_COMMIT_SHA + "=" + getSHA());
		return result.toString();
	}

	public byte[] getContent() {
		return toString().getBytes(ICommonConstants.UTF8);
	}
}
