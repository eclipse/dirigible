/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.git.project;

import java.io.File;
import java.nio.file.Paths;

import javax.inject.Inject;

import org.eclipse.dirigible.core.git.utils.GitFileUtils;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.fs.FileSystemRepository;

/**
 * Verify that the given project is Git aware.
 */
public class ProjectPropertiesVerifier {

	/** The repository. */
	@Inject
	private IRepository repository;

	/**
	 * Verify.
	 *
	 * @param workspace
	 *            the workspace
	 * @param repositoryName
	 *            the repositoryName
	 * @return true, if successful
	 */
	public boolean verify(String workspace, String repositoryName) {
		try {
			if (repository instanceof FileSystemRepository) {
				File gitRepository = GitFileUtils.getGitDirectoryByRepositoryName(workspace, repositoryName);
				String gitDirectory = gitRepository.getCanonicalPath();
				return Paths.get(Paths.get(gitDirectory).toString(), ".git").toFile().exists();
			}
		} catch (Exception e) {
			// do nothing
		}
		return false;
	}

}
