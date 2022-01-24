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
package org.eclipse.dirigible.core.scheduler.repository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IMasterRepository;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initializer of the Local Repository form the Master one, if configured
 *
 */
public class MasterToRepositoryInitializer {
	
	private static final Logger logger = LoggerFactory.getLogger(MasterToRepositoryInitializer.class);
	
	private IMasterRepository masterRepository = (IMasterRepository) StaticObjects.get(StaticObjects.MASTER_REPOSITORY);
	
	private IRepository repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
	
	/**
	 * Initialize the Repository from the Master Repository, if configured
	 *
	 * @throws SQLException
	 *             the SQL exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void initialize() throws SQLException, IOException {
		if (this.masterRepository != null && this.masterRepository.hasCollection("/")) {
			if (this.repository != null) {
				copyRepository(masterRepository, repository);
			} else {
				logger.error("No Repository has been initialized.");
			}
		} else {
			logger.info("No Master Repository has been initialized.");
		}
	}
	
	private void copyRepository(IMasterRepository sourceRepository, IRepository targetRepository) throws IOException {
		// Copy from Master to Local
		ICollection root = sourceRepository.getRoot();
		copyCollection(root, targetRepository);
	}

	private void copyCollection(ICollection parent, IRepository targetRepository) throws IOException {
		List<IEntity> entities = parent.getChildren();
		for (IEntity entity : entities) {
			if (entity instanceof ICollection) {
				ICollection collection = (ICollection) entity;
				copyCollection(collection, targetRepository);
			} else {
				IResource resource = (IResource) entity;
				try {
					targetRepository.createResource(resource.getPath(), resource.getContent(), resource.isBinary(), resource.getContentType(), true);
					logger.info(String.format("Initial copy from the Mater Repository of the Resource: %s", resource.getPath()));
				} catch (Exception e) {
					logger.info(String.format("Failed initial copy from the Mater Repository of the Resource: %s", resource.getPath()));
					logger.error(e.getMessage());
				}
			}
		}
	}

}
