/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.repository.master.fs;

import org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.repository.master.IMasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Module for managing File System Repository instantiation and binding.
 */
public class FileSystemMasterRepositoryModule extends AbstractDirigibleModule {

	private static final Logger logger = LoggerFactory.getLogger(FileSystemMasterRepositoryModule.class);

	private static final String MODULE_NAME = "File System Master Repository Module";

	/*
	 * (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		Configuration.load("/dirigible-repository-master-fs.properties");
		String repositoryProvider = Configuration.get(IMasterRepository.DIRIGIBLE_MASTER_REPOSITORY_PROVIDER);

		if (FileSystemMasterRepository.TYPE.equals(repositoryProvider)) {
			bind(IMasterRepository.class).toInstance(createInstance());
			logger.info("Bound File System Repository as the Master Repository for this instance.");
		}
	}

	/**
	 * Creates the instance.
	 *
	 * @return the i master repository
	 */
	private IMasterRepository createInstance() {
		logger.debug("creating FileSystem Master Repository...");
		String rootFolder = Configuration.get(FileSystemMasterRepository.DIRIGIBLE_MASTER_REPOSITORY_ROOT_FOLDER);
		FileSystemMasterRepository fileSystemMasterRepository = new FileSystemMasterRepository(rootFolder);
		logger.debug("FileSystem Mater Repository created.");
		return fileSystemMasterRepository;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule#getName()
	 */
	@Override
	public String getName() {
		return MODULE_NAME;
	}

}
