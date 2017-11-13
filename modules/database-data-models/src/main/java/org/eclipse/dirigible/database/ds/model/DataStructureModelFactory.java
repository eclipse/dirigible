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

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;

/**
 * The factory for creation of the data structure models from source content.
 */
public class DataStructureModelFactory {

	/**
	 * Creates a table model from the raw content.
	 *
	 * @param content            the table definition
	 * @return the table model instance
	 */
	public static DataStructureTableModel parseTable(String content) {
		DataStructureTableModel result = GsonHelper.GSON.fromJson(content, DataStructureTableModel.class);
		result.setHash(DigestUtils.md5Hex(content));
		return result;
	}

	/**
	 * Creates a view model from the raw content.
	 *
	 * @param content            the view definition
	 * @return the view model instance
	 */
	public static DataStructureViewModel parseView(String content) {
		DataStructureViewModel result = GsonHelper.GSON.fromJson(content, DataStructureViewModel.class);
		result.setHash(DigestUtils.md5Hex(content));
		return result;
	}

	/**
	 * Creates a table model from the raw content.
	 *
	 * @param bytes            the table definition
	 * @return the table model instance
	 */
	public static DataStructureTableModel parseTable(byte[] bytes) {
		DataStructureTableModel result = GsonHelper.GSON.fromJson(new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8),
				DataStructureTableModel.class);
		result.setHash(DigestUtils.md5Hex(bytes));
		return result;
	}

	/**
	 * Creates a view model from the raw content.
	 *
	 * @param bytes            the view definition
	 * @return the view model instance
	 */
	public static DataStructureViewModel parseView(byte[] bytes) {
		DataStructureViewModel result = GsonHelper.GSON.fromJson(new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8),
				DataStructureViewModel.class);
		result.setHash(DigestUtils.md5Hex(bytes));
		return result;
	}

}
