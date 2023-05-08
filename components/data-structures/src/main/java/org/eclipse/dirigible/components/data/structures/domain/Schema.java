/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.data.structures.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.google.gson.annotations.Expose;

/**
 * The Class Schema.
 */
@Entity
@javax.persistence.Table(name = "DIRIGIBLE_DATA_SCHEMAS")
public class Schema extends Artefact {
	
	/** The Constant ARTEFACT_TYPE. */
	public static final String ARTEFACT_TYPE = "schema";
	
	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SCHEMA_ID", nullable = false)
	private Long id;
	
	/** The tables. */
	@OneToMany(mappedBy = "schema", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@Expose
	private List<Table> tables = new ArrayList<Table>();
	
	/** The views. */
	@OneToMany(mappedBy = "schema", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@Expose
	private List<View> views = new ArrayList<View>();

	/**
	 * Instantiates a new table.
	 *
	 * @param location the location
	 * @param name the name
	 * @param description the description
	 * @param dependencies the dependencies
	 */
	public Schema(String location, String name, String description, String dependencies) {
		super(location, name, ARTEFACT_TYPE, description, dependencies);
	}
	
	/**
	 * Instantiates a new schema.
	 *
	 * @param schemaName the schema name
	 */
	public Schema(String schemaName) {
		this(schemaName, schemaName, null, null);
	}
	
	/**
	 * Instantiates a new schema.
	 */
	public Schema() {
		super();
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the tables.
	 *
	 * @return the tables
	 */
	public List<Table> getTables() {
		return tables;
	}

	/**
	 * Sets the tables.
	 *
	 * @param tables the tables to set
	 */
	public void setTables(List<Table> tables) {
		this.tables = tables;
	}

	/**
	 * Gets the views.
	 *
	 * @return the views
	 */
	public List<View> getViews() {
		return views;
	}

	/**
	 * Sets the views.
	 *
	 * @param views the views to set
	 */
	public void setViews(List<View> views) {
		this.views = views;
	}
	
	/**
	 * Find table.
	 *
	 * @param name the name
	 * @return the table
	 */
	public Table findTable(String name) {
		for (Table t : getTables()) {
			if (t.getName().equals(name)) {
				return t;
			}
		}
		return null;
	}
	
	/**
	 * Find view.
	 *
	 * @param name the name
	 * @return the view
	 */
	public View findView(String name) {
		for (View v : getViews()) {
			if (v.getName().equals(name)) {
				return v;
			}
		}
		return null;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "Schema [id=" + id + ", tables=" + tables + ", views=" + views + ", location=" + location + ", name="
				+ name + ", type=" + type + ", description=" + description + ", key=" + key + ", dependencies="
				+ dependencies + ", createdBy=" + createdBy + ", createdAt=" + createdAt + ", updatedBy=" + updatedBy
				+ ", updatedAt=" + updatedAt + "]";
	}
	
}
