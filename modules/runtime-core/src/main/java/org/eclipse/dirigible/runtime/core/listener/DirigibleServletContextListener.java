package org.eclipse.dirigible.runtime.core.listener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ServiceLoader;

import javax.servlet.ServletContextEvent;

import org.apache.cxf.interceptor.security.SecureAnnotationsInterceptor;
import org.eclipse.dirigible.commons.api.module.DirigibleModulesInstallerModule;
import org.eclipse.dirigible.commons.api.service.RestService;
import org.eclipse.dirigible.commons.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * This class handles the initialization of all Guice modules and all REST API
 * resources.
 */
public class DirigibleServletContextListener extends GuiceServletContextListener {

	private static final Logger logger = LoggerFactory.getLogger(DirigibleServletContextListener.class);

	private static final HashSet<Object> services = new HashSet<Object>();

	private static Injector staticInjector;

	private Injector injector;

	@Override
	protected Injector getInjector() {
		logger.debug("Initializing Guice Injector with modules for dependency injection...");

		Configuration.create();
		injector = Guice.createInjector(new DirigibleModulesInstallerModule());
		setStaticInjector(injector);
		
		logger.debug("Guice Injector with modules for dependency injection initialized.");
		
		return injector;
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		super.contextInitialized(servletContextEvent);
		logger.debug("Initializing servlet context...");
		
		registerRestServicesForCxf();
		
		logger.debug("Servlet context initialized.");
	}

	private void registerRestServicesForCxf() {
		logger.debug("Registering REST services...");

		getServices().add(new SecureAnnotationsInterceptor());
		addRestServices();

		logger.debug("REST services registed: [{}]", Arrays.asList(getServices().toArray()));
	}

	private void addRestServices() {
		for (RestService next : ServiceLoader.load(RestService.class)) {
			logger.debug("Registering REST service {} ...", next.getServiceType());
			
			getServices().add(injector.getInstance(next.getServiceType()));
			
			logger.debug("REST service {} registered.", next.getServiceType());
		}
	}

	/**
	 * Get singleton services registred to this application.
	 *
	 * @return all singleton services.
	 */
	public static HashSet<Object> getServices() {
		return services;
	}

	/**
	 * Gets the injector
	 *
	 * @return returns injector
	 */
	public static Injector getStaticInjector() {
		return staticInjector;
	}

	/**
	 * Sets the injector
	 *
	 * @param staticInjector
	 */
	public static void setStaticInjector(Injector staticInjector) {
		DirigibleServletContextListener.staticInjector = staticInjector;
	}

}
