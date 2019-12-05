/**
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.odata2.synchronizer;

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
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.engine.odata2.api.IODataCoreService;
import org.eclipse.dirigible.engine.odata2.api.ODataException;
import org.eclipse.dirigible.engine.odata2.definition.ODataMappingDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataSchemaDefinition;
import org.eclipse.dirigible.engine.odata2.service.ODataCoreService;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class MappingsSynchronizer.
 */
@Singleton
public class ODataSynchronizer extends AbstractSynchronizer {

	private static final Logger logger = LoggerFactory.getLogger(ODataSynchronizer.class);

	private static final Map<String, ODataSchemaDefinition> SCHEMAS_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, ODataSchemaDefinition>());

	private static final Map<String, ODataMappingDefinition> MAPPINGS_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, ODataMappingDefinition>());

	private static final List<String> SCHEMAS_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	private static final List<String> MAPPINGS_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	@Inject
	private ODataCoreService odataCoreService;

	/**
	 * Force synchronization.
	 */
	public static final void forceSynchronization() {
		ODataSynchronizer mappingsSynchronizer = StaticInjector.getInjector().getInstance(ODataSynchronizer.class);
		mappingsSynchronizer.synchronize();
	}

	/**
	 * Register pre-delivered mapping point.
	 *
	 * @param schemaPath
	 *            the mapping point path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredSchema(String schemaPath) throws IOException {
		InputStream in = ODataSynchronizer.class.getResourceAsStream(schemaPath);
		try {
			String content = IOUtils.toString(in, StandardCharsets.UTF_8);
			ODataSchemaDefinition schemaDefinition = new ODataSchemaDefinition();
			schemaDefinition.setLocation(schemaPath);
			schemaDefinition.setContent(content.getBytes());
			SCHEMAS_PREDELIVERED.put(schemaPath, schemaDefinition);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	/**
	 * Register pre-delivered mapping.
	 *
	 * @param mappingPath
	 *            the mapping path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredMapping(String mappingPath) throws IOException {
		InputStream in = ODataSynchronizer.class.getResourceAsStream(mappingPath);
		try {
			String content = IOUtils.toString(in, StandardCharsets.UTF_8);
			ODataMappingDefinition mappingDefinition = new ODataMappingDefinition();
			mappingDefinition.setLocation(mappingPath);
			mappingDefinition.setContent(content.getBytes());
			MAPPINGS_PREDELIVERED.put(mappingPath, mappingDefinition);
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
		synchronized (ODataSynchronizer.class) {
			logger.trace("Synchronizing OData Schemas and Mappings...");
			try {
				clearCache();
				synchronizePredelivered();
				synchronizeRegistry();
				cleanup();
				clearCache();
			} catch (Exception e) {
				logger.error("Synchronizing process for OData Schemas and Mappings failed.", e);
			}
			logger.trace("Done synchronizing OData Schemas and Mappings.");
		}
	}

	private void clearCache() {
		SCHEMAS_SYNCHRONIZED.clear();
		MAPPINGS_SYNCHRONIZED.clear();
	}

	private void synchronizePredelivered() throws SynchronizationException {
		logger.trace("Synchronizing predelivered OData Schemas and Mappings...");
		// Schemas
		for (ODataSchemaDefinition schemaDefinition : SCHEMAS_PREDELIVERED.values()) {
			synchronizeSchema(schemaDefinition);
		}
		// Mappings
		for (ODataMappingDefinition mappingDefinition : MAPPINGS_PREDELIVERED.values()) {
			synchronizeMapping(mappingDefinition);
		}
		logger.trace("Done synchronizing predelivered OData Schemas and Mappings.");
	}

	private void synchronizeSchema(ODataSchemaDefinition schemaDefinition) throws SynchronizationException {
		try {
			if (!odataCoreService.existsSchema(schemaDefinition.getLocation())) {
				odataCoreService.createSchema(schemaDefinition.getLocation(), schemaDefinition.getContent());
				logger.info("Synchronized a new OData Schema from location: {}", schemaDefinition.getLocation());
			} else {
				ODataSchemaDefinition existing = odataCoreService.getSchema(schemaDefinition.getLocation());
				if (!schemaDefinition.equals(existing)) {
					odataCoreService.updateSchema(schemaDefinition.getLocation(), schemaDefinition.getContent());
					logger.info("Synchronized a modified OData Schema from location: {}", schemaDefinition.getLocation());
				}
			}
			SCHEMAS_SYNCHRONIZED.add(schemaDefinition.getLocation());
		} catch (ODataException e) {
			throw new SynchronizationException(e);
		}
	}

	private void synchronizeMapping(ODataMappingDefinition mappingDefinition) throws SynchronizationException {
		try {
			if (!odataCoreService.existsMapping(mappingDefinition.getLocation())) {
				odataCoreService.createMapping(mappingDefinition.getLocation(), mappingDefinition.getContent());
				logger.info("Synchronized a new OData Mapping from location: {}", mappingDefinition.getLocation());
			} else {
				ODataMappingDefinition existing = odataCoreService.getMapping(mappingDefinition.getLocation());
				if (!mappingDefinition.equals(existing)) {
					odataCoreService.updateMapping(mappingDefinition.getLocation(), mappingDefinition.getContent());
					logger.info("Synchronized a modified OData Mapping from location: {}", mappingDefinition.getLocation());
				}
			}
			MAPPINGS_SYNCHRONIZED.add(mappingDefinition.getLocation());
		} catch (ODataException e) {
			throw new SynchronizationException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeRegistry()
	 */
	@Override
	protected void synchronizeRegistry() throws SynchronizationException {
		logger.trace("Synchronizing OData Schemas and Mappings from Registry...");

		super.synchronizeRegistry();

		logger.trace("Done synchronizing OData Schemas and Mappings from Registry.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeResource(org.eclipse.dirigible.
	 * repository.api.IResource)
	 */
	@Override
	protected void synchronizeResource(IResource resource) throws SynchronizationException {
		String resourceName = resource.getName();
		if (resourceName.endsWith(IODataCoreService.FILE_EXTENSION_ODATA_SCHEMA)) {
			ODataSchemaDefinition schemaDefinition = new ODataSchemaDefinition();
			schemaDefinition.setLocation(getRegistryPath(resource));
			schemaDefinition.setContent(resource.getContent());
			synchronizeSchema(schemaDefinition);
		}

		if (resourceName.endsWith(IODataCoreService.FILE_EXTENSION_ODATA_MAPPING)) {
			ODataMappingDefinition mappingDefinition = new ODataMappingDefinition();
			mappingDefinition.setLocation(getRegistryPath(resource));
			mappingDefinition.setContent(resource.getContent());
			synchronizeMapping(mappingDefinition);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#cleanup()
	 */
	@Override
	protected void cleanup() throws SynchronizationException {
		logger.trace("Cleaning up OData Schemas and Mappings...");

		IRepository repository = getRepository();
		try {
			List<ODataSchemaDefinition> schemaDefinitions = odataCoreService.getSchemas();
			for (ODataSchemaDefinition schemaDefinition : schemaDefinitions) {
				if (!SCHEMAS_SYNCHRONIZED.contains(schemaDefinition.getLocation())) {
					// Only if it really does not exists. The schema can come from different sources e.g. generated from another artifact
					if (!repository.getResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + schemaDefinition.getLocation()).exists()) {
						odataCoreService.removeSchema(schemaDefinition.getLocation());
						logger.warn("Cleaned up OData Schema from location: {}", schemaDefinition.getLocation());
					}
				}
			}

			List<ODataMappingDefinition> mappingDefinitions = odataCoreService.getMappings();
			for (ODataMappingDefinition mappingDefinition : mappingDefinitions) {
				if (!MAPPINGS_SYNCHRONIZED.contains(mappingDefinition.getLocation())) {
					// Only if it really does not exists. The mapping can come from different sources e.g. generated from another artifact
					String location = mappingDefinition.getLocation();
					location = location.indexOf("#") > 1 ? location.substring(0, location.indexOf("#")) : location;
					if (!repository.getResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + location).exists()) {
						odataCoreService.removeMapping(mappingDefinition.getLocation());
						logger.warn("Cleaned up OData Mapping from location: {}", mappingDefinition.getLocation());
					}
				}
			}
		} catch (ODataException e) {
			throw new SynchronizationException(e);
		}

		logger.trace("Done cleaning up OData Schemas and Mappings.");
	}
}
