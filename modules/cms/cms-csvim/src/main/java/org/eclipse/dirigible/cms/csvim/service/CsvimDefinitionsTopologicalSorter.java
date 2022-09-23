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
package org.eclipse.dirigible.cms.csvim.service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.dirigible.cms.csvim.definition.CsvFileDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class CsvimDefinitionsTopologicalSorter.
 */
public class CsvimDefinitionsTopologicalSorter {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(CsvimDefinitionsTopologicalSorter.class);

	/**
	 * Sort.
	 *
	 * @param csvFileDefinitions the csv file definitions
	 * @param sortedConfigurationDefinitions the sorted configuration definitions
	 * @param connection the connection
	 */
	public static void sort(List<CsvFileDefinition> csvFileDefinitions,
			List<CsvFileDefinition> sortedConfigurationDefinitions, Connection connection) {
		Map<String, CsvFileDefinition> mappedCsvFileDefinititions = new HashMap<>();

		for (CsvFileDefinition csvFileDefinition : csvFileDefinitions) {
			mappedCsvFileDefinititions.put(csvFileDefinition.getTable(), csvFileDefinition);
		}

		Set<CsvFileDefinition> visitedCsvFileDefinitions = new HashSet<>();

		try {
			DatabaseMetaData metaData = connection.getMetaData();

			for (Entry<String, CsvFileDefinition> entry : mappedCsvFileDefinititions.entrySet()) {
				Set<CsvFileDefinition> cyclingDependencySet = new HashSet<>();
				visitCsvFileDefinition(entry.getValue(), visitedCsvFileDefinitions,
						sortedConfigurationDefinitions, metaData, mappedCsvFileDefinititions,
						cyclingDependencySet);
				if (!sortedConfigurationDefinitions.contains(entry.getValue())) {
					sortedConfigurationDefinitions.add(entry.getValue());
				}
			}
		} catch (SQLException exception) {
			if (logger.isErrorEnabled()) {logger.error(String.format("An error occurred while trying to get metadata. %s", exception.getMessage()), exception);}
		}
	}

	/**
	 * Visit csv file definition.
	 *
	 * @param csvFileDefinition the csv file definition
	 * @param visitedCsvFileDefinitions the visited csv file definitions
	 * @param sortedCsvFileDefinitions the sorted csv file definitions
	 * @param metaData the meta data
	 * @param mappedCsvFileDefinititions the mapped csv file definititions
	 * @param cyclingDependencySet the cycling dependency set
	 * @throws SQLException the SQL exception
	 */
	private static void visitCsvFileDefinition(CsvFileDefinition csvFileDefinition,
			Set<CsvFileDefinition> visitedCsvFileDefinitions,
			List<CsvFileDefinition> sortedCsvFileDefinitions, DatabaseMetaData metaData,
			Map<String, CsvFileDefinition> mappedCsvFileDefinititions,
			Set<CsvFileDefinition> cyclingDependencySet) throws SQLException {

		if (mappedCsvFileDefinititions.containsKey(csvFileDefinition.getTable())) {
			if (!visitedCsvFileDefinitions.contains(csvFileDefinition)) {
				visitedCsvFileDefinitions.add(csvFileDefinition);
				if (!cyclingDependencySet.contains(csvFileDefinition)) {
					cyclingDependencySet.add(csvFileDefinition);
					try {
						ResultSet foreignKeys = metaData.getImportedKeys(null, csvFileDefinition.getSchema(),
								csvFileDefinition.getTable());
						while (foreignKeys.next()) {
							String pk_table = foreignKeys.getString("PKTABLE_NAME");
							CsvFileDefinition dependencyConfigDefinition = mappedCsvFileDefinititions
									.get(pk_table);
							if (!visitedCsvFileDefinitions.contains(dependencyConfigDefinition)) {
								visitCsvFileDefinition(dependencyConfigDefinition,
										visitedCsvFileDefinitions, sortedCsvFileDefinitions, metaData,
										mappedCsvFileDefinititions, cyclingDependencySet);
								if (!sortedCsvFileDefinitions
										.contains(mappedCsvFileDefinititions.get(pk_table))) {
									sortedCsvFileDefinitions.add(mappedCsvFileDefinititions.get(pk_table));
								}
							}
							if (!sortedCsvFileDefinitions.contains(csvFileDefinition)) {
								sortedCsvFileDefinitions.add(csvFileDefinition);
							}
						}
					} catch (SQLException exception) {
						if (logger.isErrorEnabled()) {logger.error(String.format("An error occurred while trying to get metadata. %s", exception.getMessage()), exception);}
					}
				} else {
					throw new SQLException(String.format("Cyclic dependency in %s ", csvFileDefinition.getTable()));
				}
			}
		}
	}

}
