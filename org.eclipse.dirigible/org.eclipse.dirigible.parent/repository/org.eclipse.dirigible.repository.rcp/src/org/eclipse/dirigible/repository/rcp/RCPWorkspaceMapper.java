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

package org.eclipse.dirigible.repository.rcp;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.dirigible.repository.api.IRepository;

public class RCPWorkspaceMapper {
	
	private static final String DB_DIRIGIBLE_USERS_LOCAL_WORKSPACE = "/db/dirigible/users/local/workspace/";
	private static Map<String, String> prefixMap = Collections.synchronizedMap(new HashMap<String, String>());
	private static Map<String, String> prefixMapEquals = Collections.synchronizedMap(new HashMap<String, String>());
	
	private static String workspaceRoot = "/";
	
	public static String getMappedName(String repositoryName) throws IOException {
		String workspaceName = null;
		
		if (repositoryName != null
				&& !"".equals(repositoryName)) {

			if (FileUtils.exists(repositoryName)
					&& !"/".equals(repositoryName)) {
				return repositoryName;
			}
			String repositoryPathEffect = repositoryName.substring(1);
			repositoryPathEffect = repositoryPathEffect.replace(File.separator, IRepository.SEPARATOR);
			if (FileUtils.exists(repositoryPathEffect)) {
				return repositoryPathEffect;
			}
			
			check();
			
			if (repositoryName.startsWith(workspaceRoot)
					&& repositoryName.length() > workspaceRoot.length()) {
				repositoryName = IRepository.SEPARATOR + repositoryName.substring(workspaceRoot.length() + 1);
			}
			
			repositoryName = repositoryName.replace(File.separator, IRepository.SEPARATOR);
			
			for (Entry<String, String> prefix: prefixMapEquals.entrySet()) {
				if (repositoryName.equals(prefix.getKey())) {
//					workspaceName = repositoryName.replace(prefix.getKey(), prefix.getValue());
					workspaceName = prefix.getValue();
					break;
				}
			}
			
			if (workspaceName == null) {
				for (Entry<String, String> prefix: prefixMap.entrySet()) {
					if (repositoryName.startsWith(prefix.getKey())
							&& !prefix.getKey().equals("/")) {
						workspaceName = repositoryName.replace(prefix.getKey(), prefix.getValue());
						break;
					}
				}
			}
		}
		
		if (workspaceName == null) {
			workspaceName = repositoryName;
//			throw new IOException("No workspace mapping for file: " + repositoryName);
		} else {
			workspaceName = workspaceName.replace(IRepository.SEPARATOR, File.separator);
		}
		
		return workspaceName;
	}

	public static String getReverseMappedName(String workspaceName) {
		if (workspaceName == null
				|| "".endsWith(workspaceName)) {
			return workspaceName;
		}
		if (workspaceName.startsWith(DB_DIRIGIBLE_USERS_LOCAL_WORKSPACE)) {
			return workspaceName;
		}
		workspaceName = workspaceName.substring(workspaceRoot.length());
		workspaceName = workspaceName.replace(File.separator, IRepository.SEPARATOR);
		return workspaceName;
	}

	private static void check() throws IOException {
		if (prefixMap.isEmpty()) {
			
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			File workspaceDirectory = workspace.getRoot().getLocation().toFile();
			workspaceRoot = workspaceDirectory.getCanonicalPath();
			
			prefixMap.put("/db/dirigible/users/local/workspace", workspaceRoot);
			prefixMap.put(workspaceRoot + "/db/dirigible/users/local/workspace", workspaceRoot);
			
			String db_dirigible_root = workspaceRoot + File.separator + "db" + File.separator + "dirigible" + File.separator;
			
			prefixMap.put("/db/dirigible/registry", db_dirigible_root + "registry");
			prefixMap.put("/db/dirigible/sandbox", db_dirigible_root + "sandbox");
			prefixMap.put("/db/dirigible/templates", db_dirigible_root + "templates");
			
			prefixMapEquals.put("/", workspaceRoot);
			prefixMapEquals.put("/db", workspaceRoot + File.separator + "db");
			prefixMapEquals.put("/db/dirigible", workspaceRoot + File.separator + "db" + File.separator + "dirigible");
			prefixMapEquals.put("/db/dirigible/users", db_dirigible_root + "users");
			prefixMapEquals.put("/db/dirigible/users/local", db_dirigible_root + "users" + File.separator + "local" );
			
			prefixMapEquals.put("/db/dirigible/registry", db_dirigible_root + "registry");
			prefixMapEquals.put("/db/dirigible/sandbox", db_dirigible_root + "sandbox");
			prefixMapEquals.put("/db/dirigible/templates", db_dirigible_root + "templates");
			prefixMapEquals.put("/db/dirigible/default.content", db_dirigible_root + "default.content");
			
		}

	}

}
