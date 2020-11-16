/*
 * Copyright (c) 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.synchronizer;

import static java.text.MessageFormat.format;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.engine.odata2.api.IODataCoreService;
import org.eclipse.dirigible.engine.odata2.api.ODataException;
import org.eclipse.dirigible.engine.odata2.definition.ODataDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataMappingDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataSchemaDefinition;
import org.eclipse.dirigible.engine.odata2.service.ODataCoreService;
import org.eclipse.dirigible.engine.odata2.transformers.OData2ODataMTransformer;
import org.eclipse.dirigible.engine.odata2.transformers.OData2ODataXTransformer;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The OData Synchronizer.
 */
@Singleton
public class ODataSynchronizer extends AbstractSynchronizer {

	private static final Logger logger = LoggerFactory.getLogger(ODataSynchronizer.class);

	private static final Map<String, ODataSchemaDefinition> SCHEMAS_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, ODataSchemaDefinition>());

	private static final Map<String, ODataMappingDefinition> MAPPINGS_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, ODataMappingDefinition>());
	
	private static final Map<String, ODataDefinition> ODATA_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, ODataDefinition>());

	private static final List<String> SCHEMAS_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	private static final List<String> MAPPINGS_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());
	
	private static final List<String> ODATA_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());
	
	private static final Map<String, ODataDefinition> ODATA_MODELS = new LinkedHashMap<String, ODataDefinition>();

	@Inject
	private ODataCoreService odataCoreService;
	
	@Inject
	private DataSource dataSource;

	@Inject
	private OData2ODataMTransformer odata2ODataMTransformer;

	@Inject
	private OData2ODataXTransformer odata2ODataXTransformer;

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
	
	/**
	 * Register predelivered odata files.
	 *
	 * @param contentPath
	 *            the data path
	 * @throws Exception 
	 */
	public void registerPredeliveredOData(String contentPath) throws Exception {
		String data = loadResourceContent(contentPath);
		ODataDefinition model = odataCoreService.parseOData(contentPath, data);
		ODATA_PREDELIVERED.put(contentPath, model);
	}

	private String loadResourceContent(String modelPath) throws IOException {
		InputStream in = ODataSynchronizer.class.getResourceAsStream(modelPath);
		try {
			String content = IOUtils.toString(in, StandardCharsets.UTF_8);
			return content;
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
				updateOData();
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
		ODATA_MODELS.clear();
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
		// OData
		for (ODataDefinition odata : ODATA_PREDELIVERED.values()) {
			try {
				synchronizeOData(odata);
			} catch (Exception e) {
				logger.error(format("Update odata [{0}] skipped due to an error: {1}", odata, e.getMessage()), e);
			}
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
	
	/**
	 * Synchronize odata.
	 *
	 * @param odataModel
	 *            the odata model
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void synchronizeOData(ODataDefinition odataModel) throws SynchronizationException {
		try {
			if (!odataCoreService.existsOData(odataModel.getLocation())) {
				odataCoreService.createOData(odataModel.getLocation(), odataModel.getNamespace(), odataModel.getHash());
				ODATA_MODELS.put(odataModel.getNamespace(), odataModel);
				logger.info("Synchronized a new OData with namespace [{}] from location: {}", odataModel.getNamespace(), odataModel.getLocation());
			} else {
				ODataDefinition existing = odataCoreService.getOData(odataModel.getLocation());
				if (!odataModel.equals(existing)) {
					odataCoreService.updateOData(odataModel.getLocation(), odataModel.getNamespace(), odataModel.getHash());
					ODATA_MODELS.put(odataModel.getNamespace(), odataModel);
					logger.info("Synchronized a modified OData file [{}] from location: {}", odataModel.getNamespace(), odataModel.getLocation());
				}
			}
			ODATA_SYNCHRONIZED.add(odataModel.getLocation());
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
		
		if (resourceName.endsWith(IODataCoreService.FILE_EXTENSION_ODATA)) {
			String registryPath = getRegistryPath(resource);
			byte[] content = resource.getContent();
			String contentAsString;
			try {
				contentAsString = IOUtils.toString(new InputStreamReader(new ByteArrayInputStream(content), StandardCharsets.UTF_8));
			} catch (IOException e) {
				throw new SynchronizationException(e);
			}
			ODataDefinition odataModel;
			try {
				odataModel = odataCoreService.parseOData(registryPath, contentAsString);
			} catch (Exception e) {
				throw new SynchronizationException(e);
			}
			odataModel.setLocation(registryPath);
			synchronizeOData(odataModel);
			return;
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
			
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				
				List<ODataDefinition> odataModels = odataCoreService.getODatas();
				for (ODataDefinition odataModel : odataModels) {
					if (!ODATA_SYNCHRONIZED.contains(odataModel.getLocation())) {
						odataCoreService.removeOData(odataModel.getLocation());
						logger.warn("Cleaned up OData file with namespace [{}] from location: {}", odataModel.getNamespace(), odataModel.getLocation());
					}
				}
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (ODataException | SQLException e) {
			throw new SynchronizationException(e);
		}

		logger.trace("Done cleaning up OData Schemas and Mappings.");
	}
	
	
	private void updateOData() {
		// Update OData
		
		if (ODATA_MODELS.isEmpty()) {
			logger.trace("No OData to update.");
			return;
		}

		List<String> errors = new ArrayList<String>();
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				// topology sort of dependencies
				List<String> sorted = new ArrayList<String>();
//				List<String> external = new ArrayList<String>();
				

				if (sorted.isEmpty()) {
					// something wrong happened with the sorting - probably cyclic dependencies
					// we go for the back-up list and try to apply what would succeed
					sorted.addAll(ODATA_MODELS.keySet());
				}
				
				// drop odata in a reverse order
				for (int i = sorted.size() - 1; i >= 0; i--) {
					String dsName = sorted.get(i);
					ODataDefinition model = ODATA_MODELS.get(dsName);
					try {
						// CLEAN UP LOGIC
						odataCoreService.removeSchema(model.getLocation());
						odataCoreService.removeContainer(model.getLocation());
						odataCoreService.removeMappings(model.getLocation());
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						errors.add(e.getMessage());
					}
				}
				
				// process tables in the proper order
				for (String dsName : sorted) {
					ODataDefinition model = ODATA_MODELS.get(dsName);
					try {
						// METADATA AND MAPPINGS GENERATION LOGIC
						String[] odataxc = generateODataX(model);
						String odatax = odataxc[0];
						String odatac = odataxc[1];
						odataCoreService.createSchema(model.getLocation(), odatax.getBytes());
						odataCoreService.createContainer(model.getLocation(), odatac.getBytes());
						
						String[] odatams = generateODataMs(model);
						int i=1;
						for (String odatam : odatams) {
							odataCoreService.createMapping(model.getLocation() + "#" + i++, odatam.getBytes());
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						errors.add(e.getMessage());
					}
				}

			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			logger.error(concatenateListOfStrings(errors, "\n---\n"), e);
		}
		
	}
	
	

	private String[] generateODataX(ODataDefinition model) throws SQLException {
		return odata2ODataXTransformer.transform(model);
	}
	
	private String[] generateODataMs(ODataDefinition model) throws SQLException {
		return odata2ODataMTransformer.transform(model);
	}

	/**
	 * Concatenate list of strings.
	 *
	 * @param list
	 *            the list
	 * @param separator
	 *            the separator
	 * @return the string
	 */
	private static String concatenateListOfStrings(List<String> list, String separator) {
		StringBuffer buff = new StringBuffer();
		for (String s : list) {
			buff.append(s).append(separator);
		}
		return buff.toString();
	}
	
}
