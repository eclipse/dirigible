package org.eclipse.dirigible.runtime.core.listener;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ServiceLoader;

import javax.servlet.ServletContextEvent;

import org.apache.cxf.interceptor.security.SecureAnnotationsInterceptor;
import org.apache.cxf.jaxrs.swagger.Swagger2Feature;
import org.eclipse.dirigible.commons.api.content.ClasspathContentLoader;
import org.eclipse.dirigible.commons.api.module.DirigibleModulesInstallerModule;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.core.messaging.service.MessagingManager;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.manager.SchedulerInitializer;
import org.eclipse.dirigible.runtime.core.services.GsonMessageBodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

import io.swagger.models.auth.BasicAuthDefinition;
import io.swagger.models.auth.SecuritySchemeDefinition;

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

		Configuration.load("/dirigible-core.properties");

		loadPredeliveredContent();

		registerRestServicesForCxf();

		startupScheduler();

		startupMessaging();

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
		getServices().add(new GsonMessageBodyHandler<Object>());

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
		feature.setBasePath("/services/v3");
		feature.setPrettyPrint(true);
		feature.setDescription("Eclipse Dirigible API of the core RESTful services provided by the application development platform itself");
		feature.setVersion("3.0");
		feature.setTitle("Eclipse Dirigible - RESTful Services API");
		feature.setContact("dirigible-dev@eclipse.org");
		feature.setLicense("Eclipse Public License - v 1.0");
		feature.setLicenseUrl("https://www.eclipse.org/legal/epl-v10.html");
		Map<String, SecuritySchemeDefinition> securityDefinitions = new HashMap<String, SecuritySchemeDefinition>();
		BasicAuthDefinition auth = new BasicAuthDefinition();
		auth.setType("basic");
		securityDefinitions.put("basicAuth", auth);
		feature.setSecurityDefinitions(securityDefinitions);
		feature.setPrettyPrint(true);

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

	private void startupMessaging() {
		logger.info("Starting Message Broker...");
		try {
			injector.getInstance(MessagingManager.class).initialize();
		} catch (Exception e) {
			logger.error("Failed starting Messaging", e);
		}
		logger.info("Done starting Message Broker.");
	}

	private void shutdownMessaging() {
		logger.trace("Shutting down Message Broker...");
		try {
			MessagingManager.shutdown();
		} catch (Exception e) {
			logger.error("Failed shutting down Message Broker", e);
		}
		logger.trace("Done shutting down Message Broker.");
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

		shutdownMessaging();

		super.contextDestroyed(servletContextEvent);

		logger.info("Eclipse Dirigible Platform shutted down.");
	}

}
