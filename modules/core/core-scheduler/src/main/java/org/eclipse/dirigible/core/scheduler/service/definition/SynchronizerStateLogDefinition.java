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
 * The Synchronizer State Log Definition transfer object.
 */
@Table(name = "DIRIGIBLE_SYNCHRONIZER_STATE_LOG")
public class SynchronizerStateLogDefinition {

	@Id
	@GeneratedValue
	@Column(name = "SYNCHRONIZER_LOG_ID", columnDefinition = "BIGINT", nullable = false, length = 255)
	private long id;
	
	@Column(name = "SYNCHRONIZER_LOG_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String name;

	@Column(name = "SYNCHRONIZER_LOG_STATE", columnDefinition = "INTEGER", nullable = false)
	private int state;

	@Column(name = "SYNCHRONIZER_LOG_MESSAGE", columnDefinition = "VARCHAR", nullable = false, length = 2000)
	private String message = "";

	@Column(name = "SYNCHRONIZER_LOG_TIMESTAMP", columnDefinition = "BIGINT", nullable = false)
	private long timestamp;
	
	/**
	 * Empty constructor
	 */
	public SynchronizerStateLogDefinition() {
		super();
	}
	
	/**
	 * Fields constructor
	 * 
	 * @param name the name of the synchronizer
	 * @param state the current state
	 * @param message the message if any
	 * @param timestamp the event timestamp
	 */
	public SynchronizerStateLogDefinition(String name, int state, String message, long timestamp) {
		super();
		this.name = name;
		this.state = state;
		this.message = message;
		this.timestamp = timestamp;
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
	 * @return the state
	 */
	public int getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(int state) {
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
