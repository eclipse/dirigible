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
package org.eclipse.dirigible.engine.odata2.api;

import java.io.InputStream;
import java.util.List;

import org.apache.olingo.odata2.api.exception.ODataException;
import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.engine.odata2.definition.ODataContainerDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataHandlerDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataMappingDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataSchemaDefinition;

/**
 * The Interface IODataCoreService.
 */
public interface IODataCoreService extends ICoreService {
	
	/** The Constant FILE_EXTENSION_ODATA_SCHEMA. */
	public static final String FILE_EXTENSION_ODATA_SCHEMA = ".odatax";

	/** The Constant FILE_EXTENSION_ODATA_MAPPING. */
	public static final String FILE_EXTENSION_ODATA_MAPPING = ".odatam";
	
	/** The Constant FILE_EXTENSION_ODATA. */
	public static final String FILE_EXTENSION_ODATA = ".odata";

	

	// Schema

	/**
	 * Creates the schema.
	 *
	 * @param location
	 *            the location
	 * @param content
	 *            the content
	 * @return the schema definition
	 * @throws ODataException
	 *             the Mapping exception
	 */
	public ODataSchemaDefinition createSchema(String location, byte[] content) throws ODataException;

	/**
	 * Gets the schema.
	 *
	 * @param location
	 *            the location
	 * @return the schema
	 * @throws ODataException
	 *             the Mapping exception
	 */
	public ODataSchemaDefinition getSchema(String location) throws ODataException;

	/**
	 * Exists schema.
	 *
	 * @param location
	 *            the location
	 * @return true, if successful
	 * @throws ODataException
	 *             the Mapping exception
	 */
	public boolean existsSchema(String location) throws ODataException;

	/**
	 * Removes the schema.
	 *
	 * @param location
	 *            the location
	 * @throws ODataException
	 *             the Mapping exception
	 */
	public void removeSchema(String location) throws ODataException;

	/**
	 * Update schema.
	 *
	 * @param location
	 *            the location
	 * @param content
	 *            the content
	 * @throws ODataException
	 *             the Mapping exception
	 */
	public void updateSchema(String location, byte[] content) throws ODataException;

	/**
	 * Gets the schemas.
	 *
	 * @return the schemas
	 * @throws ODataException
	 *             the Mapping exception
	 */
	public List<ODataSchemaDefinition> getSchemas() throws ODataException;

	// Mapping

	/**
	 * Creates the mapping.
	 *
	 * @param location
	 *            the location
	 * @param content
	 *            the content
	 * @return the mapping definition
	 * @throws ODataException
	 *             the Mapping exception
	 */
	public ODataMappingDefinition createMapping(String location, byte[] content) throws ODataException;

	/**
	 * Gets the mapping.
	 *
	 * @param location
	 *            the location
	 * @return the mapping
	 * @throws ODataException
	 *             the Mapping exception
	 */
	public ODataMappingDefinition getMapping(String location) throws ODataException;

	/**
	 * Exists mapping.
	 *
	 * @param location
	 *            the location
	 * @return true, if successful
	 * @throws ODataException
	 *             the Mapping exception
	 */
	public boolean existsMapping(String location) throws ODataException;

	/**
	 * Removes the mapping.
	 *
	 * @param location
	 *            the location
	 * @throws ODataException
	 *             the Mapping exception
	 */
	public void removeMapping(String location) throws ODataException;

	/**
	 * Update mapping.
	 *
	 * @param location
	 *            the location
	 * @param content
	 *            the content
	 * @throws ODataException
	 *             the Mapping exception
	 */
	public void updateMapping(String location, byte[] content) throws ODataException;

	/**
	 * Gets the Mappings.
	 *
	 * @return the Mapping
	 * @throws ODataException
	 *             the Mapping exception
	 */
	public List<ODataMappingDefinition> getMappings() throws ODataException;

	/**
	 * Removes all the mappings matching given location.
	 *
	 * @param location
	 *            the location
	 * @throws ODataException
	 *             the Mapping exception
	 */
	void removeMappings(String location) throws ODataException;
	
	
	// Container

	/**
	 * Creates the container.
	 *
	 * @param location
	 *            the location
	 * @param content
	 *            the content
	 * @return the container definition
	 * @throws ODataException
	 *             the Mapping exception
	 */
	public ODataContainerDefinition createContainer(String location, byte[] content) throws ODataException;

	/**
	 * Gets the container.
	 *
	 * @param location
	 *            the location
	 * @return the container
	 * @throws ODataException
	 *             the Mapping exception
	 */
	public ODataContainerDefinition getContainer(String location) throws ODataException;

	/**
	 * Exists container.
	 *
	 * @param location
	 *            the location
	 * @return true, if successful
	 * @throws ODataException
	 *             the Mapping exception
	 */
	public boolean existsContainer(String location) throws ODataException;

