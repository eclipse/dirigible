/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.extensions.synchronizer;

import java.io.IOException;

import org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class ExtensionsClasspathContentHandler.
 */
public class ExtensionsClasspathContentHandler extends AbstractClasspathContentHandler {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ExtensionsClasspathContentHandler.class);
	
	/** The extensions synchronizer. */
	private ExtensionsSynchronizer extensionsSynchronizer = StaticInjector.getInjector().getInstance(ExtensionsSynchronizer.class);
	
	/* (non-Javadoc)
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return logger;
	}


}
