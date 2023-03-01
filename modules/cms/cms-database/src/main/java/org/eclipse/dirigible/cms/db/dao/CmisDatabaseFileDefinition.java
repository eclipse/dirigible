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
package org.eclipse.dirigible.cms.db.dao;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Database File Definition.
 */
@Table(name = "DIRIGIBLE_CMS_FILES")
public class CmisDatabaseFileDefinition {

	/** The Constant OBJECT_TYPE_FOLDER. */
	static transient final byte OBJECT_TYPE_FOLDER = 0;
	
	/** The Constant OBJECT_TYPE_TEXT. */
	static transient final byte OBJECT_TYPE_TEXT = 1;
	
	/** The Constant OBJECT_TYPE_BINARY. */
	static transient final byte OBJECT_TYPE_BINARY = 2;

	/** The path. */
	@Id
	@Column(name = "FILE_PATH", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String path;

	/** The name. */
	@Column(name = "FILE_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String name;

	/** The type. */
	@Column(name = "FILE_TYPE", columnDefinition = "TINYINT", nullable = false)
	private byte type;

	/** The content type. */
	@Column(name = "FILE_CONTENT_TYPE", columnDefinition = "VARCHAR", nullable = true, length = 128)
	private String contentType;

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
	 * Gets the type.
	 *
	 * @return the type
	 */
	public byte getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(byte type) {
		this.type = type;
	}

	/**
	 * Gets the content type.
	 *
	 * @return the content type
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Sets the content type.
	 *
	 * @param contentType the new content type
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
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
