/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.odata2.api;

import java.io.InputStream;
import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.engine.odata2.definition.ODataContainerDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataDefinition;
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
	 * Generates the EDMX Metadata
	 * 
	 * @return the metadata
	 * @throws ODataException in case of an error
	 */
	public InputStream getMetadata() throws ODataException;

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
	ODataDefinition parseOData(String contentPath, String data);

	/**
	 * Getter for the OData entity
	 * 
	 * @param location
	 * @return
	 * @throws ODataException
	 */
	ODataDefinition getOData(String location) throws ODataException;

	/**
	 * Update the OData entity
	 * 
	 * @param location the location
	 * @param namespace the namespace
	 * @param hash the hash
	 * @throws ODataException in case of an error
	 */
	void updateOData(String location, String namespace, String hash) throws ODataException;

	/**
	 * Get all the OData entities
	 * @return the list of the OData entities
	 * @throws ODataException in case of an error
	 */
	List<ODataDefinition> getODatas() throws ODataException;

	/**
	 * Whether OData entity exists already or not
	 * 
	 * @param location the OData entity location
	 * @return true if exists and false otherwise
	 * @throws ODataException in case of an error
	 */
	boolean existsOData(String location) throws ODataException;

	/**
	 * Create the OData entity
	 * 
	 * @param location the location
	 * @param namespace the namespace
	 * @param hash the hash
	 * @return newly created OData entity
	 * @throws ODataException in case of an error
	 */
	ODataDefinition createOData(String location, String namespace, String hash) throws ODataException;

}
