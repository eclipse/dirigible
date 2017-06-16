package org.eclipse.dirigible.commons.api.module;

import com.google.inject.Injector;

public class StaticInjector {
	
	private static Injector injector;
	
	/**
	 * Gets the injector
	 *
	 * @return returns injector
	 */
	public static Injector getInjector() {
		return injector;
	}
	
	/**
	 * Sets the injector
	 *
	 * @param staticInjector
	 */
	public static void setInjector(Injector injector) {
		StaticInjector.injector = injector;
	}

}
