/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.master.jar;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.repository.master.IMasterRepository;
import org.eclipse.dirigible.repository.master.IMasterRepositoryProvider;

/**
 * The Provider for Master Jar based Repository
 */
public class JarMasterRepositoryProvider implements IMasterRepositoryProvider {

	private static final Logger logger = Logger.getLogger(JarMasterRepositoryProvider.class);

	public static final String TYPE = "jar";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public IMasterRepository createMasterRepository() {

		logger.debug("creating Jar Master Repository...");

		String jar = Configuration.get(IMasterRepository.DIRIGIBLE_MASTER_REPOSITORY_JAR_PATH);
		JarMasterRepository jarMasterRepository = new JarMasterRepository(jar);

		logger.debug("Jar Mater Repository created.");

		return jarMasterRepository;
	}

	

}
