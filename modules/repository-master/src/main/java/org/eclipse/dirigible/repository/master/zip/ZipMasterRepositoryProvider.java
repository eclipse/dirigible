/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.master.zip;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.repository.master.IMasterRepository;
import org.eclipse.dirigible.repository.master.IMasterRepositoryProvider;

/**
 * The Provider for Master Zip based Repository
 */
public class ZipMasterRepositoryProvider implements IMasterRepositoryProvider {

	private static final Logger logger = Logger.getLogger(ZipMasterRepositoryProvider.class);

	public static final String TYPE = "zip";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public IMasterRepository createMasterRepository() {

		logger.debug("creating Zip Master Repository...");

		String zip = Configuration.get(IMasterRepository.DIRIGIBLE_MASTER_REPOSITORY_ZIP_LOCATION);
		ZipMasterRepository zipMasterRepository = new ZipMasterRepository(zip);

		logger.debug("Zip Mater Repository created.");

		return zipMasterRepository;
	}

	

}
