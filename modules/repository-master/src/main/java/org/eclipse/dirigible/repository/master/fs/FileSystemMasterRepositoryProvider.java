/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.master.fs;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.repository.master.IMasterRepository;
import org.eclipse.dirigible.repository.master.IMasterRepositoryProvider;

/**
 * The Provider for Master File System based Repository
 */
public class FileSystemMasterRepositoryProvider implements IMasterRepositoryProvider {
	
	private static final Logger logger = Logger.getLogger(FileSystemMasterRepositoryProvider.class);

	public static final String TYPE = "filesystem";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public IMasterRepository createMasterRepository() {
		logger.debug("creating FileSystem Master Repository...");
		
		String rootFolder = Configuration.get(IMasterRepository.DIRIGIBLE_MASTER_REPOSITORY_ROOT_FOLDER);
		FileSystemMasterRepository fileSystemMasterRepository = new FileSystemMasterRepository(rootFolder);
		
		logger.debug("FileSystem Mater Repository created.");
		
		return fileSystemMasterRepository;
	}

	

}
