/**
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.commons.config;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration Facade class keeps all the configurations in the Dirigible instance
 * It has the default built in properties file - dirigible.properties
 * After the initialization, all the default properties are replaced with the ones coming as:
 * 1. System's properties
 * 2. Environment variables
 * This can be triggered programmatically with update() method
 * It supports also loading of custom properties files from the class loader with load() for the modules
 * and also merge with a provided properties object with add() methods
 */
public class Configuration {

	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

	private static Configuration INSTANCE;

	private Map<String, String> parameters = Collections.synchronizedMap(new HashMap<String, String>());

	/**
	 * Loads a custom properties file from the class loader.
	 *
	 * @param path
	 *            the path
	 */
	public static void load(String path) {
		try {
			Properties custom = new Properties();
			InputStream in = Configuration.class.getResourceAsStream(path);
			if (in == null) {
				throw new IOException(format("Configuration file {0} does not exist", path));
			}
			try {
				custom.load(in);
				add(custom);
			} finally {
				in.close();
			}
			logger.debug("Configuration loaded: " + path);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Merge the provided properties object.
	 *
	 * @param custom
	 *            the custom
	 */
	public static void add(Properties custom) {
		getInstance().putAll(custom);
	}

	/**
	 * Getter for the value of the property by its key.
	 *
	 * @param key
	 *            the key
	 * @return the string
	 */
	public static String get(String key) {
		return get(key, null);
	}

	/**
	 * Getter for the value of the property by its key.
	 *
	 * @param key
	 *            the key
	 * @param defaultValue
	 *            the default value
	 * @return the string
	 */
	public static String get(String key, String defaultValue) {
		String value;
		if (getInstance().parameters.containsKey(key)) {
			value = getInstance().parameters.get(key);
		} else if (System.getProperties().containsKey(key)) {
			value = System.getProperty(key);
		} else {
			value = System.getenv(key);
		}
		return (value != null) ? value : defaultValue;
	}

	/**
	 * Setter for the property's key and value.
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public static void set(String key, String value) {
		getInstance().parameters.put(key, value);
	}

	/**
	 * Remove property 
	 * @param key the key
	 */
	public static void remove(String key) {
		getInstance().parameters.remove(key);
	}

	/**
	 * Getter for all the keys.
	 *
	 * @return the keys
	 */
	public static String[] getKeys() {
		return getInstance().parameters.keySet().toArray(new String[] {});
	}

	/**
	 * Update the properties values from the System's properties and from the Environment if any.
	 */
	public static void update() {
		Set<String> keys = getInstance().parameters.keySet();
		for (String key : keys) {
			String asSystemProperty = System.getProperty(key);
			if (asSystemProperty != null) {
				getInstance().parameters.put(key, asSystemProperty);
			} else {
				String asEnvVar = System.getenv(key);
				if (asEnvVar != null) {
					getInstance().parameters.put(key, asEnvVar);
				}
			}
		}
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
	 * Checks if productive iframe is enabled.
	 *
	 * @return true, if productive iframe is enabled
	 */
	public static boolean isProductiveIFrameEnabled() {
		return Boolean.parseBoolean(Configuration.get("DIRIGIBLE_PRODUCTIVE_IFRAME_ENABLED", Boolean.FALSE.toString()));
	}

	/**
	 * Setter as a System's property.
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public static void setSystemProperty(String key, String value) {
		System.setProperty(key, value);
	}

	private static void create() {
		synchronized (Configuration.class) {
			INSTANCE = new Configuration();
			Configuration.update();
		}
	}

	private static Configuration getInstance() {
		if (INSTANCE == null) {
			create();
		}
		return INSTANCE;
	}

	private Configuration() {
		init();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void init() {
		try {
			Properties properties = new Properties();
			InputStream in = Configuration.class.getResourceAsStream("/dirigible.properties");
			if (in == null) {
				logger.warn("No default configuration dirigible.properties present in the classpath");
				return;
			}
			try {
				properties.load(in);
				this.parameters.putAll((Map) properties);
			} finally {
				in.close();
			}
			logger.debug("Configuration initialized with dirigible.properties");
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void putAll(Properties properties) {
		this.parameters.putAll((Map) properties);
		update();
	}
}
