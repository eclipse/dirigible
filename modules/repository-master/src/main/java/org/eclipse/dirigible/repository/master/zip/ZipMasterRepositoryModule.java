/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.repository.master.zip;

import org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.repository.master.IMasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Module for managing ZIP Repository instantiation and binding
 */
public class ZipMasterRepositoryModule extends AbstractDirigibleModule {

	private static final Logger logger = LoggerFactory.getLogger(ZipMasterRepositoryModule.class);

	private static final String MODULE_NAME = "Zip Master Repository Module";
	
	@Override
	protected void configure() {
		Configuration.load("/dirigible-repository-master-zip.properties");
		String repositoryProvider = Configuration.get(IMasterRepository.DIRIGIBLE_MASTER_REPOSITORY_PROVIDER);

		if (ZipMasterRepository.TYPE.equals(repositoryProvider)) {
			bind(IMasterRepository.class).toInstance(createInstance());
			logger.info("Bound Zip Repository as the Master Repository for this instance.");
		}
	}

	private IMasterRepository createInstance() {
		logger.debug("creating Zip Master Repository...");
		String zip = Configuration.get(ZipMasterRepository.DIRIGIBLE_MASTER_REPOSITORY_ZIP_LOCATION);
		ZipMasterRepository zipMasterRepository = new ZipMasterRepository(zip);
		logger.debug("Zip Mater Repository created.");
		return zipMasterRepository;
	}
	
	@Override
	public String getName() {
		return MODULE_NAME;
	}

}
