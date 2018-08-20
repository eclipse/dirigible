/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.repository.master;

import java.io.IOException;

import org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.repository.api.IMasterRepository;
import org.eclipse.dirigible.repository.local.LocalRepositoryException;
import org.eclipse.dirigible.repository.master.fs.FileSystemMasterRepository;
import org.eclipse.dirigible.repository.master.jar.JarMasterRepository;
import org.eclipse.dirigible.repository.master.zip.ZipMasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Module for managing Master Repository instantiation and binding.
 */
public class MasterRepositoryModule extends AbstractDirigibleModule {

	private static final Logger logger = LoggerFactory.getLogger(MasterRepositoryModule.class);

	private static final String MODULE_NAME = "Master Repository Module";

	/*
	 * (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		
		String masterType = Configuration.get(IMasterRepository.DIRIGIBLE_MASTER_REPOSITORY_PROVIDER);
		
		if (masterType == null) {
			// no master repository provider configured - default will not be chosen
			logger.warn("No master repository provider configured.");
			bind(IMasterRepository.class).toInstance(new DummyMasterRepository());
			return;
		}
		
		if (masterType.equals(FileSystemMasterRepository.TYPE)) {
			Configuration.load("/dirigible-repository-master-fs.properties");
			bind(IMasterRepository.class).toInstance(createFileSystemInstance());
			logger.info("Bound File System Repository as the Master Repository for this instance.");
		} else if (masterType.equals(JarMasterRepository.TYPE)) {
			Configuration.load("/dirigible-repository-master-jar.properties");
			bind(IMasterRepository.class).toInstance(createJarInstance());
			logger.info("Bound Jar Repository as the Master Repository for this instance.");
		} else if (masterType.equals(ZipMasterRepository.TYPE)) {
			Configuration.load("/dirigible-repository-master-zip.properties");
			bind(IMasterRepository.class).toInstance(createZipInstance());
			logger.info("Bound Zip Repository as the Master Repository for this instance.");
		} else {
			bind(IMasterRepository.class).toInstance(new DummyMasterRepository());
			logger.error("Unknown master repository provider configured: " + masterType);
		}
		
	}

	/**
	 * Creates the instance of File System Mater Repository.
	 *
	 * @return the master repository
	 */
	private IMasterRepository createFileSystemInstance() {
		logger.debug("creating FileSystem Master Repository...");
		String rootFolder = Configuration.get(FileSystemMasterRepository.DIRIGIBLE_MASTER_REPOSITORY_ROOT_FOLDER);
		FileSystemMasterRepository fileSystemMasterRepository = new FileSystemMasterRepository(rootFolder);
		logger.debug("FileSystem Mater Repository created.");
		return fileSystemMasterRepository;
	}
	
	/**
	 * Creates the instance of Jar Master Repository.
	 *
	 * @return the master repository
	 */
	private IMasterRepository createJarInstance() {
		logger.debug("creating Jar Master Repository...");
		String jar = Configuration.get(JarMasterRepository.DIRIGIBLE_MASTER_REPOSITORY_JAR_PATH);
		JarMasterRepository jarMasterRepository;
		try {
			jarMasterRepository = new JarMasterRepository(jar);
		} catch (LocalRepositoryException | IOException e) {
			logger.error("Jar Mater Repository failed", e);
			throw new IllegalStateException(e);
		}
		logger.debug("Jar Mater Repository created.");
		return jarMasterRepository;
	}
	
	/**
	 * Creates the instance of Zip Master Repository.
	 *
	 * @return the master repository
	 */
	private IMasterRepository createZipInstance() {
		logger.debug("creating Zip Master Repository...");
		String zip = Configuration.get(ZipMasterRepository.DIRIGIBLE_MASTER_REPOSITORY_ZIP_LOCATION);
		ZipMasterRepository zipMasterRepository = new ZipMasterRepository(zip);
		logger.debug("Zip Mater Repository created.");
		return zipMasterRepository;
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
