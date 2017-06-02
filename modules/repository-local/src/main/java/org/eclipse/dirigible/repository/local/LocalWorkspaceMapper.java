/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.local;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.eclipse.dirigible.repository.fs.FileSystemRepository;
import org.eclipse.dirigible.repository.fs.FileSystemUtils;

public class LocalWorkspaceMapper {

	private static final String DB_DIRIGIBLE_USERS_LOCAL_WORKSPACE = "/db/dirigible/users/local/workspace/";
	private static Map<String, String> prefixMap = Collections.synchronizedMap(new HashMap<String, String>());
	private static Map<String, String> prefixMapEquals = Collections.synchronizedMap(new HashMap<String, String>());

	private static String workspaceRoot = "/";

	public static String getMappedName(FileSystemRepository repository, String repositoryName) throws RepositoryWriteException {
		String workspaceName = null;

		if ((repositoryName != null) && !"".equals(repositoryName)) {

			if (FileSystemUtils.exists(repositoryName) && !IRepository.SEPARATOR.equals(repositoryName)) {
				return repositoryName;
			}
		}

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

	public static String getReverseMappedName(String workspaceName) {
		if ((workspaceName == null) || "".endsWith(workspaceName)) {
			return workspaceName;
		}
		if (workspaceName.startsWith(DB_DIRIGIBLE_USERS_LOCAL_WORKSPACE)) {
			return workspaceName;
		}
		workspaceName = workspaceName.substring(workspaceRoot.length());
		workspaceName = workspaceName.replace(File.separator, IRepository.SEPARATOR);
		return workspaceName;
	}

}
