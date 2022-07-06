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

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.eclipse.dirigible.commons.api.artefacts.IArtefactDefinition;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;

/**
 * The CsvimDefinition Entity.
 */
@Table(name = "DIRIGIBLE_CSV")
public class CsvDefinition implements IArtefactDefinition {

	@Id
	@Column(name = "CSV_LOCATION", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String location;
	
	@Column(name = "CSV_HASH", columnDefinition = "VARCHAR", nullable = false, length = 32)
	private String hash;
	
	@Column(name = "CSV_IMPORTED", columnDefinition = "BOOLEAN", nullable = false)
	private boolean imported;

	@Transient
	private String content;

	@Column(name = "CSV_CREATED_BY", columnDefinition = "VARCHAR", nullable = false, length = 128)
	private String createdBy;

	@Column(name = "CSV_CREATED_AT", columnDefinition = "TIMESTAMP", nullable = false)
	private Timestamp createdAt;
	
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
	 * @param location
	 *            the new location
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the hash
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * @param hash the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	/**
	 * Getter for imported flag
	 * 
	 * @return whether is imported already
	 */
	public boolean getImported() {
		return imported;
	}
	
	/**
	 * Setter for imported flag
	 * 
	 * @param imported the flag
	 */
	public void setImported(boolean imported) {
		this.imported = imported;
	}

	/**
	 * Getter for content
	 *
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Setter for content
	 *
	 * @param content the content
	 */
	public void setContent(String content) {
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
	 * Sets the created by.
	 *
	 * @param createdBy
	 *            the new created by
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Gets the created at.
	 *
	 * @return the created at
	 */
	public Timestamp getCreatedAt() {
		if (createdAt == null) {
			return null;
		}
		return new Timestamp(createdAt.getTime());
	}

	/**
	 * Sets the created at.
	 *
	 * @param createdAt
	 *            the new created at
	 */
	public void setCreatedAt(Timestamp createdAt) {
		if (createdAt == null) {
			this.createdAt = null;
			return;
		}
		this.createdAt = new Timestamp(createdAt.getTime());
	}

	/**
	 * Creates ExtensionPointDefinition from JSON.
	 *
	 * @param json
	 *            the JSON
	 * @return the extension point definition
	 */
	public static CsvDefinition fromJson(String json) {
		return GsonHelper.GSON.fromJson(json, CsvDefinition.class);
	}

	/**
	 * Converts ExtensionPointDefinition to JSON.
	 *
	 * @return the JSON
	 */
	public String toJson() {
		return GsonHelper.GSON.toJson(this, CsvDefinition.class);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toJson();
	}

	@Override
	public String getArtefactName() {
		return getLocation();
	}

	@Override
	public String getArtefactLocation() {
		return getLocation();
	}

	@Override
	public int hashCode() {
		return Objects.hash(hash, location);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CsvDefinition other = (CsvDefinition) obj;
		return Objects.equals(hash, other.hash) && Objects.equals(location, other.location);
	}
	
	

}
