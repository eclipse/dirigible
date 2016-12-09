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
import com.google.gson.JsonParser;

/**
 * {
 * "tableName":"table_name",
 * "columns":
 * [
 * {
 * "name":"id",
 * "type":"INTEGER",
 * "length":"0",
 * "notNull":"true",
 * "primaryKey":"true"
 * },
 * {
 * "name":"text",
 * "type":"VARCHAR",
 * "length":"32",
 * "notNull":"false",
 * "primaryKey":"false"
 * },
 * ],
 * "dependencies":
 * [
 * {
 * "name":"ST_PROC",
 * "type":"STORED_PROCEDURE",
 * }
 * ]
 * }
 */
public class TableModel extends DataStructureModel {

	private static final String TABLE = "TABLE"; //$NON-NLS-1$
	private static final String TABLE_NAME = "tableName"; //$NON-NLS-1$
	private static final String COLUMNS = "columns"; //$NON-NLS-1$
	private static final String COLUMN_NAME = "name"; //$NON-NLS-1$
	private static final String COLUMN_TYPE = "type"; //$NON-NLS-1$
	private static final String COLUMN_LENGTH = "length"; //$NON-NLS-1$
	private static final String COLUMN_NOT_NULL = "notNull"; //$NON-NLS-1$
	private static final String COLUMN_PRIMARY_KEY = "primaryKey"; //$NON-NLS-1$
	private static final String COLUMN_DEFAULT_VALUE = "defaultValue"; //$NON-NLS-1$

	private static final String ELEMENT_S_DOES_NOT_EXIST_IN_THIS_COLUMNS_ARRAY_IN_THE_TABLE_MODEL_S = Messages.TableModel_ELEMENT_S_DOES_NOT_EXIST_IN_THIS_COLUMNS_ARRAY_IN_THE_TABLE_MODEL_S;
	private static final String ELEMENT_S_DOES_NOT_EXIST_IN_THIS_COLUMN_S_IN_THE_TABLE_MODEL_S = Messages.TableModel_ELEMENT_S_DOES_NOT_EXIST_IN_THIS_COLUMN_S_IN_THE_TABLE_MODEL_S;
	private static final String ELEMENT_S_MUST_BE_A_SINGLE_ELEMENT_NOT_AN_ARRAY_IN_THIS_COLUMN_S_IN_THE_TABLE_MODEL_S = Messages.TableModel_ELEMENT_S_MUST_BE_A_SINGLE_ELEMENT_NOT_AN_ARRAY_IN_THIS_COLUMN_S_IN_THE_TABLE_MODEL_S;

	private List<TableColumnModel> columns = new ArrayList<TableColumnModel>();

	/**
	 * The constructor from a raw content string
	 *
	 * @param content
	 * @throws EDataStructureModelFormatException
	 */
	public TableModel(String content) throws EDataStructureModelFormatException {
		this.setType(TABLE);
		JsonParser parser = new JsonParser();
		JsonObject definitionObject = (JsonObject) parser.parse(content);

		// tableName
		JsonElement nameElement = definitionObject.get(TABLE_NAME);
		if (nameElement == null) {
			throw new EDataStructureModelFormatException(
					String.format(DataStructureModel.ELEMENT_S_DOES_NOT_EXIST_IN_THIS_MODEL_S, TABLE_NAME, content));
		}
		this.setName(nameElement.getAsString());

		fillColumns(definitionObject);

		fillDependencies(definitionObject);
	}

