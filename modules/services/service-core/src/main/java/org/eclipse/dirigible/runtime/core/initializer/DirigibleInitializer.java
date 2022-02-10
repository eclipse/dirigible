/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.runtime.core.initializer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.interceptor.security.SecureAnnotationsInterceptor;
import org.apache.cxf.jaxrs.swagger.Swagger2Feature;
import org.eclipse.dirigible.commons.api.content.ClasspathContentLoader;
import org.eclipse.dirigible.commons.api.module.DirigibleModulesInstallerModule;
import org.eclipse.dirigible.commons.api.service.AbstractExceptionHandler;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.messaging.service.SchedulerManager;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.manager.SchedulerInitializer;
import org.eclipse.dirigible.runtime.core.services.GsonMessageBodyHandler;
import org.eclipse.dirigible.runtime.core.version.Version;
import org.eclipse.dirigible.runtime.core.version.VersionProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.models.auth.BasicAuthDefinition;
import io.swagger.models.auth.SecuritySchemeDefinition;

/**
 * This class handles the initialization of all modules and all REST API
 * resources.
 */
public class DirigibleInitializer {

	private static final Logger logger = LoggerFactory.getLogger(DirigibleInitializer.class);

	private static final HashSet<Object> services = new HashSet<Object>();

	public synchronized void initialize() {

		logger.info("---------- Initializing Eclipse Dirigible Platform... ----------");

		initializeModules();
		
		Configuration.loadModuleConfig("/dirigible-core.properties");
		Configuration.loadModuleConfig("/dirigible.properties");

		loadPredeliveredContent();

		registerRestServicesForCxf();

		startupScheduler();

		startupMessaging();
		
		startupTerminalServer();

		printAllConfigurations();

		logger.info("---------- Eclipse Dirigible Platform initialized. ----------");
	}
	
	private void initializeModules() {
		logger.trace("Initializing modules ...");

		DirigibleModulesInstallerModule.configure();

		logger.trace("Modules have been initialized.");
	}

	private void printAllConfigurations() {
		
		logger.info("---------- Environment ----------");
		logger.info("========== Configurations =======");
		String[] keys = Configuration.getKeys();
		for (String key : keys) {
			String value = Configuration.get(key);
			if(StringUtils.containsAnyIgnoreCase(key, "password", "secret", "_user", "username"))
			{
				value = "******";
			}
			logger.info("Configuration: {}={}", key, value);
		}
		
		logger.info("========== Properties =======");
		Properties props = System.getProperties();
		for (Map.Entry<Object, Object> entry : props.entrySet()) {
			if (entry.getKey() != null && entry.getKey().toString().startsWith("DIRIGIBLE")) {
				logger.info("Configuration: {}={}", entry.getKey().toString(), entry.getValue());
			}
		}
		
		logger.info("========== Variables =======");
		Map<String, String> env = System.getenv();
		for (Map.Entry<String, String> entry : env.entrySet()) {
			if (entry.getKey() != null && entry.getKey().startsWith("DIRIGIBLE")) {
				logger.info("Configuration: {}={}", entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * Load predelivered content.
	 */
	private void loadPredeliveredContent() {
		logger.trace("Loading the predelivered content...");
		try {
			ClasspathContentLoader.load();
		} catch (IOException e) {
			logger.error("Failed loading the predelivered content", e);
		}
		logger.trace("Done loading predelivered content.");
	}

	/**
	 * Register rest services for cxf.
	 */
	private void registerRestServicesForCxf() {
		logger.trace("Registering REST services...");

		getServices().add(new SecureAnnotationsInterceptor());
		getServices().add(new GsonMessageBodyHandler<Object>());

		addRestServices();
		addExceptionHandlers();
		addSwagger();

		logger.trace("Done registering REST services.");
	}

	/**
	 * Adds the rest services.
	 */
	private void addRestServices() {
		for (IRestService next : ServiceLoader.load(IRestService.class)) {
			StaticObjects.set(next.getType().getCanonicalName(), next);
			getServices().add(next);
			logger.info("REST service registered {}.", next.getType());
		}
	}

	/**
	 * Adds the exception handlers.
	 */
	private void addExceptionHandlers() {
		for (AbstractExceptionHandler<?> next : ServiceLoader.load(AbstractExceptionHandler.class)) {
			StaticObjects.set(next.getType().getCanonicalName(), next);
			getServices().add(next);
			logger.info("Exception Handler registered {}.", next.getType());
		}
	}

	/**
	 * Adds the swagger.
	 */
	private void addSwagger() {
		Swagger2Feature feature = new Swagger2Feature();

		// customize some of the properties
		feature.setBasePath("/services/v4");
//		feature.setPrettyPrint(true);
		feature.setDescription("Eclipse Dirigible API of the core REST services provided by the application development platform itself");
		try {
			Version version = new VersionProcessor().getVersion();
			feature.setVersion(version.getProductVersion());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			feature.setVersion("6.0.0");
		}
		feature.setTitle("Eclipse Dirigible - Core REST Services API");
		feature.setContact("dirigible-dev@eclipse.org");
		feature.setLicense("Eclipse Public License - v 2.0");
		feature.setLicenseUrl("https://www.eclipse.org/legal/epl-v20.html");
		Map<String, SecuritySchemeDefinition> securityDefinitions = new HashMap<String, SecuritySchemeDefinition>();
		BasicAuthDefinition auth = new BasicAuthDefinition();
		auth.setType("basic");
		securityDefinitions.put("basicAuth", auth);
		feature.setSecurityDefinitions(securityDefinitions);
//		feature.setPrettyPrint(true);

		getServices().add(feature);
	}

	/**
	 * Startup scheduler.
	 */
	private void startupScheduler() {
		logger.info("Starting Scheduler...");
		try {
			new SchedulerInitializer().initialize();
		} catch (SchedulerException | SQLException | IOException e) {
			logger.error("Failed starting Scheduler", e);
		}
		logger.info("Done starting Scheduler.");
	}

	/**
	 * Shutdown scheduler.
	 */
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
	 * Startup messaging.
	 */
	private void startupMessaging() {
		logger.info("Starting Message Broker...");
		try {
			new SchedulerManager().initialize();
		} catch (Exception e) {
			logger.error("Failed starting Messaging", e);
		}
		logger.info("Done starting Message Broker.");
	}
	
	/**
	 * Startup Terminal Server.
	 */
	private void startupTerminalServer() {
		logger.info("Starting Terminal Server...");
		try {
			Class.forName("org.eclipse.dirigible.runtime.ide.terminal.service.XTerminalWebsocketService");
		} catch (ClassNotFoundException e) {
			logger.warn("Terminal Server is not available");
		} catch (Throwable e) {
			logger.error("Failed starting Terminal Server", e);
		}
		logger.info("Done starting Terminal Server.");
	}

	/**
	 * Shutdown messaging.
	 */
	private void shutdownMessaging() {
		logger.trace("Shutting down Message Broker...");
		try {
			SchedulerManager.shutdown();
		} catch (Exception e) {
			logger.error("Failed shutting down Message Broker", e);
		}
		logger.trace("Done shutting down Message Broker.");
	}

	/**
	 * Get singleton services registered to this application.
	 *
	 * @return all singleton services.
	 */
	public static HashSet<Object> getServices() {
		return services;
	}

	public void destory() {
		logger.info("Shutting down Eclipse Dirigible Platform...");

		shutdownScheduler();

		shutdownMessaging();

		logger.info("Eclipse Dirigible Platform shut down.");
	}

}
