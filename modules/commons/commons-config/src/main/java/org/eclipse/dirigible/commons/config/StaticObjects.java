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
package org.eclipse.dirigible.commons.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StaticObjects {
	
	private static final Logger logger = LoggerFactory.getLogger(StaticObjects.class);
	
	public static final String DATASOURCE = "DATASOURCE"; // DataSource
	
	public static final String SYSTEM_DATASOURCE = "SYSTEM_DATASOURCE"; // System DataSource
	
	public static final String DATABASE = "DATABASE"; // IDatabase
	
	public static final String REPOSITORY = "REPOSITORY"; // IRepository
	
	public static final String MASTER_REPOSITORY = "MASTER_REPOSITORY"; // IMasterRepository
	
	public static final String DATABASE_REPOSITORY = "DATABASE_REPOSITORY"; // DatabaseRepository
	
	public static final String LOCAL_REPOSITORY = "LOCAL_REPOSITORY"; // DatabaseRepository
	
	public static final String CMS_PROVIDER = "CMS_PROVIDER"; // ICmsProvider
	
	public static final String CMS_DATABASE_REPOSITORY = "CMS_DATABASE_REPOSITORY"; // CmsDatabaseRepository
	
	public static final String JAVASCRIPT_ENGINE = "JAVASCRIPT_ENGINE"; // IJavascriptEngineExecutor
	
	public static final String WEBSOCKET_HANDLER = "WEBSOCKET_HANDLER"; // WebsocketHandler
	
	public static final String BPM_PROVIDER = "BPM_PROVIDER"; // IBpmProvider
	
	
	
	private final static Map<String, Object> OBJECTS = Collections.synchronizedMap(new HashMap<String, Object>());
	
	public static final Object get(String key) {
		logger.trace("Getting static object by key: " + key);
		Object object = OBJECTS.get(key);
		if (object == null) {
			String message = "Static object by key: " + key + " is null";
			logger.error(message);
			new Exception(message).printStackTrace();
		}
		return object;
	}
	
	public static final void set(String key, Object object) {
		logger.info("Setting static object by key: " + key);
		OBJECTS.put(key, object);
	}
	
	public static final boolean exists(String key) {
		logger.trace("Exists static object by key: " + key);
		Object object = OBJECTS.get(key);
		if (object == null) {
			return false;
		}
		return true;
	}

}
