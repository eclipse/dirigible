package org.eclipse.dirigible.runtime.core.listener;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ServiceLoader;

import javax.servlet.ServletContextEvent;

import org.apache.cxf.interceptor.security.SecureAnnotationsInterceptor;
import org.eclipse.dirigible.commons.api.content.ClasspathContentLoader;
import org.eclipse.dirigible.commons.api.module.DirigibleModulesInstallerModule;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler;
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
		logger.info("Initializing Guice Injector with modules for dependency injection...");

		injector = Guice.createInjector(new DirigibleModulesInstallerModule());
		StaticInjector.setInjector(injector);
		
		logger.info("Guice Injector with modules for dependency injection initialized.");
		
		return injector;
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		super.contextInitialized(servletContextEvent);
		logger.info("Initializing servlet context...");
		
		loadPredeliveredContent();
		
		registerRestServicesForCxf();
		
		logger.info("Done initializing servlet context");
	}

	private void loadPredeliveredContent() {
		logger.info("Loading the predelivered content...");
		try {
			ClasspathContentLoader.load();
		} catch (IOException e) {
			logger.error("Failed loading the predelivered content", e);
		}
		logger.info("Done loading predelivered content.");
	}

	private void registerRestServicesForCxf() {
		logger.info("Registering REST services...");

		getServices().add(new SecureAnnotationsInterceptor());
		addRestServices();
		addExceptionHandlers();

		logger.info("Done registering REST services: [{}].", Arrays.asList(getServices().toArray()));
	}

	private void addRestServices() {
		for (IRestService next : ServiceLoader.load(IRestService.class)) {
			logger.info("Registering REST service {} ...", next.getType());
			
			getServices().add(injector.getInstance(next.getType()));
			
			logger.info("Done registering REST service {}.", next.getType());
		}
	}

	private void addExceptionHandlers() {
		for (AbstractExceptionHandler<?> next : ServiceLoader.load(AbstractExceptionHandler.class)) {
			logger.info("Registering Exception Handler {} ...", next.getType());
			
			getServices().add(injector.getInstance(next.getType()));
			
			logger.info("Done registering Exception Handler {}.", next.getType());
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
