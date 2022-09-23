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
package org.eclipse.dirigible.core.publisher.definition;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The PublishLogDefinition serialization object.
 */
@Table(name = "DIRIGIBLE_PUBLISH_LOGS")
public class PublishLogDefinition {

	/** The id. */
	@Id
	@GeneratedValue
	@Column(name = "PUBLOG_ID", columnDefinition = "BIGINT", nullable = false)
	private long id;

	/** The source. */
	@Column(name = "PUBLOG_SOURCE", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String source;

	/** The target. */
	@Column(name = "PUBLOG_TARGET", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String target;

	/** The created by. */
	@Column(name = "PUBLOG_CREATED_BY", columnDefinition = "VARCHAR", nullable = false, length = 128)
	private String createdBy;

	/** The created at. */
	@Column(name = "PUBLOG_CREATED_AT", columnDefinition = "TIMESTAMP", nullable = false)
	private Timestamp createdAt;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Gets the source.
	 *
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Sets the source.
	 *
	 * @param source
	 *            the new source
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * Gets the target.
	 *
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * Sets the target.
	 *
	 * @param target
	 *            the new target
	 */
	public void setTarget(String target) {
		this.target = target;
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

}
