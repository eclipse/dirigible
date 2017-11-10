/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.publisher.definition;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "DIRIGIBLE_PUBLISH_REQUESTS")
public class PublishRequestDefinition {

	@Id
	@GeneratedValue
	@Column(name = "PUBREQ_ID", columnDefinition = "BIGINT", nullable = false)
	private long id;

	@Column(name = "PUBREQ_WORKSPACE", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String workspace;

	@Column(name = "PUBREQ_PATH", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String path;

	@Column(name = "PUBREQ_REGISTRY", columnDefinition = "VARCHAR", nullable = true, length = 255)
	private String registry; // null means default to IRepositoryStructure.REGISTRY_PUBLIC

	@Column(name = "PUBREQ_CREATED_BY", columnDefinition = "VARCHAR", nullable = false, length = 32)
	private String createdBy;

	@Column(name = "PUBREQ_CREATED_AT", columnDefinition = "TIMESTAMP", nullable = false)
	private Timestamp createdAt;

	public long getId() {
		return id;
	}

	public String getWorkspace() {
		return workspace;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getRegistry() {
		return registry;
	}

	public void setRegistry(String registry) {
		this.registry = registry;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedAt() {
		if (createdAt == null) {
			return null;
		}
		return new Timestamp(createdAt.getTime());
	}

	public void setCreatedAt(Timestamp createdAt) {
		if (createdAt == null) {
			this.createdAt = null;
			return;
		}
		this.createdAt = new Timestamp(createdAt.getTime());
	}

}
