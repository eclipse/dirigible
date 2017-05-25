/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.master;

import java.util.ServiceLoader;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.repository.api.RepositoryCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MasterRepositoryFactory {

	private static final Logger logger = LoggerFactory.getLogger(MasterRepositoryFactory.class.getCanonicalName());

	private ServiceLoader<IMasterRepositoryProvider> masterRepositoryProviders = ServiceLoader.load(IMasterRepositoryProvider.class);

	/**
	 * Create a Master Repository from which the content can be synchronized as initial load or reset
	 *
	 * @param parameters
	 * @return master repository
	 * @throws RepositoryCreationException
	 */
	public IMasterRepository createMasterRepository() throws RepositoryCreationException {
		String defaultMasterRepositoryProvider = Configuration.get(IMasterRepository.DIRIGIBLE_MASTER_REPOSITORY_PROVIDER);
		if (defaultMasterRepositoryProvider != null) {
			for (IMasterRepositoryProvider masterRepositoryProvider : masterRepositoryProviders) {
				if (masterRepositoryProvider.getType().equals(defaultMasterRepositoryProvider)) {
					logger.info(String.format("Master Repository Provider used is: %s", masterRepositoryProvider.getType()));
					IMasterRepository masterRepository = masterRepositoryProvider.createMasterRepository();
					masterRepository.initialize();
					return masterRepository;
				}
		    }
		}

		logger.info("Master Repository Provider not found");
		return null;
	}
	
}
