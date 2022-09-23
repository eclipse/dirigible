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
 * The EnvFacade is used to retrieve the environment variables.
 */
public class EnvFacade implements IScriptingFacade {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(EnvFacade.class);

	/**
	 * Gets the environment variable by name.
	 *
	 * @param name
	 *            the name
	 * @return the string
	 */
	public static final String get(String name) {
		if (logger.isTraceEnabled()) {logger.trace("API - EnvFacade.get() -> begin");}
		String value = System.getenv(name);
		if (logger.isTraceEnabled()) {logger.trace("API - EnvFacade.get() -> end");}
		return value;
	}

	/**
	 * List all the environment variables.
	 *
	 * @return the string
	 */
	public static final String list() {
		if (logger.isTraceEnabled()) {logger.trace("API - EnvFacade.get() -> begin");}
		String value = GsonHelper.GSON.toJson(System.getenv());
		if (logger.isTraceEnabled()) {logger.trace("API - EnvFacade.get() -> end");}
		return value;
	}

}
