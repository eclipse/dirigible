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
package org.eclipse.dirigible.repository.db;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Database File Version Definition.
 */
@Table(name = "DIRIGIBLE_FILES_VERSIONS")
public class DatabaseFileVersionDefinition {

	/** The id. */
	@Id
	@GeneratedValue
	@Column(name = "FILE_ID", columnDefinition = "BIGINT", nullable = false)
	private long id;

	/** The version. */
	@Column(name = "FILE_VERSION", columnDefinition = "INTEGER", nullable = false)
	private int version;

	/** The path. */
	@Column(name = "FILE_PATH", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String path;

	/** The name. */
	@Column(name = "FILE_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String name;

	/** The content. */
	@Column(name = "FILE_CONTENT", columnDefinition = "BLOB", nullable = true)
	private byte[] content;

	/** The created at. */
	@Column(name = "FILE_CREATED_AT", columnDefinition = "BIGINT", nullable = false)
	private long createdAt;

	/** The created by. */
	@Column(name = "FILE_CREATED_BY", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String createdBy;

	/** The modified at. */
	@Column(name = "FILE_MODIFIED_AT", columnDefinition = "BIGINT", nullable = false)
	private long modifiedAt;

	/** The modified by. */
	@Column(name = "FILE_MODIFIED_BY", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String modifiedBy;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Sets the version.
	 *
	 * @param version the new version
	 */
	public void setVersion(int version) {
		this.version = version;
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
	 * @param path the new path
	 */
	public void setPath(String path) {
		this.path = path;
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
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public byte[] getContent() {
		return content != null ? content.clone() : new byte[] {};
	}

	/**
	 * Sets the content.
	 *
	 * @param content the new content
	 */
	public void setContent(byte[] content) {
		this.content = content != null ? content.clone() : null;
	}

	/**
	 * Gets the created at.
	 *
	 * @return the created at
	 */
	public long getCreatedAt() {
		return createdAt;
	}

	/**
	 * Sets the created at.
	 *
	 * @param createdAt the new created at
	 */
	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
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
	 * @param createdBy the new created by
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Gets the modified at.
	 *
	 * @return the modified at
	 */
	public long getModifiedAt() {
		return modifiedAt;
	}

	/**
	 * Sets the modified at.
	 *
	 * @param modifiedAt the new modified at
	 */
	public void setModifiedAt(long modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	/**
	 * Gets the modified by.
	 *
	 * @return the modified by
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * Sets the modified by.
	 *
	 * @param modifiedBy the new modified by
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

}
