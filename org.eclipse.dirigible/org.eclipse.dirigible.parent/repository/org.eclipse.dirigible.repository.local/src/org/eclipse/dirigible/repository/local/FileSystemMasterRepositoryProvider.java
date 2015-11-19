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

import java.util.Map;

import org.eclipse.dirigible.repository.api.IMasterRepository;
import org.eclipse.dirigible.repository.api.IMasterRepositoryProvider;

/**
 * The Provider for Master File System based Repository
 */
public class FileSystemMasterRepositoryProvider implements IMasterRepositoryProvider {

	private static final String PARAM_USER = "user";
	private static final String PARAM_FOLDER = "folder";

	@Override
	public IMasterRepository createRepository(Map<String, Object> parameters) {
		String user = (String) parameters.get(PARAM_USER);
		String folder = (String) parameters.get(PARAM_FOLDER);
		return new FileSystemMasterRepository(user, folder);
	}

}
