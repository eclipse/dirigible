package org.eclipse.dirigible.runtime.core.listener;

import java.io.IOException;
import java.util.HashSet;
import java.util.ServiceLoader;

import javax.servlet.ServletContextEvent;

import org.apache.cxf.interceptor.security.SecureAnnotationsInterceptor;
import org.apache.cxf.jaxrs.swagger.Swagger2Feature;
import org.eclipse.dirigible.commons.api.content.ClasspathContentLoader;
import org.eclipse.dirigible.commons.api.module.DirigibleModulesInstallerModule;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.manager.SchedulerInitializer;
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
		logger.trace("Initializing Guice Injector with modules for dependency injection...");

		injector = Guice.createInjector(new DirigibleModulesInstallerModule());
		StaticInjector.setInjector(injector);
		
		logger.trace("Guice Injector with modules for dependency injection initialized.");
		
		return injector;
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		logger.info("---------- Initializing Eclipse Dirigible Platform... ----------");
		super.contextInitialized(servletContextEvent);
		
		loadPredeliveredContent();
		
		registerRestServicesForCxf();
		
		startupScheduler();
		
		logger.info("---------- Eclipse Dirigible Platform initialized. ----------");
	}

	

	private void loadPredeliveredContent() {
		logger.trace("Loading the predelivered content...");
		try {
			ClasspathContentLoader.load();
		} catch (IOException e) {
			logger.error("Failed loading the predelivered content", e);
		}
		logger.trace("Done loading predelivered content.");
	}

	private void registerRestServicesForCxf() {
		logger.trace("Registering REST services...");

		getServices().add(new SecureAnnotationsInterceptor());
		addRestServices();
		addExceptionHandlers();
		addSwagger();

		logger.trace("Done registering REST services.");
	}

	private void addRestServices() {
		for (IRestService next : ServiceLoader.load(IRestService.class)) {
			getServices().add(injector.getInstance(next.getType()));
			logger.info("REST service registered {}.", next.getType());
		}
	}

	private void addExceptionHandlers() {
		for (AbstractExceptionHandler<?> next : ServiceLoader.load(AbstractExceptionHandler.class)) {
			getServices().add(injector.getInstance(next.getType()));
			logger.info("Exception Handler registered {}.", next.getType());
		}
	}
	
	private void addSwagger() {
		Swagger2Feature feature = new Swagger2Feature();
		 
	    // customize some of the properties
//	    feature.setBasePath("/api");
		feature.setPrettyPrint(true);
		feature.setDescription("Eclipse Dirigible API of the core RESTful services provided by the application development platform itself");
		feature.setVersion("3.0");
		feature.setTitle("Eclipse Dirigible - RESTful Services API");
		feature.setContact("dirigible-dev@eclipse.org");
		feature.setLicense("Eclipse Public License - v 1.0");
		feature.setLicenseUrl("https://www.eclipse.org/legal/epl-v10.html");
	         
	    getServices().add(feature);		
	}
	
	private void startupScheduler() {
		logger.info("Starting Scheduler...");
		try {
			injector.getInstance(SchedulerInitializer.class).initialize();
		} catch (SchedulerException e) {
			logger.error("Failed starting Scheduler", e);
		}
		logger.info("Done starting Scheduler.");
	}
	
	private void shutdownScheduler() {
		logger.trace("Shutting down Scheduler...");
		try {
			SchedulerInitializer.shutdown();
		} catch (SchedulerException e) {
			logger.error("Failed shutting down Scheduler", e);
		}
		logger.trace("Done shutting down Scheduler.");
	}
	
	/**
	 * Get singleton services registred to this application.
	 *
	 * @return all singleton services.
	 */
	public static HashSet<Object> getServices() {
		return services;
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		logger.info("Shutting down Eclipse Dirigible Platform...");
		
		shutdownScheduler();
		
		super.contextDestroyed(servletContextEvent);
		
		logger.info("Eclipse Dirigible Platform shutted down.");
	}

}
