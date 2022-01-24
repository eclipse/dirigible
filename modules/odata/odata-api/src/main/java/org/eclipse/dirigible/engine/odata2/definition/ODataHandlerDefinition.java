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
package org.eclipse.dirigible.engine.odata2.definition;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.dirigible.commons.api.artefacts.IArtefactDefinition;

/**
 * The OData Handler Model
 */
@Table(name = "DIRIGIBLE_ODATA_HANDLER")
public class ODataHandlerDefinition {
	
	@Id
	@GeneratedValue
	@Column(name = "ODATAH_ID", columnDefinition = "BIGINT", nullable = false)
	private long id;
	
	@Column(name = "ODATAH_LOCATION", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String location;
	
	@Column(name = "ODATAH_NAMESPACE", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String namespace;
	
	@Column(name = "ODATAH_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String name;
	
	@Column(name = "ODATAH_METHOD", columnDefinition = "VARCHAR", nullable = false, length = 20)
	private String method;
	
	@Column(name = "ODATAH_TYPE", columnDefinition = "VARCHAR", nullable = false, length = 20)
	private String type;
	
	@Column(name = "ODATAH_HANDLER", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String handler;
	
	@Column(name = "ODATAH_CREATED_BY", columnDefinition = "VARCHAR", nullable = false, length = 128)
	private String createdBy;

	@Column(name = "ODATAH_CREATED_AT", columnDefinition = "TIMESTAMP", nullable = false)
	private Timestamp createdAt;

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace the namespace to set
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the handler
	 */
	public String getHandler() {
		return handler;
	}

	/**
	 * @param handler the handler to set
	 */
	public void setHandler(String handler) {
		this.handler = handler;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdAt
	 */
	public Timestamp getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

}
