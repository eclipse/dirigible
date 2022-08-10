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
package org.eclipse.dirigible.engine.messaging.synchronizer;

import java.io.IOException;

import org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler;
import org.eclipse.dirigible.core.messaging.api.IMessagingCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class MessagingClasspathContentHandler.
 */
public class MessagingClasspathContentHandler extends AbstractClasspathContentHandler {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(MessagingClasspathContentHandler.class);

	/** The messaging synchronizer. */
	private MessagingSynchronizer messagingSynchronizer = new MessagingSynchronizer();

	/**
	 * Checks if is valid.
	 *
	 * @param path the path
	 * @return true, if is valid
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler#isValid(java.lang.String)
	 */
	@Override
	protected boolean isValid(String path) {
		try {
			if (path.endsWith(IMessagingCoreService.FILE_EXTENSION_LISTENER)) {
				messagingSynchronizer.registerPredeliveredListener(path);
				return true;
			}
		} catch (IOException e) {
			logger.error("Predelivered Listener at path [" + path + "] is not valid", e);
		}

		return false;
	}

	/**
	 * Gets the logger.
	 *
	 * @return the logger
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return logger;
	}

}
