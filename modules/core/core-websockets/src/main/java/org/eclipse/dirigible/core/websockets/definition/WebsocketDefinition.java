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
package org.eclipse.dirigible.core.websockets.definition;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.dirigible.commons.api.artefacts.IArtefactDefinition;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;

/**
 * The WebsocketDefinition Entity.
 */
@Table(name = "DIRIGIBLE_WEBSOCKETS")
public class WebsocketDefinition implements IArtefactDefinition {

	/** The location. */
	@Id
	@Column(name = "WEBSOCKET_LOCATION", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String location;

	/** The endpoint. */
	@Column(name = "WEBSOCKET_ENDPOINT_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255, unique = true)
	private String endpoint;

	/** The handler. */
	@Column(name = "WEBSOCKET_HANDLER", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String handler;
	
	/** The engine. */
	@Column(name = "WEBSOCKET_ENGINE", columnDefinition = "VARCHAR", nullable = true, length = 255)
	private String engine;

	/** The description. */
	@Column(name = "WEBSOCKET_DESCRIPTION", columnDefinition = "VARCHAR", nullable = true, length = 1024)
	private String description;

	/** The created by. */
	@Column(name = "WEBSOCKET_CREATED_BY", columnDefinition = "VARCHAR", nullable = false, length = 128)
	private String createdBy;

	/** The created at. */
	@Column(name = "WEBSOCKET_CREATED_AT", columnDefinition = "TIMESTAMP", nullable = false)
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
	 * Gets the endpoint.
	 *
	 * @return the endpoint
	 */
	public String getEndpoint() {
		return endpoint;
	}

	/**
	 * Sets the endpoint.
	 *
	 * @param endpoint
	 *            the new endpoint
	 */
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
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
	 * Gets the engine.
	 *
	 * @return the engine
	 */
	public String getEngine() {
		return engine;
	}

	/**
	 * Sets the engine.
	 *
	 * @param engine
	 *            the new engine
	 */
	public void setEngine(String engine) {
		this.engine = engine;
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
	 * Creates ExtensionDefinition from JSON.
	 *
	 * @param json
	 *            the JSON
	 * @return the extension definition
	 */
	public static WebsocketDefinition fromJson(String json) {
		return GsonHelper.fromJson(json, WebsocketDefinition.class);
	}

	/**
	 * Converts ExtensionDefinition to JSON.
	 *
	 * @return the JSON
	 */
	public String toJson() {
		return GsonHelper.toJson(this, WebsocketDefinition.class);
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	/*
	 * (non-Javadoc)
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
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((description == null) ? 0 : description.hashCode());
		result = (prime * result) + ((endpoint == null) ? 0 : endpoint.hashCode());
		result = (prime * result) + ((location == null) ? 0 : location.hashCode());
		result = (prime * result) + ((handler == null) ? 0 : handler.hashCode());
		result = (prime * result) + ((engine == null) ? 0 : engine.hashCode());
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
		WebsocketDefinition other = (WebsocketDefinition) obj;
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (endpoint == null) {
			if (other.endpoint != null) {
				return false;
			}
		} else if (!endpoint.equals(other.endpoint)) {
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
		if (engine == null) {
			if (other.engine != null) {
				return false;
			}
		} else if (!engine.equals(other.engine)) {
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
		return getEndpoint();
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