	/**
	 * Removes the container.
	 *
	 * @param location
	 *            the location
	 * @throws ODataException
	 *             the Mapping exception
	 */
	public void removeContainer(String location) throws ODataException;

	/**
	 * Update container.
	 *
	 * @param location
	 *            the location
	 * @param content
	 *            the content
	 * @throws ODataException
	 *             the Mapping exception
	 */
	public void updateContainer(String location, byte[] content) throws ODataException;

	/**
	 * Gets the containers.
	 *
	 * @return the containers
	 * @throws ODataException
	 *             the Mapping exception
	 */
	public List<ODataContainerDefinition> getContainers() throws ODataException;
	
	
	
	
	// Parsers

	/**
	 * Parse the *.odata artefact
	 * 
	 * @param contentPath the path
	 * @param data the content
	 * @return the ODataDefinition object
	 */
	public ODataDefinition parseOData(String contentPath, String data);

	/**
	 * Getter for the OData entity.
	 *
	 * @param location the location
	 * @return ODataDefinition
	 * @throws ODataException in case of an error
	 */
	public ODataDefinition getOData(String location) throws ODataException;

	/**
	 * Update the OData entity.
	 *
	 * @param location the location
	 * @param namespace the namespace
	 * @param hash the hash
	 * @throws ODataException in case of an error
	 */
	public void updateOData(String location, String namespace, String hash) throws ODataException;

	/**
	 * Get all the OData entities.
	 *
	 * @return the list of the OData entities
	 * @throws ODataException in case of an error
	 */
	public List<ODataDefinition> getODatas() throws ODataException;

	/**
	 * Whether OData entity exists already or not.
	 *
	 * @param location the OData entity location
	 * @return true if exists and false otherwise
	 * @throws ODataException in case of an error
	 */
	public boolean existsOData(String location) throws ODataException;

	/**
	 * Create the OData entity.
	 *
	 * @param location the location
	 * @param namespace the namespace
	 * @param hash the hash
	 * @return newly created OData entity
	 * @throws ODataException in case of an error
	 */
	public ODataDefinition createOData(String location, String namespace, String hash) throws ODataException;
	
	
	
	// Handler

	/**
	 * Creates the handler.
	 *
	 * @param location the location
	 * @param namespace the namespace
	 * @param name the name
	 * @param method the method
	 * @param type the type
	 * @param handler the handler
	 * @return the handler definition
	 * @throws ODataException in case of an error
	 */
	public ODataHandlerDefinition createHandler(String location, String namespace, String name, String method, String type, String handler) throws ODataException;

	/**
	 * Gets the handler.
	 *
	 * @param location the location
	 * @param namespace the namespace
	 * @param name the name
	 * @param method the method
	 * @param type the type
	 * @return the handler
	 * @throws ODataException in case of an error
	 */
	public ODataHandlerDefinition getHandler(String location, String namespace, String name, String method, String type) throws ODataException;
	
	/**
	 * Gets the handlers.
	 *
	 * @param namespace the namespace
	 * @param name the name
	 * @param method the method
	 * @param type the type
	 * @return the list of handlers
	 * @throws ODataException in case of an error
	 */
	public List<ODataHandlerDefinition> getHandlers(String namespace, String name, String method, String type) throws ODataException;

	/**
	 * Exists handler.
	 *
	 * @param namespace the namespace
	 * @param name the name
	 * @param method the method
	 * @param type the type
	 * @return true if exists at least one
	 * @throws ODataException in case of an error
	 */
	public boolean existsHandler(String namespace, String name,  String method, String type) throws ODataException;

	/**
	 * Removes the handler.
	 *
	 * @param location the location
	 * @param namespace the namespace
	 * @param name the name
	 * @param method the method
	 * @param type the type
	 * @throws ODataException in case of an error
	 */
	public void removeHandler(String location, String namespace, String name, String method, String type) throws ODataException;
	
	/**
	 * Removes the handlers.
	 *
	 * @param location the location
	 * @throws ODataException in case of an error
	 */
	public void removeHandlers(String location) throws ODataException;

	/**
	 * Update handler.
	 *
	 * @param location the location
	 * @param namespace the namespace
	 * @param name the name
	 * @param method the method
	 * @param type the type
	 * @param handler the handler
	 * @throws ODataException in case of an error
	 */
	public void updateHandler(String location, String namespace, String name, String method, String type, String handler) throws ODataException;

	/**
	 * Gets the handlers.
	 *
	 * @return the handlers
	 * @throws ODataException in case of an error
	 */
	public List<ODataHandlerDefinition> getAllHandlers() throws ODataException;
	
	
	
	
	// Overall
	
	/**
	 * Generates the EDMX Metadata.
	 *
	 * @return the metadata
	 * @throws ODataException in case of an error
	 */
	public InputStream getMetadata() throws ODataException;

}
