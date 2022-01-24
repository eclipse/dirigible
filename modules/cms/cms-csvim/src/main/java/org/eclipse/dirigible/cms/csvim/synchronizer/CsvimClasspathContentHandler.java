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
package org.eclipse.dirigible.cms.csvim.synchronizer;

import java.io.IOException;

import org.eclipse.dirigible.cms.csvim.api.ICsvimCoreService;
import org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class CSVIM ClasspathContentHandler.
 */
public class CsvimClasspathContentHandler extends AbstractClasspathContentHandler {

	private static final Logger logger = LoggerFactory.getLogger(CsvimClasspathContentHandler.class);

	private CsvimSynchronizer csvimSynchronizer = new CsvimSynchronizer();

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler#isValid(java.lang.String)
	 */
	@Override
	protected boolean isValid(String path) {
		boolean isValid = false;

		try {
			if (path.endsWith(ICsvimCoreService.FILE_EXTENSION_CSVIM)) {
				isValid = true;
				csvimSynchronizer.registerPredeliveredCsvim(path);
			}
			if (path.endsWith(ICsvimCoreService.FILE_EXTENSION_CSV)) {
				isValid = true;
				csvimSynchronizer.registerPredeliveredCsv(path);
			}

		} catch (IOException e) {
			logger.error("Predelivered CSVIM is not valid", e);
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
