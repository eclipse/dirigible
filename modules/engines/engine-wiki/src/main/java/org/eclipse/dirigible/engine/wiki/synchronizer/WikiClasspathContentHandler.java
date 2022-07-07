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
package org.eclipse.dirigible.engine.wiki.synchronizer;

import java.io.IOException;

import org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler;
import org.eclipse.dirigible.engine.wiki.api.IWikiCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class WikiClasspathContentHandler.
 */
public class WikiClasspathContentHandler extends AbstractClasspathContentHandler {

	private static final Logger logger = LoggerFactory.getLogger(WikiClasspathContentHandler.class);

	private WikiSynchronizer wikiSynchronizer = new WikiSynchronizer();

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler#isValid(java.lang.String)
	 */
	@Override
	protected boolean isValid(String path) {
		try {
			if (path.endsWith(IWikiCoreService.FILE_EXTENSION_MARKDOWN)
					|| path.endsWith(IWikiCoreService.FILE_EXTENSION_MD)
					|| path.endsWith(IWikiCoreService.FILE_EXTENSION_CONFLUENCE)) {
				wikiSynchronizer.registerPredeliveredWiki(path);
				return true;
			}
		} catch (IOException e) {
			logger.error("Predelivered Wiki at path [" + path + "] is not valid", e);
		}

		return false;
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
