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
package org.eclipse.dirigible.engine.odata2.synchronizer;

import java.io.IOException;

import org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler;
import org.eclipse.dirigible.engine.odata2.api.IODataCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ODataClasspathContentHandler.
 */
public class ODataClasspathContentHandler extends AbstractClasspathContentHandler {

	private static final Logger logger = LoggerFactory.getLogger(ODataClasspathContentHandler.class);

	private ODataSynchronizer odataSynchronizer = new ODataSynchronizer();

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler#isValid(java.lang.String)
	 */
	@Override
	protected boolean isValid(String path) {
		boolean isValid = false;

		try {
			if (path.endsWith(IODataCoreService.FILE_EXTENSION_ODATA_SCHEMA)) {
				isValid = true;
				odataSynchronizer.registerPredeliveredSchema(path);
			}

			if (path.endsWith(IODataCoreService.FILE_EXTENSION_ODATA_MAPPING)) {
				isValid = true;
				odataSynchronizer.registerPredeliveredMapping(path);
			}
		} catch (IOException e) {
			logger.error("Predelivered OData Schema or Mapping is not valid", e);
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
