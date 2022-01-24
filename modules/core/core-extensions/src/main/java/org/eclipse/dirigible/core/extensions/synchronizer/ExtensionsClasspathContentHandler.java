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
package org.eclipse.dirigible.core.extensions.synchronizer;

import java.io.IOException;

import org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler;
import org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ExtensionsClasspathContentHandler.
 */
public class ExtensionsClasspathContentHandler extends AbstractClasspathContentHandler {

	private static final Logger logger = LoggerFactory.getLogger(ExtensionsClasspathContentHandler.class);

	private ExtensionsSynchronizer extensionsSynchronizer = new ExtensionsSynchronizer();

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler#isValid(java.lang.String)
	 */
	@Override
	protected boolean isValid(String path) {
		boolean isValid = false;

		try {
			if (path.endsWith(IExtensionsCoreService.FILE_EXTENSION_EXTENSIONPOINT)) {
				isValid = true;
				extensionsSynchronizer.registerPredeliveredExtensionPoint(path);
			}

			if (path.endsWith(IExtensionsCoreService.FILE_EXTENSION_EXTENSION)) {
				isValid = true;
				extensionsSynchronizer.registerPredeliveredExtension(path);
			}
		} catch (IOException e) {
			logger.error("Predelivered Extension Point or Extension is not valid", e);
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
