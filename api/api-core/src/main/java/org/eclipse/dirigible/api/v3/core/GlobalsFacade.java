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
package org.eclipse.dirigible.api.v3.core;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The GlobalsFacade is used to store and retrieve values per name
 */
public class GlobalsFacade implements IScriptingFacade {

	private static final Logger logger = LoggerFactory.getLogger(GlobalsFacade.class);

	/**
	 * Gets the value per name.
	 *
	 * @param name
	 *            the name
	 * @return the string value
	 */
	public static final String get(String name) {
		logger.trace("API - GlobalsFacade.get() -> begin");
		String value = System.getProperty(name);
		logger.trace("API - GlobalsFacade.get() -> end");
		return value;
	}

	/**
	 * Sets the value per name.
	 *
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 */
	public static final void set(String name, String value) {
		logger.trace("API - GlobalsFacade.set() -> begin");
		System.setProperty(name, value);
		logger.trace("API - GlobalsFacade.set() -> end");
	}

	/**
	 * List all the name value pairs.
	 *
	 * @return the string
	 */
	public static final String list() {
		logger.trace("API - GlobalsFacade.get() -> begin");
		String value = GsonHelper.GSON.toJson(System.getProperties());
		logger.trace("API - GlobalsFacade.get() -> end");
		return value;
	}

}
