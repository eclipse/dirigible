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
package org.eclipse.dirigible.components.base.artefact;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.DatatypeConverter;

/**
 * The Class Artefact.
 */
@MappedSuperclass
public abstract class Artefact extends Auditable<String> implements Serializable {
	
	/** The Constant KEY_SEPARATOR. */
	private static final String KEY_SEPARATOR = ":";

	/** The location. */
	@Column(name = "ARTEFACT_LOCATION", columnDefinition = "VARCHAR", nullable = false, length = 255)
	protected String location;
	
	/** The name. */
	@Column(name = "ARTEFACT_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255)
	protected String name;
	
	/** The key. */
	@Column(name = "ARTEFACT_TYPE", columnDefinition = "VARCHAR", nullable = false, length = 255)
	protected String type;
	
	/** The description. */
	@Column(name = "ARTEFACT_DESCRIPTION", columnDefinition = "VARCHAR", nullable = true, length = 2000)
	protected String description;
	
	/** The key
	 * e.g. table:/sales/domain/customer.table:customer
	 */
	@Column(name = "ARTEFACT_KEY", columnDefinition = "VARCHAR", nullable = false, length = 255, unique = true)
	protected String key;
	
	/** The dependencies as comma separated keys. */
	@Column(name = "ARTEFACT_DEPENDENCIES", columnDefinition = "VARCHAR", nullable = true, length = 20000)
	protected String dependencies;
	
	
	/**
	 * Instantiates a new artefact.
	 *
	 * @param location the location
	 * @param name the name
	 * @param type the type
	 * @param description the description
	 * @param dependencies the dependencies
	 */
	public Artefact(String location, String name, String type, String description, String dependencies) {
		super();
		this.location = location;
		this.name = name;
		this.type = type;
		this.description = description;
		this.dependencies = dependencies;
		updateKey();
	}
	
	/**
	 * Instantiates a new artefact.
	 */
	public Artefact() {
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
		updateKey();
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
	 * Sets the name.
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
		updateKey();
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
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
	 * Gets the key.
	 *
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key.
	 *
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
	/**
	 * Gets the dependencies.
	 *
	 * @return the dependencies
	 */
	public String getDependencies() {
		return dependencies;
	}

	/**
	 * Sets the dependencies.
	 *
	 * @param dependencies the dependencies to set
	 */
	public void setDependencies(String dependencies) {
		this.dependencies = dependencies;
	}
	
	/**
	 * Update key.
	 *
	 */
	public void updateKey() {
		if (this.type != null
				&& this.location != null 
				&& this.name != null) {
			this.key =  this.type + KEY_SEPARATOR + this.location + KEY_SEPARATOR + this.name;
		}
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "Artefact [location=" + location + ", name=" + name + ", description=" + description + ", type=" + type
				+ ", key=" + key + ", dependencies=" + dependencies
				+ ", createdBy=" + createdBy + ", createdAt=" + createdAt + ", updatedBy=" + updatedBy + ", updatedAt="
				+ updatedAt + "]";
	}
	
}
