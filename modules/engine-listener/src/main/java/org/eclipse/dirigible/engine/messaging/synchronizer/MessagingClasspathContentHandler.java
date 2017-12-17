/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.engine.messaging.synchronizer;

import java.io.IOException;

import org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.messaging.api.IMessagingCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class MessagingClasspathContentHandler.
 */
public class MessagingClasspathContentHandler extends AbstractClasspathContentHandler {

	private static final Logger logger = LoggerFactory.getLogger(MessagingClasspathContentHandler.class);

	private MessagingSynchronizer messagingSynchronizer = StaticInjector.getInjector().getInstance(MessagingSynchronizer.class);

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler#isValid(java.lang.String)
	 */
	@Override
	protected boolean isValid(String path) {
		boolean isValid = false;

		try {
			if (path.endsWith(IMessagingCoreService.FILE_EXTENSION_LISTENER)) {
				isValid = true;
				messagingSynchronizer.registerPredeliveredListener(path);
			}
		} catch (IOException e) {
			logger.error("Predelivered Listener is not valid", e);
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
