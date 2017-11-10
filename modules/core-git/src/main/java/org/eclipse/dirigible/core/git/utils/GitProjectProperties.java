/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.git.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;

// TODO: Auto-generated Javadoc
/**
 * The Class GitProjectProperties.
 */
public class GitProjectProperties {
	
	/** The git properties. */
	private final Properties gitProperties;
	
	/** The Constant PROJECT_GIT_PROPERTY. */
	public static final String PROJECT_GIT_PROPERTY = "git.property";
	
	/** The Constant PROPERTY_LAST_COMMIT_SHA. */
	public static final String PROPERTY_LAST_COMMIT_SHA = "last.commit.sha";
	
	/** The Constant PROPERTY_GIT_REPOSITORY_URL. */
	public static final String PROPERTY_GIT_REPOSITORY_URL = "git.repository.url";
	
	/** The Constant PATTERN_USERS_GIT_REPOSITORY. */
	public static final String PATTERN_USERS_GIT_REPOSITORY = IRepositoryStructure.PATH_ROOT + "git/%s/%s/%s"; // /git/john/workspace1/
	
	/** The Constant PATTERN_USERS_WORKSPACE. */
	public static final String PATTERN_USERS_WORKSPACE = IRepositoryStructure.PATH_USERS + "/%s/%s/"; // /users/john/workspace1
	
	/** The Constant GIT_PROPERTY_FILE_LOCATION. */
	public static final String GIT_PROPERTY_FILE_LOCATION = PATTERN_USERS_GIT_REPOSITORY + IRepositoryStructure.SEPARATOR + PROJECT_GIT_PROPERTY;

	/**
	 * Instantiates a new git project properties.
	 *
	 * @param URL the url
	 * @param SHA the sha
	 */
	public GitProjectProperties(String URL, String SHA) {
		gitProperties = new Properties();
		gitProperties.setProperty(GitProjectProperties.PROPERTY_GIT_REPOSITORY_URL, URL);
		gitProperties.setProperty(GitProjectProperties.PROPERTY_LAST_COMMIT_SHA, SHA);
	}

	/**
	 * Instantiates a new git project properties.
	 *
	 * @param in the in
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public GitProjectProperties(InputStream in) throws IOException {
		gitProperties = new Properties();
		load(in);
	}

	/**
	 * Load.
	 *
	 * @param in the in
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void load(InputStream in) throws IOException {
		gitProperties.load(in);
	}

	/**
	 * Gets the sha.
	 *
	 * @return the sha
	 */
	public String getSHA() {
		return gitProperties.getProperty(GitProjectProperties.PROPERTY_LAST_COMMIT_SHA);
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getURL() {
		return gitProperties.getProperty(GitProjectProperties.PROPERTY_GIT_REPOSITORY_URL);
	}

	/**
	 * Sets the SHA.
	 *
	 * @param SHA the sha
	 * @return the git project properties
	 */
	public GitProjectProperties setSHA(String SHA) {
		gitProperties.setProperty(GitProjectProperties.PROPERTY_LAST_COMMIT_SHA, SHA);
		return this;
	}

	/**
	 * Sets the URL.
	 *
	 * @param URL the url
	 * @return the git project properties
	 */
	public GitProjectProperties setURL(String URL) {
		gitProperties.setProperty(GitProjectProperties.PROPERTY_GIT_REPOSITORY_URL, URL);
		return this;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(GitProjectProperties.PROPERTY_GIT_REPOSITORY_URL + "=" + getURL() + "\n");
		result.append(GitProjectProperties.PROPERTY_LAST_COMMIT_SHA + "=" + getSHA());
		return result.toString();
	}

	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public byte[] getContent() {
		return toString().getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * Generate workspace path.
	 *
	 * @param workspace the workspace
	 * @param dirigibleUser the dirigible user
	 * @return the string
	 */
	public static String generateWorkspacePath(final IWorkspace workspace, String dirigibleUser) {
		String workspacePath = String.format(GitProjectProperties.PATTERN_USERS_WORKSPACE, dirigibleUser, workspace.getName());
		return workspacePath;
	}
}
