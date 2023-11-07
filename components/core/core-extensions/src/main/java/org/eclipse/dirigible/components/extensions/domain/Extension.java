/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.extensions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.dirigible.components.base.artefact.Artefact;

import com.google.gson.annotations.Expose;

/**
 * The Class Extension.
 */
@Entity
@Table(name = "DIRIGIBLE_EXTENSIONS")
public class Extension extends Artefact {

    /** The Constant ARTEFACT_TYPE. */
    public static final String ARTEFACT_TYPE = "extension";

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EXTENSION_ID", nullable = false)
    private Long id;

    /** The extension point. */
    @Column(name = "EXTENSION_EXTENSIONPOINT_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255, unique = false)
    @Expose
    private String extensionPoint;

    /** The module. */
    @Column(name = "EXTENSION_MODULE", columnDefinition = "VARCHAR", nullable = false, length = 255)
    @Expose
    private String module;


    /**
     * Instantiates a new extension.
     *
     * @param location the location
     * @param name the name
     * @param description the description
     * @param extensionPoint the extension point
     * @param module the module
     */
    public Extension(String location, String name, String description, String extensionPoint, String module) {
        super(location, name, ARTEFACT_TYPE, description, null);
        this.extensionPoint = extensionPoint;
        this.module = module;
    }

    /**
     * Instantiates a new extension.
     */
    public Extension() {
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
     * Gets the extension point.
     *
     * @return the extensionPoint
     */
    public String getExtensionPoint() {
        return extensionPoint;
    }

    /**
     * Sets the extension point.
     *
     * @param extensionPoint the extensionPoint to set
     */
    public void setExtensionPoint(String extensionPoint) {
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
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "Extension [id=" + id + ", extensionPoint=" + extensionPoint + ", module=" + module + ", location=" + location + ", name="
                + name + ", description=" + description + ", type=" + type + ", key=" + key + ", dependencies=" + dependencies
                + ", createdBy=" + createdBy + ", createdAt=" + createdAt + ", updatedBy=" + updatedBy + ", updatedAt=" + updatedAt + "]";
    }

}
