package org.eclipse.dirigible.init;

import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class InitializationListener implements ServletContextListener {

	public static final Logger logger = Logger.getLogger(InitializationListener.class.getCanonicalName());

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		logger.info("Eclipse Dirigible AIR started. Initialization of databases, repository and configurations triggerred.");
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		logger.info("ServletContextListener destroyed");
	}

}
