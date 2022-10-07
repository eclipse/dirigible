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
package org.eclipse.dirigible.components.extensions.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;

import org.eclipse.dirigible.components.base.Artefact;
import org.eclipse.dirigible.components.base.Auditable;

/**
 * The Class ExtensionPoint.
 */
@Entity
@Table(name = "DIRIGIBLE_EXTENSION_POINTS")
public class ExtensionPoint extends Artefact {
	
	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EXTENSIONPOINT_ID", nullable = false)
	private Long id;
	
	/** The location. */
	@Column(name = "EXTENSIONPOINT_LOCATION", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String location;
	
	/** The name. */
	@Column(name = "EXTENSIONPOINT_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255, unique = true)
	private String name;

	/** The description. */
	@Column(name = "EXTENSIONPOINT_DESCRIPTION", columnDefinition = "VARCHAR", nullable = true, length = 1024)
	private String description;

	/** The extensions. */
	@Valid
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "EXTENSIONPOINT_NAME")
	private Set<Extension> extensions;
	
	/**
	 * Instantiates a new extension point.
	 */
	public ExtensionPoint() {
		super();
	}

	/**
	 * Instantiates a new extension point.
	 *
	 * @param location the location
	 * @param name the name
	 * @param description the description
	 */
	public ExtensionPoint(String location, String name, String description) {
		super();
		this.location = location;
		this.name = name;
		this.description = description;
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
	 * Gets the location.
	 *
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Sets the location.
	 *
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the extensions.
	 *
	 * @return the extensions
	 */
	public Set<Extension> getExtensions() {
		return extensions;
	}

	/**
	 * Sets the extensions.
	 *
	 * @param extensions the extensions to set
	 */
	public void setExtensions(Set<Extension> extensions) {
		this.extensions = extensions;
	}
	
}
