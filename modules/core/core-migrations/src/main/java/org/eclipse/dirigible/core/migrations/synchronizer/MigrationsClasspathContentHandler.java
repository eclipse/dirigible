/**
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.core.migrations.synchronizer;

import java.io.IOException;

import org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.migrations.api.IMigrationsCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Migrations Classpath Content Handler.
 */
public class MigrationsClasspathContentHandler extends AbstractClasspathContentHandler {

	private static final Logger logger = LoggerFactory.getLogger(MigrationsClasspathContentHandler.class);

	private MigrationsSynchronizer migrationsSynchronizer = StaticInjector.getInjector().getInstance(MigrationsSynchronizer.class);

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler#isValid(java.lang.String)
	 */
	@Override
	protected boolean isValid(String path) {
		boolean isValid = false;

		try {
			if (path.endsWith(IMigrationsCoreService.FILE_EXTENSION_MIGRATE)) {
				isValid = true;
				migrationsSynchronizer.registerPredeliveredMigrations(path);
			}
		} catch (IOException e) {
			logger.error("Predelivered Migrations artifact is not valid", e);
		}

		return isValid;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return logger;
	}

}
