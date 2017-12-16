/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.ds.model;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;

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
		DataStructureTableModel result = GsonHelper.GSON.fromJson(content, DataStructureTableModel.class);
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
		DataStructureTableModel result = GsonHelper.GSON.fromJson(
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
		DataStructureViewModel result = GsonHelper.GSON.fromJson(content, DataStructureViewModel.class);
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
		DataStructureViewModel result = GsonHelper.GSON.fromJson(
				new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8),
				DataStructureViewModel.class);
		result.setHash(DigestUtils.md5Hex(bytes));
		return result;
	}

	/**
	 * Creates a data model from the raw content.
	 *
	 * @param content
	 *            the data definition
	 * @return the data model instance
	 */
	public static DataStructureDataReplaceModel parseReplace(String location, String content) {
		DataStructureDataReplaceModel result = new DataStructureDataReplaceModel();
		setDataModelAttributes(location, content, result, IDataStructureModel.TYPE_REPLACE);
		return result;
	}

	/**
	 * Creates a data model from the raw content.
	 *
	 * @param content
	 *            the data definition
	 * @return the data model instance
	 */
	public static DataStructureDataAppendModel parseAppend(String location, String content) {
		DataStructureDataAppendModel result = new DataStructureDataAppendModel();
		setDataModelAttributes(location, content, result, IDataStructureModel.TYPE_APPEND);
		return result;
	}

	/**
	 * Creates a data model from the raw content.
	 *
	 * @param content
	 *            the data definition
	 * @return the data model instance
	 */
	public static DataStructureDataDeleteModel parseDelete(String location, String content) {
		DataStructureDataDeleteModel result = new DataStructureDataDeleteModel();
		setDataModelAttributes(location, content, result, IDataStructureModel.TYPE_DELETE);
		return result;
	}

	/**
	 * Creates a data model from the raw content.
	 *
	 * @param content
	 *            the data definition
	 * @return the data model instance
	 */
	public static DataStructureDataUpdateModel parseUpdate(String location, String content) {
		DataStructureDataUpdateModel result = new DataStructureDataUpdateModel();
		setDataModelAttributes(location, content, result, IDataStructureModel.TYPE_UPDATE);
		return result;
	}

	private static void setDataModelAttributes(String location, String content, DataStructureDataModel dataModel,
			String type) {
		dataModel.setLocation(location);
		dataModel.setName(FilenameUtils.getBaseName(location));
		dataModel.setType(type);
		dataModel.setContent(content);
		dataModel.setCreatedBy(UserFacade.getName());
		dataModel.setCreatedAt(new Timestamp(new java.util.Date().getTime()));
		dataModel.setHash(DigestUtils.md5Hex(content));
	}

}
