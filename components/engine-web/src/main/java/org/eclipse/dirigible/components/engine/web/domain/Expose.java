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
package org.eclipse.dirigible.components.engine.web.domain;

import java.util.Arrays;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.springframework.data.annotation.Transient;

/**
 * The Class Expose.
 */
@Entity
@Table(name = "DIRIGIBLE_WEB_EXPOSE")
public class Expose extends Artefact {
	
	/** The Constant ARTEFACT_TYPE. */
	public static final String ARTEFACT_TYPE = "expose";
	
	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EXPOSE_ID", nullable = false)
	private Long id;
	
	/** The guid. */
	@Column(name = "EXPOSE_GUID", columnDefinition = "VARCHAR", nullable = true, length = 255)
	private String guid;
	
	/** The exposes. */
	@Column(name = "EXPOSE_EXPOSES", columnDefinition = "VARCHAR", nullable = true, length = 2000)
	@Nullable
	@ElementCollection
	@OrderColumn
	private String[] exposes = new String[] {};
	
	/**
	 * Instantiates a new web.
	 *
	 * @param location the location
	 * @param name the name
	 * @param description the description
	 * @param exposes the exposes
	 */
	public Expose(String location, String name, String description, String[] exposes) {
		super(location, name, ARTEFACT_TYPE, description, null);
		this.exposes = exposes;
	}	
	
	/**
	 * Instantiates a new web.
	 */
	public Expose() {
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
	 * Gets the guid.
	 *
	 * @return the guid
	 */
	public String getGuid() {
		return guid;
	}

	/**
	 * Sets the guid.
	 *
	 * @param guid the guid to set
	 */
	public void setGuid(String guid) {
		this.guid = guid;
	}
	
	/**
	 * Gets the exposes list.
	 *
	 * @return the exposes list
	 */
	public String[] getExposes() {
		return exposes;
	}
	
	/**
	 * Sets the exposes list.
	 * 
	 * @param exposes the exposes list
	 */
	public void setExposes(String[] exposes) {
		this.exposes = exposes;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "Web [id=" + id + ", exposes=" + Arrays.toString(exposes) + ", location=" + location
				+ ", name=" + name + ", type=" + type + ", description=" + description + ", key=" + key
				+ ", dependencies=" + dependencies + ", createdBy=" + createdBy + ", createdAt=" + createdAt
				+ ", updatedBy=" + updatedBy + ", updatedAt=" + updatedAt + "]";
	}

}
