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
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.ext.fs.FileSystemUtils;

public class LocalWorkspaceMapper {

	private static final String DB_DIRIGIBLE_USERS_LOCAL_WORKSPACE = "/db/dirigible/users/local/workspace/";
	private static Map<String, String> prefixMap = Collections.synchronizedMap(new HashMap<String, String>());
	private static Map<String, String> prefixMapEquals = Collections.synchronizedMap(new HashMap<String, String>());

	private static String workspaceRoot = "/";

	public static String getMappedName(LocalRepository repository, String repositoryName) throws IOException {
		String workspaceName = null;

		if ((repositoryName != null) && !"".equals(repositoryName)) {

			if (FileSystemUtils.exists(repositoryName) && !IRepository.SEPARATOR.equals(repositoryName)) {
				return repositoryName;
			}
		}

		if (workspaceName == null) {
			if (repositoryName.startsWith(repository.getRepositoryPath())) {
				workspaceName = repositoryName;
			} else {
				workspaceName = repository.getRepositoryPath() + repositoryName;
			}
			// throw new IOException("No workspace mapping for file: " + repositoryName);
		}

		workspaceName = workspaceName.replace(IRepository.SEPARATOR, File.separator);

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

	// private static void check() throws IOException {
	// if (prefixMap.isEmpty()) {
	//
	// IWorkspace workspace = ResourcesPlugin.getWorkspace();
	// File workspaceDirectory = workspace.getRoot().getLocation().toFile();
	// workspaceRoot = workspaceDirectory.getCanonicalPath();
	//
	// prefixMap.put("/db/dirigible/users/local/workspace", workspaceRoot);
	// prefixMap.put(workspaceRoot + "/db/dirigible/users/local/workspace", workspaceRoot);
	//
	// String db_dirigible_root = workspaceRoot + File.separator + "db" + File.separator + "dirigible" + File.separator;
	//
	// prefixMap.put("/db/dirigible/registry", db_dirigible_root + "registry");
	// prefixMap.put("/db/dirigible/sandbox", db_dirigible_root + "sandbox");
	// prefixMap.put("/db/dirigible/templates", db_dirigible_root + "templates");
	//
	// prefixMapEquals.put("/", workspaceRoot);
	// prefixMapEquals.put("/db", workspaceRoot + File.separator + "db");
	// prefixMapEquals.put("/db/dirigible", workspaceRoot + File.separator + "db" + File.separator + "dirigible");
	// prefixMapEquals.put("/db/dirigible/users", db_dirigible_root + "users");
	// prefixMapEquals.put("/db/dirigible/users/local", db_dirigible_root + "users" + File.separator + "local");
	//
	// prefixMapEquals.put("/db/dirigible/registry", db_dirigible_root + "registry");
	// prefixMapEquals.put("/db/dirigible/sandbox", db_dirigible_root + "sandbox");
	// prefixMapEquals.put("/db/dirigible/templates", db_dirigible_root + "templates");
	// prefixMapEquals.put("/db/dirigible/default.content", db_dirigible_root + "default.content");
	//
	// }
	//
	// }

}
