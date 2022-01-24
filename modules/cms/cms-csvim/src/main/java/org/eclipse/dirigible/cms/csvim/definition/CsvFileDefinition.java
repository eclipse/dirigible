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
package org.eclipse.dirigible.cms.csvim.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvFileDefinition {
	
	private String table;
	private String schema;
	private String file;
	private Boolean header = Boolean.FALSE;
	private Boolean useHeaderNames = Boolean.FALSE;
	private String delimField;
	private String delimEnclosing;
	private Boolean distinguishEmptyFromNull = Boolean.TRUE;
	private List<CsvimKeyDefinition> keys;
	/**
	 * @return the table
	 */
	public String getTable() {
		return table;
	}
	/**
	 * @param table the table to set
	 */
	public void setTable(String table) {
		this.table = table;
	}
	/**
	 * @return the schema
	 */
	public String getSchema() {
		return schema;
	}
	/**
	 * @param schema the schema to set
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}
	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}
	/**
	 * @param file the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}
	/**
	 * @return the header
	 */
	public Boolean getHeader() {
		return header;
	}
	/**
	 * @param header the header to set
	 */
	public void setHeader(Boolean header) {
		this.header = header;
	}
	/**
	 * @return the useHeaderNames
	 */
	public Boolean getUseHeaderNames() {
		return useHeaderNames;
	}
	/**
	 * @param useHeaderNames the useHeaderNames to set
	 */
	public void setUseHeaderNames(Boolean useHeaderNames) {
		this.useHeaderNames = useHeaderNames;
	}
	/**
	 * @return the delimField
	 */
	public String getDelimField() {
		return delimField;
	}
	/**
	 * @param delimField the delimField to set
	 */
	public void setDelimField(String delimField) {
		this.delimField = delimField;
	}
	/**
	 * @return the delimEnclosing
	 */
	public String getDelimEnclosing() {
		return delimEnclosing;
	}
	/**
	 * @param delimEnclosing the delimEnclosing to set
	 */
	public void setDelimEnclosing(String delimEnclosing) {
		this.delimEnclosing = delimEnclosing;
	}
	/**
	 * @return the distinguishEmptyFromNull
	 */
	public Boolean getDistinguishEmptyFromNull() {
		return distinguishEmptyFromNull;
	}
	/**
	 * @param distinguishEmptyFromNull the distinguishEmptyFromNull to set
	 */
	public void setDistinguishEmptyFromNull(Boolean distinguishEmptyFromNull) {
		this.distinguishEmptyFromNull = distinguishEmptyFromNull;
	}
	/**
	 * @return the keys
	 */
	public List<CsvimKeyDefinition> getKeys() {
		return keys;
	}
	/**
	 * @param keys the keys to set
	 */
	public void setKeys(List<CsvimKeyDefinition> keys) {
		this.keys = keys;
	}
	public Map<String, List<String>> getKeysAsMap() {
		Map<String, List<String>> map = new HashMap<>();
		if (keys == null) {
			return map;
		}
		for (CsvimKeyDefinition key : keys) {
			map.put(key.getColumn(), key.getValues());
		}
		return null;
	}

}
