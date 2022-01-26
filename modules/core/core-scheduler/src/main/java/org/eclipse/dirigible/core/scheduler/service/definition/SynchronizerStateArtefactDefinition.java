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
package org.eclipse.dirigible.core.scheduler.service.definition;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Synchronizer State Artefact Definition transfer object.
 */
@Table(name = "DIRIGIBLE_SYNCHRONIZER_STATE_ARTEFACTS")
public class SynchronizerStateArtefactDefinition {
	
	@Id
	@GeneratedValue
	@Column(name = "SYNCHRONIZER_ARTEFACT_ID", columnDefinition = "BIGINT", nullable = false, length = 255)
	private long id;
	
	@Column(name = "SYNCHRONIZER_ARTEFACT_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String name;
	
	@Column(name = "SYNCHRONIZER_ARTEFACT_LOCATION", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String location;
	
	@Column(name = "SYNCHRONIZER_ARTEFACT_TYPE", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String type;

	@Column(name = "SYNCHRONIZER_ARTEFACT_STATE", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String state;

	@Column(name = "SYNCHRONIZER_ARTEFACT_MESSAGE", columnDefinition = "VARCHAR", nullable = false, length = 2000)
	private String message = "";

	@Column(name = "SYNCHRONIZER_ARTEFACT_TIMESTAMP", columnDefinition = "BIGINT", nullable = false)
	private long timestamp;

	
	public SynchronizerStateArtefactDefinition() {
		super();
	}

	/**
	 * Create Synchronizer state artefact by parameters
	 * 
	 * @param name the name of the artefact
	 * @param location the file location of the artefact
	 * @param type the type of the artefact
	 * @param state the state of the artefact
	 * @param message the last status message
	 */
	public SynchronizerStateArtefactDefinition(String name, String location, String type, String state, String message) {
		super();
		this.name = name;
		this.location = location;
		this.type = type;
		this.state = state;
		this.message = message;
		this.timestamp = System.currentTimeMillis();
	}

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
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	

}
