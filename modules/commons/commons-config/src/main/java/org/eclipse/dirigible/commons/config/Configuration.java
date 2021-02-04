/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.commons.config;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration Facade class keeps all the configurations in the Dirigible
 * instance It has the default built in properties file - dirigible.properties
 * After the initialization, all the default properties are replaced with the
 * ones coming as: 1. System's properties 2. Environment variables This can be
 * triggered programmatically with update() method It supports also loading of
 * custom properties files from the class loader with load() for the modules and
 * also merge with a provided properties object with add() methods
 */
public class Configuration {

	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

	private enum ConfigType {
		RUNTIME, ENVIRONMENT, DEPLOYMENT, MODULE
	}

	private static final Map<String, String> RUNTIME_VARIABLES = Collections.synchronizedMap(new HashMap<String, String>());
	private static final Map<String, String> ENVIRONMENT_VARIABLES = Collections.synchronizedMap(new HashMap<String, String>());
	private static final Map<String, String> DEPLOYMENT_VARIABLES = Collections.synchronizedMap(new HashMap<String, String>());
	private static final Map<String, String> MODULE_VARIABLES = Collections.synchronizedMap(new HashMap<String, String>());

	public static boolean LOADED = false;

	static {
		loadDeploymentConfig("/dirigible.properties");
		loadEnvironmentConfig();
		LOADED = true;
	}

	private static void loadEnvironmentConfig() {
		addConfigProperties(System.getenv(), ConfigType.ENVIRONMENT);
		addConfigProperties(System.getProperties(), ConfigType.ENVIRONMENT);
	}

	private static void loadDeploymentConfig(String path) {
		load(path, ConfigType.DEPLOYMENT);
	}

	public static void loadModuleConfig(String path) {
		load(path, ConfigType.MODULE);
	}

	private static void load(String path, ConfigType type) {
		try {
			Properties custom = new Properties();
			InputStream in = Configuration.class.getResourceAsStream(path);
			if (in == null) {
				throw new IOException(format("Configuration file {0} does not exist", path));
			}
			try {
				custom.load(in);
				switch (type) {
				case RUNTIME:
					addConfigProperties(custom, RUNTIME_VARIABLES);
					break;
				case ENVIRONMENT:
					addConfigProperties(custom, ENVIRONMENT_VARIABLES);
					break;
				case DEPLOYMENT:
					addConfigProperties(custom, DEPLOYMENT_VARIABLES);
					break;
				case MODULE:
					addConfigProperties(custom, MODULE_VARIABLES);
					break;
				default:
					break;
				}
			} finally {
				in.close();
			}
			logger.debug("Configuration loaded: " + path);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private static void addConfigProperties(Map<String, String> properties, ConfigType type) {
		switch(type) {
		case RUNTIME:
			RUNTIME_VARIABLES.putAll(properties);
			break;
		case ENVIRONMENT:
			ENVIRONMENT_VARIABLES.putAll(properties);
			break;
		case DEPLOYMENT:
			DEPLOYMENT_VARIABLES.putAll(properties);
			break;
		case MODULE:
			MODULE_VARIABLES.putAll(properties);
			break;
		default:
			break;
		
		}
	}

	private static void addConfigProperties(Properties properties, ConfigType type) {
		switch(type) {
		case RUNTIME:
			addConfigProperties(properties, RUNTIME_VARIABLES);
			break;
		case ENVIRONMENT:
			addConfigProperties(properties, ENVIRONMENT_VARIABLES);
			break;
		case DEPLOYMENT:
			addConfigProperties(properties, DEPLOYMENT_VARIABLES);
			break;
		case MODULE:
			addConfigProperties(properties, MODULE_VARIABLES);
			break;
		default:
			break;
		}
	}

	private static void addConfigProperties(Properties properties, Map<String, String> map) {
		for (String property: properties.stringPropertyNames()) {
			map.put(property, properties.getProperty(property));
		}
	}

	/**
	 * Getter for the value of the property by its key.
	 *
	 * @param key the key
	 * @return the string
	 */
	public static String get(String key) {
		return get(key, null);
	}

	/**
	 * Getter for the value of the property by its key.
	 *
	 * @param key          the key
	 * @param defaultValue the default value
	 * @return the string
	 */
	public static String get(String key, String defaultValue) {
		String value = null;
		if (RUNTIME_VARIABLES.containsKey(key)) {
			value = RUNTIME_VARIABLES.get(key);
		} else if (ENVIRONMENT_VARIABLES.containsKey(key)) {
			value = ENVIRONMENT_VARIABLES.get(key);
		} else if (DEPLOYMENT_VARIABLES.containsKey(key)) {
			value = DEPLOYMENT_VARIABLES.get(key);
		} else if (MODULE_VARIABLES.containsKey(key)) {
			value = MODULE_VARIABLES.get(key);
		}
		return (value != null) ? value : defaultValue;
	}

	/**
	 * Setter for the property's key and value.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	public static void set(String key, String value) {
		RUNTIME_VARIABLES.put(key, value);
	}

	/**
	 * Remove property
	 * 
	 * @param key the key
	 */
	public static void remove(String key) {
		RUNTIME_VARIABLES.remove(key);
	}

	/**
	 * Getter for all the keys.
	 *
	 * @return the keys
	 */
	public static String[] getKeys() {
		Set<String> keys = new HashSet<String>();
		keys.addAll(RUNTIME_VARIABLES.keySet());
		keys.addAll(ENVIRONMENT_VARIABLES.keySet());
		keys.addAll(DEPLOYMENT_VARIABLES.keySet());
		keys.addAll(MODULE_VARIABLES.keySet());
		return keys.toArray(new String[] {});
	}

	/**
	 * Update the properties values from the System's properties and from the
	 * Environment if any.
	 */
	public static void update() {
		loadEnvironmentConfig();
	}

	/**
	 * Checks if is anonymous mode enabled.
	 *
	 * @return true, if is anonymous mode enabled
	 */
	public static boolean isAnonymousModeEnabled() {
		try {
			Class.forName("org.eclipse.dirigible.runtime.anonymous.AnonymousAccess");
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if is anonymous user enabled.
	 *
	 * @return true, if is anonymous user enabled
	 */
	public static boolean isAnonymousUserEnabled() {
		try {
			Class.forName("org.eclipse.dirigible.anonymous.AnonymousUser");
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if is JWT mode enabled.
	 *
	 * @return true, if is JWT mode enabled
	 */
	public static boolean isJwtModeEnabled() {
		try {
			Class.forName("org.eclipse.dirigible.jwt.JwtAccess");
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if productive iframe is enabled.
	 *
	 * @return true, if productive iframe is enabled
	 */
	public static boolean isProductiveIFrameEnabled() {
		return Boolean.parseBoolean(Configuration.get("DIRIGIBLE_PRODUCTIVE_IFRAME_ENABLED", Boolean.TRUE.toString()));
	}

	/**
	 * Setter as a System's property.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	public static void setSystemProperty(String key, String value) {
		System.setProperty(key, value);
	}

}
