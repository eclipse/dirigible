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
package org.eclipse.dirigible.repository.local.module;

import org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.repository.api.IMasterRepository;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.local.LocalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Module for managing Local Repository instantiation and binding.
 */
public class LocalRepositoryModule extends AbstractDirigibleModule {

	private static final Logger logger = LoggerFactory.getLogger(LocalRepositoryModule.class);

	private static final String MODULE_NAME = "Local Repository Module";

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule#getName()
	 */
	@Override
	public void configure() {
		Configuration.loadModuleConfig("/dirigible-repository-local.properties");
		String repositoryProvider = Configuration.get(IRepository.DIRIGIBLE_REPOSITORY_PROVIDER);

		if (repositoryProvider == null) {
			throw new RuntimeException("No repository provider is configured, set the DIRIGIBLE_REPOSITORY_PROVIDER property. See more at: https://www.dirigible.io/help/setup_environment_variables.html");
		}

		if (LocalRepository.TYPE.equals(repositoryProvider)) {
			LocalRepository localRepository = createInstance();
			StaticObjects.set(StaticObjects.LOCAL_REPOSITORY, localRepository);
			StaticObjects.set(StaticObjects.REPOSITORY, localRepository);
			logger.info("Bound Local Repository as the Repository for this instance.");

			String masterType = Configuration.get(IMasterRepository.DIRIGIBLE_MASTER_REPOSITORY_PROVIDER);
			if (masterType == null) {
				StaticObjects.set(StaticObjects.MASTER_REPOSITORY, new DummyMasterRepository());
			}
		}
	}

	/**
	 * Creates the instance.
	 *
	 * @return the i repository
	 */
	private LocalRepository createInstance() {
		logger.debug("creating Local Repository...");
		String rootFolder = Configuration.get(LocalRepository.DIRIGIBLE_REPOSITORY_LOCAL_ROOT_FOLDER);
		boolean absolute = Boolean.parseBoolean(Configuration.get(LocalRepository.DIRIGIBLE_REPOSITORY_LOCAL_ROOT_FOLDER_IS_ABSOLUTE));
		LocalRepository localRepository = new LocalRepository(rootFolder, absolute);
		logger.debug("Local Repository created.");
		return localRepository;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule#getName()
	 */
	@Override
	public String getName() {
		return MODULE_NAME;
	}
	
	@Override
	public int getPriority() {
		return PRIORITY_REPOSITORY;
	}
}
