/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.core.extensions.synchronizer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService;
import org.eclipse.dirigible.core.extensions.definition.ExtensionDefinition;
import org.eclipse.dirigible.core.extensions.definition.ExtensionPointDefinition;
import org.eclipse.dirigible.core.extensions.service.ExtensionsCoreService;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ExtensionsSynchronizer.
 */
@Singleton
public class ExtensionsSynchronizer extends AbstractSynchronizer {

	private static final Logger logger = LoggerFactory.getLogger(ExtensionsSynchronizer.class);

	private static final Map<String, ExtensionPointDefinition> EXTENSION_POINTS_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, ExtensionPointDefinition>());

	private static final Map<String, ExtensionDefinition> EXTENSIONS_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, ExtensionDefinition>());

	private static final List<String> EXTENSION_POINTS_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	private static final List<String> EXTENSIONS_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	@Inject
	private ExtensionsCoreService extensionsCoreService;

	/**
	 * Force synchronization.
	 */
	public static final void forceSynchronization() {
		ExtensionsSynchronizer extensionsSynchronizer = StaticInjector.getInjector().getInstance(ExtensionsSynchronizer.class);
		extensionsSynchronizer.synchronize();
	}

	/**
	 * Register pre-delivered extension point.
	 *
	 * @param extensionPointPath
	 *            the extension point path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredExtensionPoint(String extensionPointPath) throws IOException {
		InputStream in = ExtensionsSynchronizer.class.getResourceAsStream(extensionPointPath);
		try {
			String json = IOUtils.toString(in, StandardCharsets.UTF_8);
			ExtensionPointDefinition extensionPointDefinition = extensionsCoreService.parseExtensionPoint(json);
			extensionPointDefinition.setLocation(extensionPointPath);
			EXTENSION_POINTS_PREDELIVERED.put(extensionPointPath, extensionPointDefinition);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	/**
	 * Register pre-delivered extension.
	 *
	 * @param extensionPath
	 *            the extension path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredExtension(String extensionPath) throws IOException {
		InputStream in = ExtensionsSynchronizer.class.getResourceAsStream(extensionPath);
		try {
			String json = IOUtils.toString(in, StandardCharsets.UTF_8);
			ExtensionDefinition extensionDefinition = extensionsCoreService.parseExtension(json);
			extensionDefinition.setLocation(extensionPath);
			EXTENSIONS_PREDELIVERED.put(extensionPath, extensionDefinition);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
		synchronized (ExtensionsSynchronizer.class) {
			logger.trace("Synchronizing Extension Points and Extensions...");
			try {
				clearCache();
				synchronizePredelivered();
				synchronizeRegistry();
				cleanup();
				clearCache();
			} catch (Exception e) {
				logger.error("Synchronizing process for Extension Points and Extensions failed.", e);
			}
			logger.trace("Done synchronizing Extension Points and Extensions.");
		}
	}

	private void clearCache() {
		EXTENSION_POINTS_SYNCHRONIZED.clear();
		EXTENSIONS_SYNCHRONIZED.clear();
	}

	private void synchronizePredelivered() throws SynchronizationException {
		logger.trace("Synchronizing predelivered Extension Points and Extensions...");
		// Extension Points
		for (ExtensionPointDefinition extensionPointDefinition : EXTENSION_POINTS_PREDELIVERED.values()) {
			synchronizeExtensionPoint(extensionPointDefinition);
		}
		// Extensions
		for (ExtensionDefinition extensionDefinition : EXTENSIONS_PREDELIVERED.values()) {
			synchronizeExtension(extensionDefinition);
		}
		logger.trace("Done synchronizing predelivered Extension Points and Extensions.");
	}

	private void synchronizeExtensionPoint(ExtensionPointDefinition extensionPointDefinition) throws SynchronizationException {
		try {
			if (!extensionsCoreService.existsExtensionPoint(extensionPointDefinition.getLocation())) {
				extensionsCoreService.createExtensionPoint(extensionPointDefinition.getLocation(), extensionPointDefinition.getName(),
						extensionPointDefinition.getDescription());
				logger.info("Synchronized a new Extension Point [{}] from location: {}", extensionPointDefinition.getName(),
						extensionPointDefinition.getLocation());
			} else {
				ExtensionPointDefinition existing = extensionsCoreService.getExtensionPoint(extensionPointDefinition.getLocation());
				if (!extensionPointDefinition.equals(existing)) {
					extensionsCoreService.updateExtensionPoint(extensionPointDefinition.getLocation(), extensionPointDefinition.getName(),
							extensionPointDefinition.getDescription());
					logger.info("Synchronized a modified Extension Point [{}] from location: {}", extensionPointDefinition.getName(),
							extensionPointDefinition.getLocation());
				}
			}
			EXTENSION_POINTS_SYNCHRONIZED.add(extensionPointDefinition.getLocation());
		} catch (ExtensionsException e) {
			throw new SynchronizationException(e);
		}
	}

	private void synchronizeExtension(ExtensionDefinition extensionDefinition) throws SynchronizationException {
		try {
			if (!extensionsCoreService.existsExtension(extensionDefinition.getLocation())) {
				extensionsCoreService.createExtension(extensionDefinition.getLocation(), extensionDefinition.getModule(),
						extensionDefinition.getExtensionPoint(), extensionDefinition.getDescription());
				logger.info("Synchronized a new Extension [{}] for Extension Point [{}] from location: {}", extensionDefinition.getModule(),
						extensionDefinition.getExtensionPoint(), extensionDefinition.getLocation());
			} else {
				ExtensionDefinition existing = extensionsCoreService.getExtension(extensionDefinition.getLocation());
				if (!extensionDefinition.equals(existing)) {
					extensionsCoreService.updateExtension(extensionDefinition.getLocation(), extensionDefinition.getModule(),
							extensionDefinition.getExtensionPoint(), extensionDefinition.getDescription());
					logger.info("Synchronized a modified Extension [{}] for Extension Point [{}] from location: {}", extensionDefinition.getModule(),
							extensionDefinition.getExtensionPoint(), extensionDefinition.getLocation());
				}
			}
			EXTENSIONS_SYNCHRONIZED.add(extensionDefinition.getLocation());
		} catch (ExtensionsException e) {
			throw new SynchronizationException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeRegistry()
	 */
	@Override
	protected void synchronizeRegistry() throws SynchronizationException {
		logger.trace("Synchronizing Extension Points and Extensions from Registry...");

		super.synchronizeRegistry();

		logger.trace("Done synchronizing Extension Points and Extensions from Registry.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeResource(org.eclipse.dirigible.
	 * repository.api.IResource)
	 */
	@Override
	protected void synchronizeResource(IResource resource) throws SynchronizationException {
		String resourceName = resource.getName();
		if (resourceName.endsWith(IExtensionsCoreService.FILE_EXTENSION_EXTENSIONPOINT)) {
			ExtensionPointDefinition extensionPointDefinition = extensionsCoreService.parseExtensionPoint(resource.getContent());

			extensionPointDefinition.setLocation(getRegistryPath(resource));
			synchronizeExtensionPoint(extensionPointDefinition);
		}

		if (resourceName.endsWith(IExtensionsCoreService.FILE_EXTENSION_EXTENSION)) {
			ExtensionDefinition extensionDefinition = extensionsCoreService.parseExtension(resource.getContent());
			extensionDefinition.setLocation(getRegistryPath(resource));
			synchronizeExtension(extensionDefinition);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#cleanup()
	 */
	@Override
	protected void cleanup() throws SynchronizationException {
		logger.trace("Cleaning up Extension Points and Extensions...");

		try {
			List<ExtensionPointDefinition> extensionPointDefinitions = extensionsCoreService.getExtensionPoints();
			for (ExtensionPointDefinition extensionPointDefinition : extensionPointDefinitions) {
				if (!EXTENSION_POINTS_SYNCHRONIZED.contains(extensionPointDefinition.getLocation())) {
					extensionsCoreService.removeExtensionPoint(extensionPointDefinition.getLocation());
					logger.warn("Cleaned up Extension Point [{}] from location: {}", extensionPointDefinition.getName(),
							extensionPointDefinition.getLocation());
				}
			}

			List<ExtensionDefinition> extensionDefinitions = extensionsCoreService.getExtensions();
			for (ExtensionDefinition extensionDefinition : extensionDefinitions) {
				if (!EXTENSIONS_SYNCHRONIZED.contains(extensionDefinition.getLocation())) {
					extensionsCoreService.removeExtension(extensionDefinition.getLocation());
					logger.warn("Cleaned up Extension for Module [{}] from location: {}", extensionDefinition.getModule(),
							extensionDefinition.getLocation());
				}
			}
		} catch (ExtensionsException e) {
			throw new SynchronizationException(e);
		}

		logger.trace("Done cleaning up Extension Points and Extensions.");
	}
}
