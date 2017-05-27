package org.eclipse.dirigible.runtime.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ServiceLoader;

import javax.servlet.ServletContextEvent;

import org.apache.cxf.interceptor.security.SecureAnnotationsInterceptor;
import org.eclipse.dirigible.commons.api.DirigibleModule;
import org.eclipse.dirigible.commons.api.DirigibleService;
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

	private static final String DEBUG_INITIALIZING_GUICE_INJECTOR_MESSAGE = "Initializing Guice Injector with modules for dependency injection";
	private static final String DEBUG_REGISTERING_API_SERVICES_MESSAGE = "Registering API services";
	private static final String DEBUG_API_SERVICES_REGISTED_MESSAGE = "API services registed: [{}]";

	private static final HashSet<Object> services = new HashSet<Object>();

	private static Injector staticInjector;

	private Injector injector;

	@Override
	protected Injector getInjector() {
		logger.debug(DEBUG_INITIALIZING_GUICE_INJECTOR_MESSAGE);

		Configuration.create();

		injector = Guice.createInjector(new DirigibleModule());

		setStaticInjector(injector);
		return injector;
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		super.contextInitialized(servletContextEvent);
		registerApiServicesForCxf();
	}

	private void registerApiServicesForCxf() {
		logger.debug(DEBUG_REGISTERING_API_SERVICES_MESSAGE);

		getServices().add(new SecureAnnotationsInterceptor());
		addDirigibleServices();

		logger.debug(DEBUG_API_SERVICES_REGISTED_MESSAGE, Arrays.asList(getServices().toArray()));
	}

	private void addDirigibleServices() {
		for (DirigibleService next : ServiceLoader.load(DirigibleService.class)) {
			getServices().add(injector.getInstance(next.getServiceType()));
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
