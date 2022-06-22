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

import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The JobLogDefinition serialization object.
 */
@Table(name = "DIRIGIBLE_JOB_LOGS")
public class JobLogDefinition {
	
	public static final short JOB_LOG_STATUS_TRIGGRED = 0;
	
	public static final short JOB_LOG_STATUS_FINISHED = 1;
	
	public static final short JOB_LOG_STATUS_FAILED = -1;
	
	public static final short JOB_LOG_STATUS_LOGGED = 2;
	
	public static final short JOB_LOG_STATUS_ERROR = 3;
	
	public static final short JOB_LOG_STATUS_WARN = 4;
	
	public static final short JOB_LOG_STATUS_INFO = 5;

	/** The id. */
	@Id
	@GeneratedValue
	@Column(name = "JOBLOG_ID", columnDefinition = "BIGINT", nullable = false)
	private long id;

	/** The name. */
	@Column(name = "JOBLOG_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String name;

	/** The handler. */
	@Column(name = "JOBLOG_HANDLER", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String handler;
	
	/** The started at. */
	@Column(name = "JOBLOG_TRIGGERED_AT", columnDefinition = "TIMESTAMP", nullable = true)
	private Timestamp triggeredAt;
	
	@Column(name = "JOBLOG_TRIGGERED_ID", columnDefinition = "BIGINT", nullable = true)
	private long triggeredId;
	
	/** The finished at. */
	@Column(name = "JOBLOG_FINISHED_AT", columnDefinition = "TIMESTAMP", nullable = true)
	private Timestamp finishedAt;

	/** The status */
	@Column(name = "JOBLOG_STATUS", columnDefinition = "SMALLINT", nullable = false)
	private short status;

	/** The message. */
	@Column(name = "JOBLOG_MESSAGE", columnDefinition = "VARCHAR", nullable = true, length = 2000)
	private String message;
	
	
	
	
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
	 * @return the triggeredAt
	 */
	public Timestamp getTriggeredAt() {
		if (triggeredAt == null) {
			return null;
		}
		return new Timestamp(triggeredAt.getTime());
	}

	/**
	 * @param triggeredAt the triggeredAt to set
	 */
	public void setTriggeredAt(Timestamp triggeredAt) {
		if (triggeredAt == null) {
			this.triggeredAt = null;
			return;
		}
		this.triggeredAt = new Timestamp(triggeredAt.getTime());
	}
	
	/**
	 * @return the triggeredId
	 */
	public long getTriggeredId() {
		return triggeredId;
	}

	/**
	 * @param triggeredId the triggeredId to set
	 */
	public void setTriggeredId(long triggeredId) {
		this.triggeredId = triggeredId;
	}


	/**
	 * @return the finishedAt
	 */
	public Timestamp getFinishedAt() {
		if (finishedAt == null) {
			return null;
		}
		return new Timestamp(finishedAt.getTime());
	}

	/**
	 * @param finishedAt the finishedAt to set
	 */
	public void setFinishedAt(Timestamp finishedAt) {
		if (finishedAt == null) {
			this.finishedAt = null;
			return;
		}
		this.finishedAt = new Timestamp(finishedAt.getTime());
	}

	/**
	 * @return the status
	 */
	public short getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(short status) {
		this.status = status;
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

	@Override
	public int hashCode() {
		return Objects.hash(finishedAt, handler, id, message, name, status, triggeredAt, triggeredId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobLogDefinition other = (JobLogDefinition) obj;
		return Objects.equals(finishedAt, other.finishedAt) && Objects.equals(handler, other.handler) && id == other.id
				&& Objects.equals(message, other.message) && Objects.equals(name, other.name) && status == other.status
				&& Objects.equals(triggeredAt, other.triggeredAt) && triggeredId == other.triggeredId;
	}
	
	

}