	private void fillColumns(JsonObject definitionObject) throws EDataStructureModelFormatException {
		// columns
		JsonElement columnsElement = definitionObject.get(COLUMNS);
		if (columnsElement == null) {
			throw new EDataStructureModelFormatException(
					String.format(DataStructureModel.ELEMENT_S_DOES_NOT_EXIST_IN_THIS_MODEL_S, COLUMNS, this.getName()));
		}
		if (!columnsElement.isJsonArray()) {
			throw new EDataStructureModelFormatException(
					String.format(DataStructureModel.ELEMENT_S_MUST_BE_ARRAY_IN_THE_MODEL_S, COLUMNS, this.getName()));
		}
		JsonArray columnsArray = columnsElement.getAsJsonArray();
		for (JsonElement jsonElement : columnsArray) {
			if (jsonElement instanceof JsonObject) {
				JsonObject jsonObject = (JsonObject) jsonElement;

				// column's name
				JsonElement columnNameElement = jsonObject.get(COLUMN_NAME);
				if (columnNameElement == null) {
					throw new EDataStructureModelFormatException(
							String.format(ELEMENT_S_DOES_NOT_EXIST_IN_THIS_COLUMNS_ARRAY_IN_THE_TABLE_MODEL_S, COLUMN_NAME, this.getName()));
				}
				if (columnNameElement instanceof JsonArray) {
					throw new EDataStructureModelFormatException(String
							.format(DataStructureModel.ELEMENT_S_MUST_BE_A_SINGLE_ELEMENT_NOT_AN_ARRAY_IN_THIS_MODEL_S, COLUMN_NAME, this.getName()));
				}
				String columnName = columnNameElement.getAsString();

				// column's type
				JsonElement columnTypeElement = jsonObject.get(COLUMN_TYPE);
				if (columnTypeElement == null) {
					throw new EDataStructureModelFormatException(
							String.format(ELEMENT_S_DOES_NOT_EXIST_IN_THIS_COLUMN_S_IN_THE_TABLE_MODEL_S, COLUMN_TYPE, columnName, this.getName()));
				}
				if (columnTypeElement instanceof JsonArray) {
					throw new EDataStructureModelFormatException(
							String.format(ELEMENT_S_MUST_BE_A_SINGLE_ELEMENT_NOT_AN_ARRAY_IN_THIS_COLUMN_S_IN_THE_TABLE_MODEL_S, COLUMN_TYPE,
									columnName, this.getName()));
				}
				String columnType = columnTypeElement.getAsString();

				// column's length
				JsonElement columnLengthElement = jsonObject.get(COLUMN_LENGTH);
				if (columnLengthElement == null) {
					throw new EDataStructureModelFormatException(
							String.format(ELEMENT_S_DOES_NOT_EXIST_IN_THIS_COLUMN_S_IN_THE_TABLE_MODEL_S, COLUMN_LENGTH, columnName, this.getName()));
				}
				if (columnLengthElement instanceof JsonArray) {
					throw new EDataStructureModelFormatException(
							String.format(ELEMENT_S_MUST_BE_A_SINGLE_ELEMENT_NOT_AN_ARRAY_IN_THIS_COLUMN_S_IN_THE_TABLE_MODEL_S, COLUMN_LENGTH,
									columnName, this.getName()));
				}
				String columnLength = columnLengthElement.getAsString();

				// column's notNull
				JsonElement columnNotNullElement = jsonObject.get(COLUMN_NOT_NULL);
				if (columnNotNullElement == null) {
					throw new EDataStructureModelFormatException(String.format(ELEMENT_S_DOES_NOT_EXIST_IN_THIS_COLUMN_S_IN_THE_TABLE_MODEL_S,
							COLUMN_NOT_NULL, columnName, this.getName()));
				}
				if (columnNotNullElement instanceof JsonArray) {
					throw new EDataStructureModelFormatException(
							String.format(ELEMENT_S_MUST_BE_A_SINGLE_ELEMENT_NOT_AN_ARRAY_IN_THIS_COLUMN_S_IN_THE_TABLE_MODEL_S, COLUMN_NOT_NULL,
									columnName, this.getName()));
				}
				boolean columnNotNull = columnNotNullElement.getAsBoolean();

				// column's primaryKey
				JsonElement columnPrimaryKeyElement = jsonObject.get(COLUMN_PRIMARY_KEY);
				if (columnPrimaryKeyElement == null) {
					throw new EDataStructureModelFormatException(String.format(ELEMENT_S_DOES_NOT_EXIST_IN_THIS_COLUMN_S_IN_THE_TABLE_MODEL_S,
							COLUMN_PRIMARY_KEY, columnName, this.getName()));
				}
				if (columnPrimaryKeyElement instanceof JsonArray) {
					throw new EDataStructureModelFormatException(
							String.format(ELEMENT_S_MUST_BE_A_SINGLE_ELEMENT_NOT_AN_ARRAY_IN_THIS_COLUMN_S_IN_THE_TABLE_MODEL_S, COLUMN_PRIMARY_KEY,
									columnName, this.getName()));
				}
				boolean columnPrimaryKey = columnPrimaryKeyElement.getAsBoolean();

				// column's defaultValue
				JsonElement columnDefaultValueElement = jsonObject.get(COLUMN_DEFAULT_VALUE);
				if (columnDefaultValueElement == null) {
					throw new EDataStructureModelFormatException(String.format(ELEMENT_S_DOES_NOT_EXIST_IN_THIS_COLUMN_S_IN_THE_TABLE_MODEL_S,
							COLUMN_DEFAULT_VALUE, columnName, this.getName()));
				}
				if (columnDefaultValueElement instanceof JsonArray) {
					throw new EDataStructureModelFormatException(
							String.format(ELEMENT_S_MUST_BE_A_SINGLE_ELEMENT_NOT_AN_ARRAY_IN_THIS_COLUMN_S_IN_THE_TABLE_MODEL_S, COLUMN_DEFAULT_VALUE,
									columnName, this.getName()));
				}
				String columnDefaultValue = columnDefaultValueElement.getAsString();

				TableColumnModel tableColumnModel = new TableColumnModel(columnName, columnType, columnLength, columnNotNull, columnPrimaryKey,
						columnDefaultValue);
				this.columns.add(tableColumnModel);
			}
		}
	}

	public List<TableColumnModel> getColumns() {
		return columns;
	}

}
