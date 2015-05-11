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

package org.eclipse.dirigible.ide.workspace.dual;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.ide.workspace.RemoteResourcesPlugin;
import org.eclipse.dirigible.ide.workspace.impl.Workspace;
import org.eclipse.dirigible.repository.api.IRepository;

public class RepositoryDataStore {

	public static final String PROJECT_NAME_SEPARATOR = "$"; //$NON-NLS-1$

	public static byte[] getByteArrayData(String fileName) throws IOException {
		IRepository repository = RepositoryFacade.getInstance().getRepository();
		Workspace workspace = (Workspace) RemoteResourcesPlugin.getWorkspace();
		String root = workspace
				.getRepositoryPathForWorkspace(RemoteResourcesPlugin
						.getUserName());
		fileName = fileName.substring(0, fileName.lastIndexOf('.'));
		List<String> projectNames = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(fileName,
				PROJECT_NAME_SEPARATOR);
		while (tokenizer.hasMoreElements()) {
			String token = (String) tokenizer.nextElement();
			projectNames.add(root + "/" + token); //$NON-NLS-1$
		}

		byte[] result = repository.exportZip(projectNames);
		return result;
	}

}
