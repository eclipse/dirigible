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

import java.util.Map;

import org.eclipse.dirigible.repository.api.IMasterRepository;
import org.eclipse.dirigible.repository.api.IMasterRepositoryProvider;
import org.eclipse.dirigible.repository.logging.Logger;

/**
 * The Provider for Master Zip based Repository
 */
public class ZipMasterRepositoryProvider implements IMasterRepositoryProvider {

	private static final Logger logger = Logger.getLogger(ZipMasterRepositoryProvider.class);

	private static final String PARAM_USER = "user";
	private static final String PARAM_ZIP = "masterRepositoryZipLocation";
	public static final String TYPE = "zip";

	@Override
	public IMasterRepository createRepository(Map<String, Object> parameters) {

		logger.debug("creating Zip Master Repository...");

		String user = (String) parameters.get(PARAM_USER);
		String zip = (String) parameters.get(PARAM_ZIP);
		if (zip == null) {
			zip = System.getProperty(PARAM_ZIP);
		}
		ZipMasterRepository zipMasterRepository = new ZipMasterRepository(user, zip);

		logger.debug("Zip Mater Repository created.");

		return zipMasterRepository;
	}

	@Override
	public String getType() {
		return TYPE;
	}

}
