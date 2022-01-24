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
package org.eclipse.dirigible.core.messaging.definition;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.dirigible.commons.api.artefacts.IArtefactDefinition;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;

/**
 * The ListenerDefinition Entity.
 */
@Table(name = "DIRIGIBLE_LISTENERS")
public class ListenerDefinition implements IArtefactDefinition {

	@Id
	@Column(name = "LISTENER_LOCATION", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String location;

	@Column(name = "LISTENER_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255, unique = true)
	private String name;

	@Column(name = "LISTENER_TYPE", columnDefinition = "CHAR", nullable = false, length = 1)
	private char type;

	@Column(name = "LISTENER_HANDLER", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String handler;

	@Column(name = "LISTENER_DESCRIPTION", columnDefinition = "VARCHAR", nullable = true, length = 1024)
	private String description;

	@Column(name = "LISTENER_CREATED_BY", columnDefinition = "VARCHAR", nullable = false, length = 128)
	private String createdBy;

	@Column(name = "LISTENER_CREATED_AT", columnDefinition = "TIMESTAMP", nullable = false)
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
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public char getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type
	 *            the new type
	 */
	public void setType(char type) {
		this.type = type;
	}

	/**
	 * Gets the handler.
	 *
	 * @return the handler
	 */
	public String getHandler() {
		return handler;
	}

	/**
	 * Sets the handler.
	 *
	 * @param handler
	 *            the new handler
	 */
	public void setHandler(String handler) {
		this.handler = handler;
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
	 * @param description
	 *            the new description
	 */
	public void setDescription(String description) {
		this.description = description;
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
	 * Creates ListenerDefinition from JSON.
	 *
	 * @param json
	 *            the JSON
	 * @return the listener definition
	 */
	public static ListenerDefinition fromJson(String json) {
		return GsonHelper.GSON.fromJson(json, ListenerDefinition.class);
	}

	/**
	 * Converts ListenerDefinition to JSON.
	 *
	 * @return the JSON
	 */
	public String toJson() {
		return GsonHelper.GSON.toJson(this, ListenerDefinition.class);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toJson();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((description == null) ? 0 : description.hashCode());
		result = (prime * result) + ((location == null) ? 0 : location.hashCode());
		result = (prime * result) + ((handler == null) ? 0 : handler.hashCode());
		result = (prime * result) + ((name == null) ? 0 : name.hashCode());
		result = (prime * result) + type;
		return result;
	}

	/*
	 * (non-Javadoc)
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
		ListenerDefinition other = (ListenerDefinition) obj;
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (location == null) {
			if (other.location != null) {
				return false;
			}
		} else if (!location.equals(other.location)) {
			return false;
		}
		if (handler == null) {
			if (other.handler != null) {
				return false;
			}
		} else if (!handler.equals(other.handler)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

	@Override
	public String getArtefactName() {
		return getName();
	}

	@Override
	public String getArtefactLocation() {
		return getLocation();
	}

}
