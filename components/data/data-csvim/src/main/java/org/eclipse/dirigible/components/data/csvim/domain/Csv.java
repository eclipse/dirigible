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
package org.eclipse.dirigible.components.data.csvim.domain;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.components.base.artefact.Artefact;

import com.google.gson.annotations.Expose;

/**
 * The Csv Entity.
 */
@Entity
@Table(name = "DIRIGIBLE_CSV")
public class Csv extends Artefact {

    /** The Constant ARTEFACT_TYPE. */
    public static final String ARTEFACT_TYPE = "csv";

    /**
     * The id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CSV_ID", nullable = false)
    private Long id;

    /**
     * The imported.
     */
    @Column(name = "CSV_IMPORTED", columnDefinition = "BOOLEAN", nullable = false)
    private boolean imported;

    /**
     * The content.
     */
    @Column(name = "CSV_CONTENT", columnDefinition = "CLOB")
    @Expose
    private byte[] content;

    /**
     * Instantiates a new csv.
     *
     * @param location the location
     * @param name the name
     * @param type the type
     * @param description the description
     * @param dependencies the dependencies
     * @param id the id
     * @param imported the imported
     * @param content the content
     */
    public Csv(String location, String name, String type, String description, Set<String> dependencies, Long id, boolean imported,
            byte[] content) {
        super(location, name, type, description, dependencies);
        this.id = id;
        this.imported = imported;
        this.content = content;
    }

    /**
     * Instantiates a new csv.
     *
     * @param id the id
     * @param imported the imported
     * @param content the content
     */
    public Csv(Long id, boolean imported, byte[] content) {
        this.id = id;
        this.imported = imported;
        this.content = content;
    }

    /**
     * Instantiates a new csv.
     */
    public Csv() {

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
     * Getter for imported flag.
     *
     * @return whether is imported already
     */
    public boolean getImported() {
        return imported;
    }

    /**
     * Setter for imported flag.
     *
     * @param imported the flag
     */
    public void setImported(boolean imported) {
        this.imported = imported;
    }

    /**
     * Getter for content.
     *
     * @return the content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Setter for content.
     *
     * @param content the content
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * Gets the created by.
     *
     * @return the created by
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Creates ExtensionPointDefinition from JSON.
     *
     * @param json the JSON
     * @return the extension point definition
     */
    public static Csv fromJson(String json) {
        return GsonHelper.fromJson(json, Csv.class);
    }

    /**
     * Converts ExtensionPointDefinition to JSON.
     *
     * @return the JSON
     */
    public String toJson() {
        return GsonHelper.toJson(this, Csv.class);
    }

    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "Csv{" + "id=" + id + ", imported=" + imported + ", content=" + Arrays.toString(content) + ", location='" + location + '\''
                + ", name='" + name + '\'' + ", type='" + type + '\'' + ", description='" + description + '\'' + ", key='" + key + '\''
                + ", dependencies='" + dependencies + '\'' + ", createdBy=" + createdBy + ", createdAt=" + createdAt + ", updatedBy="
                + updatedBy + ", updatedAt=" + updatedAt + '}';
    }

    /**
     * Equals.
     *
     * @param o object
     * @return true/false if the content is matching
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Csv csv = (Csv) o;

        if (imported != csv.imported)
            return false;
        if (!Objects.equals(id, csv.id))
            return false;
        return Arrays.equals(content, csv.content);
    }

    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, getLocation());
    }
}
