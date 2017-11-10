/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.api.v3.core;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvFacade implements IScriptingFacade {

	private static final Logger logger = LoggerFactory.getLogger(EnvFacade.class);

	public static final String get(String name) {
		logger.trace("API - EnvFacade.get() -> begin");
		String value = System.getenv(name);
		logger.trace("API - EnvFacade.get() -> end");
		return value;
	}

	public static final String list() {
		logger.trace("API - EnvFacade.get() -> begin");
		String value = GsonHelper.GSON.toJson(System.getenv());
		logger.trace("API - EnvFacade.get() -> end");
		return value;
	}

}
