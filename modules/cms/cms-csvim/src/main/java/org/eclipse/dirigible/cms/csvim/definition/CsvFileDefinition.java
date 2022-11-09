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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class CsvFileDefinition.
 */
public class CsvFileDefinition {
	
	/** The table. */
	private String table;
	
	/** The schema. */
	private String schema;
	
	/** The file. */
	private String file;
	
	/** The header. */
	private Boolean header = Boolean.FALSE;
	
	/** The use header names. */
	private Boolean useHeaderNames = Boolean.FALSE;
	
	/** The delim field. */
	private String delimField;
	
	/** The delim enclosing. */
	private String delimEnclosing;

	private String sequence;

	/** The distinguish empty from null. */
	private Boolean distinguishEmptyFromNull = Boolean.TRUE;
	
	/** The keys. */
	private List<CsvimKeyDefinition> keys;
	
	/**
	 * Gets the table.
	 *
	 * @return the table
	 */
	public String getTable() {
		return table;
	}
	
	/**
	 * Sets the table.
	 *
	 * @param table the table to set
	 */
	public void setTable(String table) {
		this.table = table;
	}
	
	/**
	 * Gets the schema.
	 *
	 * @return the schema
	 */
	public String getSchema() {
		return schema;
	}
	
	/**
	 * Sets the schema.
	 *
	 * @param schema the schema to set
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public String getFile() {
		return file;
	}
	
	/**
	 * Sets the file.
	 *
	 * @param file the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}
	
	/**
	 * Gets the header.
	 *
	 * @return the header
	 */
	public Boolean getHeader() {
		return header;
	}
	
	/**
	 * Sets the header.
	 *
	 * @param header the header to set
	 */
	public void setHeader(Boolean header) {
		this.header = header;
	}
	
	/**
	 * Gets the use header names.
	 *
	 * @return the useHeaderNames
	 */
	public Boolean getUseHeaderNames() {
		return useHeaderNames;
	}
	
	/**
	 * Sets the use header names.
	 *
	 * @param useHeaderNames the useHeaderNames to set
	 */
	public void setUseHeaderNames(Boolean useHeaderNames) {
		this.useHeaderNames = useHeaderNames;
	}
	
	/**
	 * Gets the delim field.
	 *
	 * @return the delimField
	 */
	public String getDelimField() {
		return delimField;
	}
	
	/**
	 * Sets the delim field.
	 *
	 * @param delimField the delimField to set
	 */
	public void setDelimField(String delimField) {
		this.delimField = delimField;
	}
	
	/**
	 * Gets the delim enclosing.
	 *
	 * @return the delimEnclosing
	 */
	public String getDelimEnclosing() {
		return delimEnclosing;
	}
	
	/**
	 * Sets the delim enclosing.
	 *
	 * @param delimEnclosing the delimEnclosing to set
	 */
	public void setDelimEnclosing(String delimEnclosing) {
		this.delimEnclosing = delimEnclosing;
	}

	/**
	 * Gets the sequence.
	 *
	 * @return
	 */
	public String getSequence() {
		return sequence;
	}

	/**
	 * Sets the sequence.
	 *
	 * @param sequence the sequence to set
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	/**
	 * Gets the distinguish empty from null.
	 *
	 * @return the distinguishEmptyFromNull
	 */
	public Boolean getDistinguishEmptyFromNull() {
		return distinguishEmptyFromNull;
	}
	
	/**
	 * Sets the distinguish empty from null.
	 *
	 * @param distinguishEmptyFromNull the distinguishEmptyFromNull to set
	 */
	public void setDistinguishEmptyFromNull(Boolean distinguishEmptyFromNull) {
		this.distinguishEmptyFromNull = distinguishEmptyFromNull;
	}
	
	/**
	 * Gets the keys.
	 *
	 * @return the keys
	 */
	public List<CsvimKeyDefinition> getKeys() {
		return keys;
	}
	
	/**
	 * Sets the keys.
	 *
	 * @param keys the keys to set
	 */
	public void setKeys(List<CsvimKeyDefinition> keys) {
		this.keys = keys;
	}
	
	/**
	 * Gets the keys as map.
	 *
	 * @return the keys as map
	 */
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
