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
package org.eclipse.dirigible.engine.web.models;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.eclipse.dirigible.commons.api.artefacts.IArtefactDefinition;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;

/**
 * The web model.
 */
@Table(name = "DIRIGIBLE_WEB")
public class WebModel implements IArtefactDefinition {

	/** The location. */
	@Id
	@Column(name = "WEB_LOCATION", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String location;

	/** The guid. */
	@Column(name = "WEB_GUID", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String guid;
	
	/** The exposed. */
	@Column(name = "WEB_EXPOSED", columnDefinition = "VARCHAR", nullable = true, length = 2000)
	private String exposed;

	/** The hash. */
	@Column(name = "WEB_HASH", columnDefinition = "VARCHAR", nullable = false, length = 32)
	private String hash;

	/** The created by. */
	@Column(name = "WEB_CREATED_BY", columnDefinition = "VARCHAR", nullable = false, length = 128)
	private String createdBy;

	/** The created at. */
	@Column(name = "WEB_CREATED_AT", columnDefinition = "TIMESTAMP", nullable = false)
	private Timestamp createdAt;

	/** The exposes. */
	@Transient
	private String[] exposes;

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
	 * @param guid
	 *            the new guid
	 */
	public void setGuid(String guid) {
		this.guid = guid;
	}
	
	/**
	 * Gets the exposed.
	 *
	 * @return the exposed
	 */
	public String getExposed() {
		if (exposed == null
				&& exposes != null) {
			exposed = String.join(",", exposes);
		}
		return exposed;
	}

	/**
	 * Sets the exposed.
	 *
	 * @param exposed
	 *            the new exposed
	 */
	public void setExposed(String exposed) {
		this.exposed = exposed;
		if (this.exposes == null && this.exposed != null) {
			this.exposes = this.exposed.split(",");
		}
	}

	/**
	 * Gets the hash.
	 *
	 * @return the hash
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * Sets the hash.
	 *
	 * @param hash
	 *            the new hash
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/**
	 * Gets the exposes list.
	 *
	 * @return the exposes list
	 */
	public String[] getExposes() {
		if (exposes == null
				&& exposed != null) {
			exposes = exposed.split(",");
		}
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
	 * To json.
	 *
	 * @return the string
	 */
	public String toJson() {
		return GsonHelper.GSON.toJson(this);
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toJson();
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((hash == null) ? 0 : hash.hashCode());
		result = (prime * result) + ((location == null) ? 0 : location.hashCode());
		result = (prime * result) + ((guid == null) ? 0 : guid.hashCode());
		return result;
	}

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		WebModel other = (WebModel) obj;
		if (hash == null) {
			if (other.hash != null) {
				return false;
			}
		} else if (!hash.equals(other.hash)) {
			return false;
		}
		if (location == null) {
			if (other.location != null) {
				return false;
			}
		} else if (!location.equals(other.location)) {
			return false;
		}
		if (guid == null) {
			if (other.guid != null) {
				return false;
			}
		} else if (!guid.equals(other.guid)) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the artefact name.
	 *
	 * @return the artefact name
	 */
	@Override
	public String getArtefactName() {
		return getGuid();
	}

	/**
	 * Gets the artefact location.
	 *
	 * @return the artefact location
	 */
	@Override
	public String getArtefactLocation() {
		return getLocation();
	}

}
