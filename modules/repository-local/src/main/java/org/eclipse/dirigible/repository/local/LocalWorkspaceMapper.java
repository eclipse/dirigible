/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.repository.local;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.eclipse.dirigible.repository.fs.FileSystemRepository;

// TODO: Auto-generated Javadoc
/**
 * The Class LocalWorkspaceMapper.
 */
public class LocalWorkspaceMapper {

	/** The prefix map. */
	private static Map<String, String> prefixMap = Collections.synchronizedMap(new HashMap<String, String>());
	
	/** The prefix map equals. */
	private static Map<String, String> prefixMapEquals = Collections.synchronizedMap(new HashMap<String, String>());

	/** The workspace root. */
	private static String workspaceRoot = "/";

	/**
	 * Gets the mapped name.
	 *
	 * @param repository the repository
	 * @param repositoryName the repository name
	 * @return the mapped name
	 * @throws RepositoryWriteException the repository write exception
	 */
	public static String getMappedName(FileSystemRepository repository, String repositoryName) throws RepositoryWriteException {
		String workspaceName = null;

		if (repositoryName != null) {
			if (repositoryName.startsWith(repository.getRepositoryPath())) {
				workspaceName = repositoryName;
			} else {
				workspaceName = repository.getRepositoryPath() + repositoryName;
			}
		}

		if (workspaceName != null) {
			workspaceName = workspaceName.replace(IRepository.SEPARATOR, File.separator);
		}

		return workspaceName;
	}

}
