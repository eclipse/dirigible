/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.core.security.synchronizer;

import java.io.IOException;

import org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.security.api.ISecurityCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Security Classpath Content Handler.
 */
public class SecurityClasspathContentHandler extends AbstractClasspathContentHandler {

	private static final Logger logger = LoggerFactory.getLogger(SecurityClasspathContentHandler.class);

	private SecuritySynchronizer extensionsSynchronizer = StaticInjector.getInjector().getInstance(SecuritySynchronizer.class);

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler#isValid(java.lang.String)
	 */
	@Override
	protected boolean isValid(String path) {
		boolean isValid = false;

		try {
			if (path.endsWith(ISecurityCoreService.FILE_EXTENSION_ACCESS)) {
				isValid = true;
				extensionsSynchronizer.registerPredeliveredAccess(path);
			}
			if (path.endsWith(ISecurityCoreService.FILE_EXTENSION_ROLES)) {
				isValid = true;
				extensionsSynchronizer.registerPredeliveredRoles(path);
			}
		} catch (IOException e) {
			logger.error("Predelivered Security Access or Roles artifact is not valid", e);
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
