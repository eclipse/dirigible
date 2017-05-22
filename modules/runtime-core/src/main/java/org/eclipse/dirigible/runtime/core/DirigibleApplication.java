package org.eclipse.dirigible.runtime.core;

import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * Sets the singletons from Guice in the Application context.
 */
public class DirigibleApplication extends Application {

	@Override
	public Set<Object> getSingletons() {
		return DirigibleServletContextListener.getServices();
	}

}
