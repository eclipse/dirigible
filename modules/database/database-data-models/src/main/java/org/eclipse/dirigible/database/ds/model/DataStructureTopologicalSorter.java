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
package org.eclipse.dirigible.database.ds.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Data models sorter utility.
 */
public class DataStructureTopologicalSorter {

	/**
	 * Sorts the data models.
	 *
	 * @param input            the models that will be sorted
	 * @param output            the output of the sorting
	 * @param external            the external dependencies
	 * @throws DataStructureModelException the data structure model exception
	 */
	public static void sort(Map<String, DataStructureModel> input, List<String> output, List<String> external) throws DataStructureModelException {
		List<String> processing = new ArrayList<String>();
		for (Entry<String, DataStructureModel> entry : input.entrySet()) {
			DataStructureModel dataStructureModel = entry.getValue();
			followDependencies(input, output, external, processing, dataStructureModel);
			String name = dataStructureModel.getName();
			if (!output.contains(name)) {
				output.add(name);
			}
		}

	}

	/**
	 * Follow dependencies.
	 *
	 * @param input the input
	 * @param output the output
	 * @param external the external
	 * @param processing the processing
	 * @param dataStructureModel the data structure model
	 * @throws DataStructureModelException the data structure model exception
	 */
	protected static void followDependencies(Map<String, DataStructureModel> input, List<String> output, List<String> external,
			List<String> processing, DataStructureModel dataStructureModel) throws DataStructureModelException {
		processing.add(dataStructureModel.getName());
		List<DataStructureDependencyModel> dependencies = dataStructureModel.getDependencies();
		for (DataStructureDependencyModel dependencyModel : dependencies) {
			String dependencyName = dependencyModel.getName();
			if (input.containsKey(dependencyName)) {
				if (processing.contains(dependencyName)) {
					throw new DataStructureModelException(String.format("Cyclic dependency %s in %s", dataStructureModel.getName(), dependencyName));
				}
				DataStructureModel dependentModel = input.get(dependencyName);
				if (!output.contains(dependencyName)) {
					followDependencies(input, output, external, processing, dependentModel);
					output.add(dependencyName);
				}
			} else {
				external.add(dependencyName);
			}
		}
		processing.remove(dataStructureModel.getName());
	}

}
