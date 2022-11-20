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

import static java.text.MessageFormat.format;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * The factory for creation of the data structure models from source content.
 */
public class DataStructureModelFactory {

	/**
	 * Creates a table model from the raw content.
	 *
	 * @param content
	 *            the table definition
	 * @return the table model instance
	 */
	public static DataStructureTableModel parseTable(String content) {
		DataStructureTableModel result = GsonHelper.fromJson(content, DataStructureTableModel.class);
		result.setHash(DigestUtils.md5Hex(content));
		return result;
	}

	/**
	 * Creates a table model from the raw content.
	 *
	 * @param bytes
	 *            the table definition
	 * @return the table model instance
	 */
	public static DataStructureTableModel parseTable(byte[] bytes) {
		DataStructureTableModel result = GsonHelper.fromJson(
				new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8),
				DataStructureTableModel.class);
		result.setHash(DigestUtils.md5Hex(bytes));
		return result;
	}

	/**
	 * Creates a view model from the raw content.
	 *
	 * @param content
	 *            the view definition
	 * @return the view model instance
	 */
	public static DataStructureViewModel parseView(String content) {
		DataStructureViewModel result = GsonHelper.fromJson(content, DataStructureViewModel.class);
		result.setHash(DigestUtils.md5Hex(content));
		return result;
	}

	/**
	 * Creates a view model from the raw content.
	 *
	 * @param bytes
	 *            the view definition
	 * @return the view model instance
	 */
	public static DataStructureViewModel parseView(byte[] bytes) {
		DataStructureViewModel result = GsonHelper.fromJson(
				new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8),
				DataStructureViewModel.class);
		result.setHash(DigestUtils.md5Hex(bytes));
		return result;
	}

	/**
	 * Creates a data model from the raw content.
	 *
	 * @param location the location
	 * @param content            the data definition
	 * @return the data model instance
	 */
	public static DataStructureDataReplaceModel parseReplace(String location, String content) {
		DataStructureDataReplaceModel result = new DataStructureDataReplaceModel();
		setContentModelAttributes(location, content, result, IDataStructureModel.TYPE_REPLACE);
		return result;
	}

	/**
	 * Creates a data model from the raw content.
	 *
	 * @param location the location
	 * @param content            the data definition
	 * @return the data model instance
	 */
	public static DataStructureDataAppendModel parseAppend(String location, String content) {
		DataStructureDataAppendModel result = new DataStructureDataAppendModel();
		setContentModelAttributes(location, content, result, IDataStructureModel.TYPE_APPEND);
		return result;
	}

	/**
	 * Creates a data model from the raw content.
	 *
	 * @param location the location
	 * @param content            the data definition
	 * @return the data model instance
	 */
	public static DataStructureDataDeleteModel parseDelete(String location, String content) {
		DataStructureDataDeleteModel result = new DataStructureDataDeleteModel();
		setContentModelAttributes(location, content, result, IDataStructureModel.TYPE_DELETE);
		return result;
	}

	/**
	 * Creates a data model from the raw content.
	 *
	 * @param location the location
	 * @param content            the data definition
	 * @return the data model instance
	 */
	public static DataStructureDataUpdateModel parseUpdate(String location, String content) {
		DataStructureDataUpdateModel result = new DataStructureDataUpdateModel();
		setContentModelAttributes(location, content, result, IDataStructureModel.TYPE_UPDATE);
		return result;
	}
	
	/**
	 * Creates a data model from the raw content.
	 *
	 * @param location the location
	 * @param content            the data definition
	 * @return the data model instance
	 */
	public static DataStructureChangelogModel parseChangelog(String location, String content) {
		DataStructureChangelogModel result = new DataStructureChangelogModel();
		setContentModelAttributes(location, content, result, IDataStructureModel.TYPE_CHANGELOG);
		return result;
	}

	/**
	 * Sets the content model attributes.
	 *
	 * @param location the location
	 * @param content the content
	 * @param dataModel the data model
	 * @param type the type
	 */
	private static void setContentModelAttributes(String location, String content, DataStructureContentModel dataModel,
			String type) {
		dataModel.setLocation(location);
		dataModel.setName(FilenameUtils.getBaseName(location));
		dataModel.setType(type);
		dataModel.setContent(content);
		dataModel.setCreatedBy(UserFacade.getName());
		dataModel.setCreatedAt(new Timestamp(new java.util.Date().getTime()));
		dataModel.setHash(DigestUtils.md5Hex(content));
	}
	
	/**
	 * Creates a schema model from the raw content.
	 *
	 * @param location the location
	 * @param content            the schema definition
	 * @return the schema model instance
	 */
	public static DataStructureSchemaModel parseSchema(String location, String content) {
		DataStructureSchemaModel result = new DataStructureSchemaModel();
		setContentModelAttributes(location, content, result, IDataStructureModel.TYPE_SCHEMA);
		
			JsonElement root = GsonHelper.parseJson(content);
			JsonArray structures = root.getAsJsonObject().get("schema").getAsJsonObject().get("structures").getAsJsonArray();
			for (int i=0; i<structures.size(); i++) {
				JsonObject structure = structures.get(i).getAsJsonObject();
				String type = structure.get("type").getAsString();
				if ("table".equalsIgnoreCase(type)) {
					DataStructureTableModel table = new DataStructureTableModel();
					setTableAttributes(location, result, structure, type, table);
					result.getTables().add(table);
				} else if ("view".equalsIgnoreCase(type)) {
					DataStructureViewModel view = new DataStructureViewModel();
					setViewAttributes(location, result, structure, type, view);
					result.getViews().add(view);
				} else if ("foreignKey".equalsIgnoreCase(type)) {
					// skip for now
				} else {
					throw new IllegalArgumentException(format("Unknown data structure type [{0}] loaded from schema [{1}]", type, location));
				}
			}
			for (int i=0; i<structures.size(); i++) {
				JsonObject structure = structures.get(i).getAsJsonObject();
				String type = structure.get("type").getAsString();
				if ("foreignKey".equals(type)) {
					DataStructureTableConstraintForeignKeyModel foreignKey = new DataStructureTableConstraintForeignKeyModel();
					foreignKey.setName(structure.get("name").getAsString());
					foreignKey.setColumns(structure.get("columns").getAsString().split(","));
					foreignKey.setReferencedTable(structure.get("referencedTable").getAsString());
					foreignKey.setReferencedColumns(structure.get("referencedColumns").getAsString().split(","));
					String tableName = structure.get("table").getAsString();
					for (DataStructureTableModel table : result.getTables()) {
						if (table.getName().equals(tableName)) {
							// add the foreign key
							List<DataStructureTableConstraintForeignKeyModel> list = new ArrayList<DataStructureTableConstraintForeignKeyModel>(); 
							if (table.getConstraints().getForeignKeys() != null ) { 
								list.addAll(table.getConstraints().getForeignKeys());
							}
							list.add(foreignKey);
							table.getConstraints().getForeignKeys().addAll(list);
							// add the dependency for the topological sorting later
							DataStructureDependencyModel dependencyModel = new DataStructureDependencyModel(foreignKey.getReferencedTable(), "TABLE");
							table.getDependencies().add(dependencyModel);
							break;
						}
					}
				}
			}
		
		
		return result;
	}

	/**
	 * Sets the view attributes.
	 *
	 * @param location the location
	 * @param result the result
	 * @param structure the structure
	 * @param type the type
	 * @param view the view
	 */
	private static void setViewAttributes(String location, DataStructureSchemaModel result, JsonObject structure,
			String type, DataStructureViewModel view) {
		view.setLocation(location);
		view.setName(structure.get("name").getAsString());
		view.setType(type);
		view.setQuery(structure.get("columns").getAsJsonArray().get(0).getAsJsonObject().get("query").getAsString());
		view.setCreatedAt(result.getCreatedAt());
		view.setCreatedBy(result.getCreatedBy());
		view.setHash(result.getHash());
	}

	/**
	 * Sets the table attributes.
	 *
	 * @param location the location
	 * @param result the result
	 * @param structure the structure
	 * @param type the type
	 * @param table the table
	 */
	private static void setTableAttributes(String location, DataStructureSchemaModel result, JsonObject structure,
			String type, DataStructureTableModel table) {
		table.setLocation(location);
		table.setName(structure.get("name").getAsString());
		table.setType(type);
		table.setCreatedAt(result.getCreatedAt());
		table.setCreatedBy(result.getCreatedBy());
		table.setHash(result.getHash());
		JsonElement columnElement = structure.get("columns");
		if (columnElement.isJsonObject()) {
			JsonObject column = columnElement.getAsJsonObject();
			DataStructureTableColumnModel columnModel = new DataStructureTableColumnModel();
			setColumnAttributes(column, columnModel);
			table.getColumns().add(columnModel);
		} else if (columnElement.isJsonArray()) {
			JsonArray columns = columnElement.getAsJsonArray();
			for (int j=0; j<columns.size(); j++) {
				JsonObject column = columns.get(j).getAsJsonObject();
				DataStructureTableColumnModel columnModel = new DataStructureTableColumnModel();
				setColumnAttributes(column, columnModel);
				table.getColumns().add(columnModel);
			}
		} else {
			throw new IllegalArgumentException(format("Error in parsing columns of table [{0}] in schema [{1}]", table.getName(), location));
		}
	}

	/**
	 * Sets the column attributes.
	 *
	 * @param column the column
	 * @param columnModel the column model
	 */
	private static void setColumnAttributes(JsonObject column, DataStructureTableColumnModel columnModel) {
		columnModel.setName(column.get("name") != null && !column.get("name").isJsonNull() ? column.get("name").getAsString() : "unknown");
		columnModel.setType(column.get("type") != null && !column.get("type").isJsonNull()  ? column.get("type").getAsString() : "unknown");
		columnModel.setLength(column.get("length") != null && !column.get("length").isJsonNull()  ? column.get("length").getAsString() : null);
		columnModel.setPrimaryKey(column.get("primaryKey") != null && !column.get("primaryKey").isJsonNull()  ? column.get("primaryKey").getAsBoolean() : false);
		columnModel.setUnique(column.get("unique") != null && !column.get("unique").isJsonNull()  ? column.get("unique").getAsBoolean() : false);
		columnModel.setNullable(column.get("nullable") != null && !column.get("nullable").isJsonNull()  ? column.get("nullable").getAsBoolean() : false);
		columnModel.setDefaultValue(column.get("defaultValue") != null && !column.get("defaultValue").isJsonNull()  ? column.get("defaultValue").getAsString() : null);
		columnModel.setScale(column.get("scale") != null && !column.get("scale").isJsonNull() ? column.get("scale").getAsString() : null);
	}

}
