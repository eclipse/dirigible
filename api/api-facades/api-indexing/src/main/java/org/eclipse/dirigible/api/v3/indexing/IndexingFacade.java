/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.api.v3.indexing;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.core.indexing.api.IIndexingCoreService;
import org.eclipse.dirigible.core.indexing.api.IndexingException;
import org.eclipse.dirigible.core.indexing.service.IndexingCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexingFacade implements IScriptingFacade {

	private static final Logger logger = LoggerFactory.getLogger(IndexingFacade.class);
	
	private static final IIndexingCoreService indexingCoreService = StaticInjector.getInjector().getInstance(IndexingCoreService.class);

	public static final void add(String index, String location, String contents, String lastModified, String parameters) throws IndexingException {
		Map map = GsonHelper.GSON.fromJson(parameters, Map.class);
		indexingCoreService.add(index, location, contents.getBytes(), Long.parseLong(lastModified), map);
	}
	
	public static final String search(String index, String term) throws IndexingException {
		return indexingCoreService.search(index, term);
	}
	
	public static final String before(String index, String date) throws IndexingException {
		return indexingCoreService.before(index, Long.parseLong(date));
	}
	
	public static final String after(String index, String date) throws IndexingException {
		return indexingCoreService.after(index, Long.parseLong(date));
	}
	
	public static final String between(String index, String lower, String upper) throws IndexingException {
		return indexingCoreService.between(index, Long.parseLong(lower), Long.parseLong(upper));
	}
	
}
