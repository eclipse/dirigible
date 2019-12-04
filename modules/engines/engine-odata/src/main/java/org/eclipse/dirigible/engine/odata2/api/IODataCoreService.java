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
package org.eclipse.dirigible.engine.odata2.api;

import java.io.InputStream;
import java.util.List;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.engine.odata2.definition.ODataMappingDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataSchemaDefinition;

/**
 * The Interface IMappingCoreService.
 */
public interface IODataCoreService extends ICoreService {
	
	/** The Constant FILE_EXTENSION_ODATA_SCHEMA. */
	public static final String FILE_EXTENSION_ODATA_SCHEMA = ".odatax";

	/** The Constant FILE_EXTENSION_ODATA_MAPPING. */
	public static final String FILE_EXTENSION_ODATA_MAPPING = ".odatam";

	

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
	public ODataSchemaDefinition createSchema(String location, String content) throws ODataException;

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
	public void updateSchema(String location, String content) throws ODataException;

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
	public ODataMappingDefinition createMapping(String location, String content) throws ODataException;

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
	public void updateMapping(String location, String content) throws ODataException;

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
	 * @throws ODataException 
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

}
