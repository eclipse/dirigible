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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * {
 * "viewName":"view_name",
 * "query":"SELECT * FROM table_name",
 * "dependencies":
 * [
 * {
 * "name":"table_name",
 * "type":"TABLE",
 * }
 * ]
 * }
 */
public class ViewModel extends DataStructureModel {

	private static final String VIEW = "VIEW"; //$NON-NLS-1$
	private static final String VIEW_NAME = "viewName"; //$NON-NLS-1$
	private static final String QUERY = "query"; //$NON-NLS-1$

	private String query;

	/**
	 * The constructor from a raw content string
	 *
	 * @param content
	 * @throws EDataStructureModelFormatException
	 */
	public ViewModel(String content) throws EDataStructureModelFormatException {
		this.setType(VIEW);
		JsonParser parser = new JsonParser();
		JsonObject definitionObject = (JsonObject) parser.parse(content);

		// viewName
		JsonElement nameElement = definitionObject.get(VIEW_NAME);
		if (nameElement == null) {
			throw new EDataStructureModelFormatException(
					String.format(DataStructureModel.ELEMENT_S_DOES_NOT_EXIST_IN_THIS_MODEL_S, VIEW_NAME, content));
		}
		this.setName(nameElement.getAsString());

		// query
		JsonElement queryElement = definitionObject.get(QUERY);
		if (queryElement == null) {
			throw new EDataStructureModelFormatException(
					String.format(DataStructureModel.ELEMENT_S_DOES_NOT_EXIST_IN_THIS_MODEL_S, QUERY, this.getName()));
		}
		this.setQuery(queryElement.getAsString());

		fillDependencies(definitionObject);
	}

	/**
	 * Getter for the query field
	 *
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Setter for the query field
	 *
	 * @param query
	 */
	public void setQuery(String query) {
		this.query = query;
	}

}
