/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.ext.db.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * The basis for all the data structure models
 */
public class DataStructureModel {

	private static final String DEPENDENCIES = "dependencies"; //$NON-NLS-1$
	private static final String DEPENDENCY_NAME = "name"; //$NON-NLS-1$
	private static final String DEPENDENCY_TYPE = "type"; //$NON-NLS-1$

	static final String ELEMENT_S_DOES_NOT_EXIST_IN_THIS_MODEL_S = Messages.DataStructureModel_ELEMENT_S_DOES_NOT_EXIST_IN_THIS_MODEL_S;
	static final String ELEMENT_S_MUST_BE_ARRAY_IN_THE_MODEL_S = Messages.DataStructureModel_ELEMENT_S_MUST_BE_ARRAY_IN_THE_MODEL_S;
	static final String ELEMENT_S_MUST_BE_A_SINGLE_ELEMENT_NOT_AN_ARRAY_IN_THIS_MODEL_S = Messages.DataStructureModel_ELEMENT_S_MUST_BE_A_SINGLE_ELEMENT_NOT_AN_ARRAY_IN_THIS_MODEL_S;
	private static final String ELEMENT_S_DOES_NOT_EXIST_IN_THIS_DEPENDENCIES_ARRAY_IN_THE_TABLE_MODEL_S = Messages.DataStructureModel_ELEMENT_S_DOES_NOT_EXIST_IN_THIS_DEPENDENCIES_ARRAY_IN_THE_TABLE_MODEL_S;
	private static final String ELEMENT_S_DOES_NOT_EXIST_IN_THIS_DEPENDENCY_S_IN_THE_TABLE_MODEL_S = Messages.DataStructureModel_ELEMENT_S_DOES_NOT_EXIST_IN_THIS_DEPENDENCY_S_IN_THE_TABLE_MODEL_S;
	private static final String ELEMENT_S_MUST_BE_A_SINGLE_ELEMENT_NOT_AN_ARRAY_IN_THIS_DEPENDENCY_S_IN_THE_TABLE_MODEL_S = Messages.DataStructureModel_ELEMENT_S_MUST_BE_A_SINGLE_ELEMENT_NOT_AN_ARRAY_IN_THIS_DEPENDENCY_S_IN_THE_TABLE_MODEL_S;

	private String name;

	private String type;

	private List<DependencyModel> dependencies = new ArrayList<DependencyModel>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<DependencyModel> getDependencies() {
		return dependencies;
	}

	protected void fillDependencies(JsonObject definitionObject) throws EDataStructureModelFormatException {
		// dependencies
		JsonElement dependenciesElement = definitionObject.get(DEPENDENCIES);
		if (dependenciesElement == null) {
			// throw new EDataStructureModelFormatException(String.format(ELEMENT_S_DOES_NOT_EXIST_IN_THIS_MODEL_S,
			// DEPENDENCIES, this.getName()));

			// backward compatibility - dependencies is optional
			return;
		}
		if (!dependenciesElement.isJsonArray()) {
			throw new EDataStructureModelFormatException(String.format(ELEMENT_S_MUST_BE_ARRAY_IN_THE_MODEL_S, DEPENDENCIES, this.getName()));
		}
		JsonArray dependenciesArray = dependenciesElement.getAsJsonArray();
		for (JsonElement jsonElement : dependenciesArray) {
			if (jsonElement instanceof JsonObject) {
				JsonObject jsonObject = (JsonObject) jsonElement;

				// dependency's name
				JsonElement dependencyNameElement = jsonObject.get(DEPENDENCY_NAME);
				if (dependencyNameElement == null) {
					throw new EDataStructureModelFormatException(
							String.format(ELEMENT_S_DOES_NOT_EXIST_IN_THIS_DEPENDENCIES_ARRAY_IN_THE_TABLE_MODEL_S, DEPENDENCY_NAME, this.getName()));
				}
				if (dependencyNameElement instanceof JsonArray) {
					throw new EDataStructureModelFormatException(
							String.format(ELEMENT_S_MUST_BE_A_SINGLE_ELEMENT_NOT_AN_ARRAY_IN_THIS_MODEL_S, DEPENDENCY_NAME, this.getName()));
				}
				String dependencyName = dependencyNameElement.getAsString();

				// dependency's type
				JsonElement dependencyTypeElement = jsonObject.get(DEPENDENCY_TYPE);
				if (dependencyTypeElement == null) {
					throw new EDataStructureModelFormatException(String.format(ELEMENT_S_DOES_NOT_EXIST_IN_THIS_DEPENDENCY_S_IN_THE_TABLE_MODEL_S,
							DEPENDENCY_TYPE, dependencyName, this.getName()));
				}
				if (dependencyTypeElement instanceof JsonArray) {
					throw new EDataStructureModelFormatException(
							String.format(ELEMENT_S_MUST_BE_A_SINGLE_ELEMENT_NOT_AN_ARRAY_IN_THIS_DEPENDENCY_S_IN_THE_TABLE_MODEL_S, DEPENDENCY_TYPE,
									dependencyName, this.getName()));
				}
				String dependencyType = dependencyTypeElement.getAsString();

				DependencyModel dependencyModel = new DependencyModel(dependencyName, dependencyType);
				this.dependencies.add(dependencyModel);
			}
		}
	}
}
