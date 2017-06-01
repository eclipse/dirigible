package org.eclipse.dirigible.runtime.core.app;

import java.util.Set;

import javax.ws.rs.core.Application;

import org.eclipse.dirigible.runtime.core.listener.DirigibleServletContextListener;

/**
 * Sets the singletons from Guice in the Application context.
 */
public class DirigibleApplication extends Application {

	@Override
	public Set<Object> getSingletons() {
		return DirigibleServletContextListener.getServices();
	}

}
