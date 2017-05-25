package org.eclipse.dirigible.commons.config;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration Facade class keeps all the configurations in the Dirigible instance
 * It has the default built in properties file - dirigible.properties
 * After the initialization, all the default properties are replaced with the ones coming as:
 *   1. System's properties
 *   2. Environment variables
 * This can be triggered programmatically with update() method
 * It supports also loading of custom properties files from the class loader with load() for the modules
 * and also merge with a provided properties object with add() methods
 * 
 */
public class Configuration {
	
	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
	
	private Properties properties = new Properties();
	
	public static Configuration INSTANCE;
	
	public static void create() {
		INSTANCE = new Configuration();
		Configuration.update();
	}
	
	private Configuration() {
		init();
	}
	
	/**
	 * Initializes with the default properties from dirigible.properties
	 */
	private void init() {
		try {
			this.properties.load(Configuration.class.getResourceAsStream("/dirigible.properties"));
			logger.debug("Configuration initialized with dirigible.properties");
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Loads a custom properties file from the class loader
	 * @param path
	 */
	public static void load(String path) {
		try {
			Properties custom = new Properties();
			custom.load(Configuration.class.getResourceAsStream(path));
			INSTANCE.properties.putAll(custom);
			logger.debug("Configuration loaded: " + path);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Merge the provided properties object
	 * @param custom
	 */
	public static void add(Properties custom) {
		INSTANCE.properties.putAll(custom);
	}
	
	/**
	 * Getter for the value of the property by its key
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		return INSTANCE.properties.getProperty(key);
	}
	
	/**
	 * Setter for the property's key and value
	 * @param key
	 * @param value
	 */
	public static void set(String key, String value) {
		INSTANCE.properties.setProperty(key, value);
	}
	
	/**
	 * Getter for all the keys
	 * @param key
	 * @param value
	 * @return
	 */
	public static String[] getKeys(String key, String value) {
		return INSTANCE.properties.stringPropertyNames().toArray(new String[]{});
	}
	
	/**
	 * Update the properties values from the System's properties and from the Environment if any
	 */
	public static void update() {
		Set<String> keys = INSTANCE.properties.stringPropertyNames();
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			String asSystemProperty = System.getProperty(key);
			if (asSystemProperty != null) {
				INSTANCE.properties.setProperty(key, asSystemProperty);
			} else {
				String asEnvVar = System.getenv(key);
				if (asEnvVar != null) {
					INSTANCE.properties.setProperty(key, asEnvVar);
				}
			}
		}
	}

}
