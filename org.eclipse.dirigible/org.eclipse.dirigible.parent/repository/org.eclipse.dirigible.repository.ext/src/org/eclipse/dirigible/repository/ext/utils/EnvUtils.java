package org.eclipse.dirigible.repository.ext.utils;

public class EnvUtils {

	public static String getEnv(String name) {
		return System.getProperty(name);
	}

	public static void setEnv(String name, String value) {
		System.setProperty(name, value);
	}

}
