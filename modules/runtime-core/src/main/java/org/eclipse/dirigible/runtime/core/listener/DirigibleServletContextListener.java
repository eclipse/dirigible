package org.eclipse.dirigible.runtime.core.listener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ServiceLoader;

import javax.servlet.ServletContextEvent;

import org.apache.cxf.interceptor.security.SecureAnnotationsInterceptor;
import org.eclipse.dirigible.commons.api.module.DirigibleModulesInstallerModule;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.commons.api.service.IRestService;
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

	private Injector injector;

	@Override
	protected Injector getInjector() {
		logger.debug("Initializing Guice Injector with modules for dependency injection...");

		injector = Guice.createInjector(new DirigibleModulesInstallerModule());
		StaticInjector.setInjector(injector);
		
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
		for (IRestService next : ServiceLoader.load(IRestService.class)) {
			logger.debug("Registering REST service {} ...", next.getType());
			
			getServices().add(injector.getInstance(next.getType()));
			
			logger.debug("REST service {} registered.", next.getType());
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

}
