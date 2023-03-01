/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.cms.csvim.api;

import java.util.List;

import org.eclipse.dirigible.cms.csvim.definition.CsvDefinition;
import org.eclipse.dirigible.cms.csvim.definition.CsvimDefinition;
import org.eclipse.dirigible.commons.api.service.ICoreService;

/**
 * The Interface ICsvimCoreService.
 */
public interface ICsvimCoreService extends ICoreService {

	/** The Constant FILE_EXTENSION_CSVIM. */
	public static final String FILE_EXTENSION_CSVIM = ".csvim";

	/** The Constant FILE_EXTENSION_CSV. */
	public static final String FILE_EXTENSION_CSV = ".csv";

	// CSVIM

	/**
	 * Creates the CSVIM.
	 *
	 * @param location the location
	 * @param hash     the hash
	 * @return the CSVIM definition
	 * @throws CsvimException the extensions exception
	 */
	public CsvimDefinition createCsvim(String location, String hash) throws CsvimException;

	/**
	 * Gets the CSVIM.
	 *
	 * @param location the location
	 * @return the CSVIM
	 * @throws CsvimException the extensions exception
	 */
	public CsvimDefinition getCsvim(String location) throws CsvimException;

	/**
	 * Exists CSVIM.
	 *
	 * @param location the location
	 * @return true, if successful
	 * @throws CsvimException the extensions exception
	 */
	public boolean existsCsvim(String location) throws CsvimException;

	/**
	 * Removes the CSVIM.
	 *
	 * @param location the location
	 * @throws CsvimException the extensions exception
	 */
	public void removeCsvim(String location) throws CsvimException;

	/**
	 * Update CSVIM.
	 *
	 * @param location the location
	 * @param hash     the hash
	 * @throws CsvimException the extensions exception
	 */
	public void updateCsvim(String location, String hash) throws CsvimException;

	/**
	 * Gets the CSVIMs.
	 *
	 * @return the CSVIMs
	 * @throws CsvimException the extensions exception
	 */
	public List<CsvimDefinition> getCsvims() throws CsvimException;

	/**
	 * Parses the CSVIM.
	 *
	 * @param json the json
	 * @return the CSVIM definition
	 */
	public CsvimDefinition parseCsvim(String json);

	/**
	 * Parses the CSVIM.
	 *
	 * @param json the json
	 * @return the CSVIM definition
	 */
	public CsvimDefinition parseCsvim(byte[] json);

	/**
	 * Serialize CSVIM.
	 *
	 * @param csvimDefinition the CSVIM definition
	 * @return the string
	 */
	public String serializeCsvim(CsvimDefinition csvimDefinition);

	// CSV

	/**
	 * Creates the CSV.
	 *
	 * @param location the location
	 * @param hash     the hash
	 * @return the CSV definition
	 * @throws CsvimException the extensions exception
	 */
	public CsvDefinition createCsv(String location, String hash) throws CsvimException;

	/**
	 * Gets the CSV.
	 *
	 * @param location the location
	 * @return the CSV
	 * @throws CsvimException the extensions exception
	 */
	public CsvDefinition getCsv(String location) throws CsvimException;

	/**
	 * Exists CSV.
	 *
	 * @param location the location
	 * @return true, if successful
	 * @throws CsvimException the extensions exception
	 */
	public boolean existsCsv(String location) throws CsvimException;

	/**
	 * Removes the CSV.
	 *
	 * @param location the location
	 * @throws CsvimException the extensions exception
	 */
	public void removeCsv(String location) throws CsvimException;

	/**
	 * Update CSV.
	 *
	 * @param location the location
	 * @param hash     the hash
	 * @param imported the flag whether it is already imported
	 * @throws CsvimException the extensions exception
	 */
	public void updateCsv(String location, String hash, Boolean imported) throws CsvimException;

	/**
	 * Gets the CSVs.
	 *
	 * @return the CSVs
	 * @throws CsvimException the extensions exception
	 */
	public List<CsvDefinition> getCsvs() throws CsvimException;

}
