/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.wiki;

import java.io.IOException;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.web.WebExecutor;

public class WikiExecutor extends WebExecutor {
	
	private static final Logger logger = Logger.getLogger(WikiExecutor.class);


	public WikiExecutor(IRepository repository, String... rootPaths) {
		super(repository, rootPaths);
	}

	@Override
	protected byte[] preprocessContent(byte[] rawContent, IEntity entity)
			throws IOException {
		return WikiProcessor.processContent(rawContent, entity);
	}

	@Override
	protected String getModuleType(String path) {
		return ICommonConstants.ARTIFACT_TYPE.WIKI_CONTENT;
	}
}
