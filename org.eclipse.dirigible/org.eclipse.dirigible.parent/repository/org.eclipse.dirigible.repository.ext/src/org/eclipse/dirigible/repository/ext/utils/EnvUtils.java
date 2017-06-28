package org.eclipse.dirigible.repository.ext.utils;

public class EnvUtils {

	public static String getEnv(String name) {
		return getEnv(name, null);
	}

	public static String getEnv(String name, String defaultValue) {
		String var = System.getProperty(name);
		if (var == null) {
			var = System.getenv(name);
			if (var == null) {
				var = defaultValue;
			}
		}
		return var;
	}

	public static void setEnv(String name, String value) {
		System.setProperty(name, value);
	}

}
