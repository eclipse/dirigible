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
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Synchronizer State Definition transfer object.
 */
@Table(name = "DIRIGIBLE_SYNCHRONIZER_STATE")
public class SynchronizerStateDefinition {

	/** The name. */
	@Id
	@Column(name = "SYNCHRONIZER_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String name;

	/** The state. */
	@Column(name = "SYNCHRONIZER_STATE", columnDefinition = "INTEGER", nullable = false)
	private int state;

	/** The message. */
	@Column(name = "SYNCHRONIZER_MESSAGE", columnDefinition = "VARCHAR", nullable = false, length = 2000)
	private String message = "";

	/** The first time triggered. */
	@Column(name = "SYNCHRONIZER_FIRST_TIME_TRIGGERED", columnDefinition = "BIGINT", nullable = false)
	private long firstTimeTriggered;
	
	/** The first time finished. */
	@Column(name = "SYNCHRONIZER_FIRST_TIME_FINISHED", columnDefinition = "BIGINT", nullable = false)
	private long firstTimeFinished;
	
	/** The last time triggered. */
	@Column(name = "SYNCHRONIZER_LAST_TIME_TRIGGERED", columnDefinition = "BIGINT", nullable = false)
	private long lastTimeTriggered;
	
	/** The last time finished. */
	@Column(name = "SYNCHRONIZER_LAST_TIME_FINISHED", columnDefinition = "BIGINT", nullable = false)
	private long lastTimeFinished;

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
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public int getState() {
		return state;
	}

	/**
	 * Sets the state.
	 *
	 * @param state the state to set
	 */
	public void setState(int state) {
		this.state = state;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message.
	 *
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets the first time triggered.
	 *
	 * @return the firstTimeTriggered
	 */
	public long getFirstTimeTriggered() {
		return firstTimeTriggered;
	}

	/**
	 * Sets the first time triggered.
	 *
	 * @param firstTimeTriggered the firstTimeTriggered to set
	 */
	public void setFirstTimeTriggered(long firstTimeTriggered) {
		this.firstTimeTriggered = firstTimeTriggered;
	}

	/**
	 * Gets the first time finished.
	 *
	 * @return the firstTimeFinished
	 */
	public long getFirstTimeFinished() {
		return firstTimeFinished;
	}

	/**
	 * Sets the first time finished.
	 *
	 * @param firstTimeFinished the firstTimeFinished to set
	 */
	public void setFirstTimeFinished(long firstTimeFinished) {
		this.firstTimeFinished = firstTimeFinished;
	}

	/**
	 * Gets the last time triggered.
	 *
	 * @return the lastTimeTriggered
	 */
	public long getLastTimeTriggered() {
		return lastTimeTriggered;
	}

	/**
	 * Sets the last time triggered.
	 *
	 * @param lastTimeTriggered the lastTimeTriggered to set
	 */
	public void setLastTimeTriggered(long lastTimeTriggered) {
		this.lastTimeTriggered = lastTimeTriggered;
	}

	/**
	 * Gets the last time finished.
	 *
	 * @return the lastTimeFinished
	 */
	public long getLastTimeFinished() {
		return lastTimeFinished;
	}

	/**
	 * Sets the last time finished.
	 *
	 * @param lastTimeFinished the lastTimeFinished to set
	 */
	public void setLastTimeFinished(long lastTimeFinished) {
		this.lastTimeFinished = lastTimeFinished;
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SynchronizerStateDefinition other = (SynchronizerStateDefinition) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}


}
