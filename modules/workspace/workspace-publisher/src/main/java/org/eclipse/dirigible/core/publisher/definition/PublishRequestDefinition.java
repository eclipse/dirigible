/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.publisher.definition;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The PublishRequestDefinition serialization.
 */
@Table(name = "DIRIGIBLE_PUBLISH_REQUESTS")
public class PublishRequestDefinition {
	
	/** The Constant COMMAND_PUBLISH. */
	public transient static final String COMMAND_PUBLISH = "P";
	
	/** The Constant COMMAND_UNPUBLISH. */
	public transient static final String COMMAND_UNPUBLISH = "U";

	/** The id. */
	@Id
	@GeneratedValue
	@Column(name = "PUBREQ_ID", columnDefinition = "BIGINT", nullable = false)
	private long id;

	/** The workspace. */
	@Column(name = "PUBREQ_WORKSPACE", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String workspace;

	/** The path. */
	@Column(name = "PUBREQ_PATH", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String path;

	/** The registry. */
	@Column(name = "PUBREQ_REGISTRY", columnDefinition = "VARCHAR", nullable = true, length = 255)
	private String registry; // null means default to IRepositoryStructure.REGISTRY_PUBLIC

	/** The created by. */
	@Column(name = "PUBREQ_CREATED_BY", columnDefinition = "VARCHAR", nullable = false, length = 128)
	private String createdBy;

	/** The created at. */
	@Column(name = "PUBREQ_CREATED_AT", columnDefinition = "TIMESTAMP", nullable = false)
	private Timestamp createdAt;
	
	/** The command. */
	@Column(name = "PUBREQ_COMMAND", columnDefinition = "CHAR", nullable = true, length = 1)
	private String command = COMMAND_PUBLISH;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Gets the workspace.
	 *
	 * @return the workspace
	 */
	public String getWorkspace() {
		return workspace;
	}

	/**
	 * Sets the workspace.
	 *
	 * @param workspace
	 *            the new workspace
	 */
	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets the path.
	 *
	 * @param path
	 *            the new path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Gets the registry.
	 *
	 * @return the registry
	 */
	public String getRegistry() {
		return registry;
	}

	/**
	 * Sets the registry.
	 *
	 * @param registry
	 *            the new registry
	 */
	public void setRegistry(String registry) {
		this.registry = registry;
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
	 * Gets the command.
	 *
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Sets the command.
	 *
	 * @param command
	 *            the new command
	 */
	public void setCommand(String command) {
		this.command = command;
	}

}
