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
public class ConfigurationFacade {
	
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationFacade.class);
	
	private static ConfigurationFacade INSTANCE;
	private Properties properties = new Properties();
	
	private ConfigurationFacade() {
		//
	}
	
	/**
	 * Getter for the static instance of this class
	 * @return the static instance
	 */
	public static final ConfigurationFacade getInstance() {
		synchronized (ConfigurationFacade.class) {
			if (INSTANCE == null) {
				INSTANCE = new ConfigurationFacade();
				INSTANCE.init();
				INSTANCE.update();
			}
		}
		
		return INSTANCE;
	}

	/**
	 * Initializes with the default properties from dirigible.properties
	 */
	private void init() {
		try {
			this.properties.load(ConfigurationFacade.class.getResourceAsStream("/dirigible.properties"));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Loads a custom properties file from the class loader
	 * @param name
	 */
	public void load(String name) {
		try {
			Properties custom = new Properties();
			custom.load(ConfigurationFacade.class.getResourceAsStream(name));
			this.properties.putAll(custom);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Merge the provided properties object
	 * @param custom
	 */
	public void add(Properties custom) {
		this.properties.putAll(custom);
	}
	
	/**
	 * Getter for the value of the property by its key
	 * @param key
	 * @return
	 */
	public String get(String key) {
		return this.properties.getProperty(key);
	}
	
	/**
	 * Setter for the property's key and value
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {
		this.properties.setProperty(key, value);
	}
	
	/**
	 * Getter for all the keys
	 * @param key
	 * @param value
	 * @return
	 */
	public String[] getKeys(String key, String value) {
		return this.properties.stringPropertyNames().toArray(new String[]{});
	}
	
	/**
	 * Update the properties values from the System's properties and from the Environment if any
	 */
	public void update() {
		Set<String> keys = this.properties.stringPropertyNames();
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			String asSystemProperty = System.getProperty(key);
			if (asSystemProperty != null) {
				this.properties.setProperty(key, asSystemProperty);
			} else {
				String asEnvVar = System.getenv(key);
				if (asEnvVar != null) {
					this.properties.setProperty(key, asEnvVar);
				}
			}
		}
	}

}
