package org.eclipse.dirigible.repository.ext.utils;

public class EnvUtils {

	public static String getEnv(String name) {
		String var = System.getProperty(name);
		if (var == null) {
			var = System.getenv(name);
		}
		return var;
	}

	public static void setEnv(String name, String value) {
		System.setProperty(name, value);
	}

}
