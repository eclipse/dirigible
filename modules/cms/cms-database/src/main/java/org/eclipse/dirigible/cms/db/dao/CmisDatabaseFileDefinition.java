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
package org.eclipse.dirigible.cms.db.dao;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Database File Definition
 */
@Table(name = "DIRIGIBLE_CMS_FILES")
public class CmisDatabaseFileDefinition {

	static transient final byte OBJECT_TYPE_FOLDER = 0;
	static transient final byte OBJECT_TYPE_TEXT = 1;
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public long getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(long modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

}
