/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.git.project;

import java.io.File;
import java.nio.file.Paths;

import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.git.utils.GitFileUtils;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.fs.FileSystemRepository;

/**
 * Verify that the given project is Git aware.
 */
public class ProjectPropertiesVerifier {

	/** The repository. */
	private IRepository repository = null;
	
	protected synchronized IRepository getRepository() {
		if (repository == null) {
			repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
		}
		return repository;
	}

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
			if (getRepository() instanceof FileSystemRepository) {
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
