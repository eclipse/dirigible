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
package org.eclipse.dirigible.components.data.structures.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@javax.persistence.Table(name = "DIRIGIBLE_TABLE_COLUMNS")
public class TableColumn {

	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "COLUMN_ID", nullable = false)
	private Long id;
	
	/** The name. */
	@Column(name = "COLUMN_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String name;
	
	/** The type. */
	@Column(name = "COLUMN_TYPE", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String type;
	
	/** The length. */
	@Column(name = "COLUMN_LENGTH", columnDefinition = "VARCHAR", nullable = true, length = 255)
	private String length;
	
	/** The nullable. */
	@Column(name = "COLUMN_NULLABLE", columnDefinition = "BOOLEAN", nullable = true)
	private boolean nullable;
	
	/** The primary key. */
	@Column(name = "COLUMN_PRIMARY_KEY", columnDefinition = "BOOLEAN", nullable = true)
	private boolean primaryKey;
	
	/** The default value. */
	@Column(name = "COLUMN_DEFAULT_VALUE", columnDefinition = "VARCHAR", nullable = true, length = 255)
	private String defaultValue;
	
	/** The scale. */
	@Column(name = "COLUMN_SCALE", columnDefinition = "VARCHAR", nullable = true, length = 255)
	private String scale;
	
	/** The unique. */
	@Column(name = "COLUMN_UNIQUE", columnDefinition = "BOOLEAN", nullable = true)
	private boolean unique;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "TABLE_ID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Table table;

	TableColumn(String name, String type, String length, boolean nullable, boolean primaryKey,
			String defaultValue, String scale, boolean unique, Table table) {
		super();
		this.name = name;
		this.type = type;
		this.length = length;
		this.nullable = nullable;
		this.primaryKey = primaryKey;
		this.defaultValue = defaultValue;
		this.scale = scale;
		this.unique = unique;
		this.table = table;
	}
	
	public TableColumn() {
		super();
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the length
	 */
	public String getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(String length) {
		this.length = length;
	}

	/**
	 * @return the nullable
	 */
	public boolean isNullable() {
		return nullable;
	}

	/**
	 * @param nullable the nullable to set
	 */
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	/**
	 * @return the primaryKey
	 */
	public boolean isPrimaryKey() {
		return primaryKey;
	}

	/**
	 * @param primaryKey the primaryKey to set
	 */
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the scale
	 */
	public String getScale() {
		return scale;
	}

	/**
	 * @param scale the scale to set
	 */
	public void setScale(String scale) {
		this.scale = scale;
	}

	/**
	 * @return the unique
	 */
	public boolean isUnique() {
		return unique;
	}

	/**
	 * @param unique the unique to set
	 */
	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	/**
	 * @return the table
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * @param table the table to set
	 */
	public void setTable(Table table) {
		this.table = table;
	}

	@Override
	public String toString() {
		return "TableColumn [id=" + id + ", name=" + name + ", type=" + type + ", length=" + length + ", nullable="
				+ nullable + ", primaryKey=" + primaryKey + ", defaultValue=" + defaultValue + ", scale=" + scale
				+ ", unique=" + unique  + ", table=" + table.getName() + "]";
	}
	
	

}
