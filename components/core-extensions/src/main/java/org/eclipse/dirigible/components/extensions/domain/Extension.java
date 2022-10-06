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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.eclipse.dirigible.components.base.Auditable;

/**
 * The Class Extension.
 */
@Entity
@Table(name = "DIRIGIBLE_EXTENSIONS")
public class Extension extends Auditable<String> implements Serializable {
	
	/** The location. */
	@Id
	@Column(name = "EXTENSION_LOCATION", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String location;

	/** The extension point. */
	@ManyToOne
    @JoinColumn(name = "EXTENSION_EXTENSIONPOINT_NAME")
	private ExtensionPoint extensionPoint;

	/** The module. */
	@Column(name = "EXTENSION_MODULE", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String module;

	/** The description. */
	@Column(name = "EXTENSION_DESCRIPTION", columnDefinition = "VARCHAR", nullable = true, length = 1024)
	private String description;
	
	/**
	 * Instantiates a new extension.
	 */
	public Extension() {
		super();
	}

	/**
	 * Instantiates a new extension.
	 *
	 * @param location the location
	 * @param extensionPoint the extension point
	 * @param module the module
	 * @param description the description
	 */
	public Extension(String location, ExtensionPoint extensionPoint, String module, String description) {
		super();
		this.location = location;
		this.extensionPoint = extensionPoint;
		this.module = module;
		this.description = description;
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
	 * Gets the extension point.
	 *
	 * @return the extensionPoint
	 */
	public ExtensionPoint getExtensionPoint() {
		return extensionPoint;
	}

	/**
	 * Sets the extension point.
	 *
	 * @param extensionPoint the extensionPoint to set
	 */
	public void setExtensionPoint(ExtensionPoint extensionPoint) {
		this.extensionPoint = extensionPoint;
	}

	/**
	 * Gets the module.
	 *
	 * @return the module
	 */
	public String getModule() {
		return module;
	}

	/**
	 * Sets the module.
	 *
	 * @param module the module to set
	 */
	public void setModule(String module) {
		this.module = module;
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

}
