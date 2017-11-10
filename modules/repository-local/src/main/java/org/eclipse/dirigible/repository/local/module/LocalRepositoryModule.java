/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.repository.local.module;

import org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.local.LocalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Module for managing Local Repository instantiation and binding
 */
public class LocalRepositoryModule extends AbstractDirigibleModule {
	
	private static final Logger logger = LoggerFactory.getLogger(LocalRepositoryModule.class);
	
	private static final String MODULE_NAME = "Local Repository Module";

	@Override
	protected void configure() {
		Configuration.load("/dirigible-repository-local.properties");
		String repositoryProvider = Configuration.get(IRepository.DIRIGIBLE_REPOSITORY_PROVIDER, IRepository.DIRIGIBLE_REPOSITORY_PROVIDER_LOCAL);

		if (LocalRepository.TYPE.equals(repositoryProvider)) {
			bind(IRepository.class).toInstance(createInstance());
			logger.info("Bound Local Repository as the Repository for this instance.");
		}
	}

	private IRepository createInstance() {
		logger.debug("creating Local Repository...");
		String rootFolder = Configuration.get(LocalRepository.DIRIGIBLE_LOCAL_REPOSITORY_ROOT_FOLDER);
		boolean absolute = Boolean.parseBoolean(Configuration.get(LocalRepository.DIRIGIBLE_LOCAL_REPOSITORY_ROOT_FOLDER_IS_ABSOLUTE));
		LocalRepository localRepository = new LocalRepository(rootFolder, absolute);
		logger.debug("Local Repository created.");
		return localRepository;
	}
	
	@Override
	public String getName() {
		return MODULE_NAME;
	}
}
