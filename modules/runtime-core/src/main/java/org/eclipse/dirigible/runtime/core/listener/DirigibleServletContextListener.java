package org.eclipse.dirigible.runtime.core.listener;

import java.io.IOException;
import java.util.HashSet;
import java.util.ServiceLoader;

import javax.servlet.ServletContextEvent;

import org.apache.cxf.interceptor.security.SecureAnnotationsInterceptor;
import org.eclipse.dirigible.commons.api.content.ClasspathContentLoader;
import org.eclipse.dirigible.commons.api.logging.LoggingHelper;
import org.eclipse.dirigible.commons.api.module.DirigibleModulesInstallerModule;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.core.scheduler.SchedulerException;
import org.eclipse.dirigible.core.scheduler.SchedulerInitializer;
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
	
	private final LoggingHelper loggingHelper = new LoggingHelper(logger);

	@Override
	protected Injector getInjector() {
		loggingHelper.beginGroup("Initializing Guice Injector with modules for dependency injection...");

		injector = Guice.createInjector(new DirigibleModulesInstallerModule(loggingHelper));
		StaticInjector.setInjector(injector);
		
		loggingHelper.endGroup("Guice Injector with modules for dependency injection initialized.");
		
		return injector;
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		loggingHelper.beginSection("Initializing Eclipse Dirigible Platform...");
		super.contextInitialized(servletContextEvent);
		
		loadPredeliveredContent();
		
		registerRestServicesForCxf();
		
		startupScheduler();
		
		loggingHelper.endSection("Eclipse Dirigible Platform initialized.");
	}

	private void loadPredeliveredContent() {
		loggingHelper.beginGroup("Loading the predelivered content...");
		try {
			ClasspathContentLoader.load(loggingHelper);
		} catch (IOException e) {
			logger.error("Failed loading the predelivered content", e);
		}
		loggingHelper.endGroup("Done loading predelivered content.");
	}

	private void registerRestServicesForCxf() {
		loggingHelper.beginGroup("Registering REST services...");

		getServices().add(new SecureAnnotationsInterceptor());
		addRestServices();
		addExceptionHandlers();

		loggingHelper.endGroup("Done registering REST services.");
	}

	private void addRestServices() {
		for (IRestService next : ServiceLoader.load(IRestService.class)) {
			getServices().add(injector.getInstance(next.getType()));
			loggingHelper.info("REST service registered {}.", next.getType());
		}
	}

	private void addExceptionHandlers() {
		for (AbstractExceptionHandler<?> next : ServiceLoader.load(AbstractExceptionHandler.class)) {
			getServices().add(injector.getInstance(next.getType()));
			loggingHelper.info("Exception Handler registered {}.", next.getType());
		}
	}
	
	private void startupScheduler() {
		loggingHelper.beginGroup("Starting Scheduler...");
		try {
			SchedulerInitializer.initialize(loggingHelper);
		} catch (SchedulerException e) {
			logger.error("Failed starting Scheduler", e);
		}
		loggingHelper.endGroup("Done starting Scheduler.");
	}
	
	private void shutdownScheduler() {
		loggingHelper.beginGroup("Shutting down Scheduler...");
		try {
			SchedulerInitializer.shutdown(loggingHelper);
		} catch (SchedulerException e) {
			logger.error("Failed initializing Scheduler", e);
		}
		loggingHelper.endGroup("Done initializing Scheduler.");
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
		loggingHelper.beginSection("Shutting down Eclipse Dirigible Platform...");
		
		shutdownScheduler();
		
		super.contextDestroyed(servletContextEvent);
		
		loggingHelper.endSection("Eclipse Dirigible Platform shutted down.");
	}

}
