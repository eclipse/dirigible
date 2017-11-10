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

public class GlobalsFacade implements IScriptingFacade {

	private static final Logger logger = LoggerFactory.getLogger(GlobalsFacade.class);

	public static final String get(String name) {
		logger.trace("API - GlobalsFacade.get() -> begin");
		String value = System.getProperty(name);
		logger.trace("API - GlobalsFacade.get() -> end");
		return value;
	}

	public static final void set(String name, String value) {
		logger.trace("API - GlobalsFacade.set() -> begin");
		System.setProperty(name, value);
		logger.trace("API - GlobalsFacade.set() -> end");
	}

	public static final String list() {
		logger.trace("API - GlobalsFacade.get() -> begin");
		String value = GsonHelper.GSON.toJson(System.getProperties());
		logger.trace("API - GlobalsFacade.get() -> end");
		return value;
	}

}
