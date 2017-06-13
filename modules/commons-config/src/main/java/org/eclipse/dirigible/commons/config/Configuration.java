package org.eclipse.dirigible.commons.config;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
	
	public static final Charset UTF8 = Charset.forName("UTF-8");
	
	public static final String DIRIGIBLE_TEST_MODE_ENABLED = "DIRIGIBLE_TEST_MODE_ENABLED";
	
	private Map<String, String> parameters = Collections.synchronizedMap(new HashMap<String, String>());
	
	private static Configuration INSTANCE;
	
	private static void create() {
		synchronized(Configuration.class) {
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
	
	/**
	 * Initializes with the default properties from dirigible.properties
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void init() {
		try {
			Properties properties = new Properties();
			properties.load(Configuration.class.getResourceAsStream("/dirigible.properties"));
			this.parameters.putAll((Map) properties);
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
			InputStream in = Configuration.class.getResourceAsStream(path);
			if (in == null) {
				throw new IOException(format("Configuration file {0} does not exist", path));
			}
			custom.load(in);
			add(custom);
			logger.debug("Configuration loaded: " + path);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Merge the provided properties object
	 * @param custom
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void add(Properties custom) {
		try {
			if (custom.containsKey(DIRIGIBLE_TEST_MODE_ENABLED)) {
				throw new TestModeException("Setting the test mode programmatically as a parameter is forbidden.");
			}
			getInstance().parameters.putAll((Map) custom);
		} catch (TestModeException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * Getter for the value of the property by its key
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		return get(key, null);
	}

	/**
	 * Getter for the value of the property by its key
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String get(String key, String defaultValue) {
		String value = getInstance().parameters.get(key);
		return (value != null) ? value: defaultValue;
	}

	/**
	 * Setter for the property's key and value
	 * @param key
	 * @param value
	 */
	public static void set(String key, String value) {
		try {
			if (DIRIGIBLE_TEST_MODE_ENABLED.equals(key)) {
				throw new TestModeException("Setting the test mode programmatically as a parameter is forbidden.");
			}
			getInstance().parameters.put(key, value);
		} catch (TestModeException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * Getter for all the keys
	 * @param key
	 * @param value
	 * @return
	 */
	public static String[] getKeys(String key, String value) {
		return getInstance().parameters.keySet().toArray(new String[]{});
	}
	
	/**
	 * Update the properties values from the System's properties and from the Environment if any
	 */
	public static void update() {
		Set<String> keys = getInstance().parameters.keySet();
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = iterator.next();
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
	
	public static boolean isTestModeEnabled() {
		String testMode = Configuration.get(DIRIGIBLE_TEST_MODE_ENABLED);
		if (testMode != null || Boolean.parseBoolean(testMode)) {
			return true;
		}
		return false;
	}
	
	public static void enableTestMode() {
		getInstance().parameters.put(DIRIGIBLE_TEST_MODE_ENABLED, "true");
	}
	
	public static void disableTestMode() {
		getInstance().parameters.put(DIRIGIBLE_TEST_MODE_ENABLED, "false");
	}

}
