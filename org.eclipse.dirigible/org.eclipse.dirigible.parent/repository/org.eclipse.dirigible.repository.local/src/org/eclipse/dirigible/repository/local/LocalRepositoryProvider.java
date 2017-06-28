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

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryProvider;

public class LocalRepositoryProvider implements IRepositoryProvider {

	private static final String PARAM_USER = "user";
	public static final String TYPE = "local";

	@Override
	public IRepository createRepository(Map<String, Object> parameters) {

		String user = (String) parameters.get(PARAM_USER);
		String rootFolder = (String) parameters.get(ICommonConstants.INIT_PARAM_LOCAL_REPOSITORY_ROOT_FOLDER);
		boolean absolute = Boolean.parseBoolean((String) parameters.get(ICommonConstants.INIT_PARAM_LOCAL_REPOSITORY_ROOT_FOLDER_IS_ABSOLUTE));
		String repositoryRootFolderName = (String) parameters.get(ICommonConstants.INIT_PARAM_LOCAL_REPOSITORY_ROOT_FOLDER_NAME);

		LocalRepository repository = new LocalRepository(user, rootFolder, repositoryRootFolderName, absolute);
		return repository;
	}

	@Override
	public String getType() {
		return TYPE;
	}

}
