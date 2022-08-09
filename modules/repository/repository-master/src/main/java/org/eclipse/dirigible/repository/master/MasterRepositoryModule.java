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
package org.eclipse.dirigible.repository.master;

import java.io.IOException;

import org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.repository.api.IMasterRepository;
import org.eclipse.dirigible.repository.local.LocalRepositoryException;
import org.eclipse.dirigible.repository.local.module.DummyMasterRepository;
import org.eclipse.dirigible.repository.master.fs.FileSystemMasterRepository;
import org.eclipse.dirigible.repository.master.jar.JarMasterRepository;
import org.eclipse.dirigible.repository.master.zip.ZipMasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Module for managing Master Repository instantiation and binding.
 */
public class MasterRepositoryModule extends AbstractDirigibleModule {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(MasterRepositoryModule.class);

	/** The Constant MODULE_NAME. */
	private static final String MODULE_NAME = "Master Repository Module";

	/**
	 * Configure.
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule#getName()
	 */
	@Override
	public void configure() {
		
		String masterType = Configuration.get(IMasterRepository.DIRIGIBLE_MASTER_REPOSITORY_PROVIDER);
	
		if (masterType == null) {
			return;
		}

		if (masterType.equals(FileSystemMasterRepository.TYPE)) {
			Configuration.loadModuleConfig("/dirigible-repository-master-fs.properties");
			StaticObjects.set(StaticObjects.MASTER_REPOSITORY, createFileSystemInstance());
			logger.info("Bound File System Repository as the Master Repository for this instance.");
		} else if (masterType.equals(JarMasterRepository.TYPE)) {
			Configuration.loadModuleConfig("/dirigible-repository-master-jar.properties");
			StaticObjects.set(StaticObjects.MASTER_REPOSITORY, createJarInstance());
			logger.info("Bound Jar Repository as the Master Repository for this instance.");
		} else if (masterType.equals(ZipMasterRepository.TYPE)) {
			Configuration.loadModuleConfig("/dirigible-repository-master-zip.properties");
			StaticObjects.set(StaticObjects.MASTER_REPOSITORY, createZipInstance());
			logger.info("Bound Zip Repository as the Master Repository for this instance.");
		} else if (masterType != null) {
			StaticObjects.set(StaticObjects.MASTER_REPOSITORY, new DummyMasterRepository());
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

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule#getName()
	 */
	@Override
	public String getName() {
		return MODULE_NAME;
	}
	
	/**
	 * Gets the priority.
	 *
	 * @return the priority
	 */
	@Override
	public int getPriority() {
		return PRIORITY_REPOSITORY;
	}

}
