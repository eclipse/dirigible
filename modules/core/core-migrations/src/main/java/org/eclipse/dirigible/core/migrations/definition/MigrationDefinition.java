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
package org.eclipse.dirigible.core.migrations.definition;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.dirigible.commons.api.artefacts.IArtefactDefinition;

/**
 * The Role Definition transfer object.
 */
@Table(name = "DIRIGIBLE_MIGRATIONS")
public class MigrationDefinition implements IArtefactDefinition {

	@Id
	@Column(name = "MIGRATION_LOCATION", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String location;
	
	@Column(name = "MIGRATION_PROJECT", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String project;
	
	@Column(name = "MIGRATION_MAJOR", columnDefinition = "INTEGER", nullable = false)
	private int major;
	
	@Column(name = "MIGRATION_MINOR", columnDefinition = "INTEGER", nullable = false)
	private int minor;
	
	@Column(name = "MIGRATION_MICRO", columnDefinition = "INTEGER", nullable = false)
	private int micro;
	
	@Column(name = "MIGRATION_HANDLER", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String handler;
	
	@Column(name = "MIGRATION_ENGINE", columnDefinition = "VARCHAR", nullable = false, length = 100)
	private String engine;

	@Column(name = "MIGRATION_DESCRIPTION", columnDefinition = "VARCHAR", nullable = true, length = 1024)
	private String description;

	@Column(name = "MIGRATION_CREATED_BY", columnDefinition = "VARCHAR", nullable = false, length = 128)
	private String createdBy;

	@Column(name = "MIGRATION_CREATED_AT", columnDefinition = "TIMESTAMP", nullable = false)
	private Timestamp createdAt;

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
	 * @return the project
	 */
	public String getProject() {
		return project;
	}

	/**
	 * @param project the project to set
	 */
	public void setProject(String project) {
		this.project = project;
	}

	/**
	 * @return the major
	 */
	public int getMajor() {
		return major;
	}

	/**
	 * @param major the major to set
	 */
	public void setMajor(int major) {
		this.major = major;
	}

	/**
	 * @return the minor
	 */
	public int getMinor() {
		return minor;
	}

	/**
	 * @param minor the minor to set
	 */
	public void setMinor(int minor) {
		this.minor = minor;
	}

	/**
	 * @return the micro
	 */
	public int getMicro() {
		return micro;
	}

	/**
	 * @param micro the micro to set
	 */
	public void setMicro(int micro) {
		this.micro = micro;
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
	 * @return the engine
	 */
	public String getEngine() {
		return engine;
	}

	/**
	 * @param engine the engine to set
	 */
	public void setEngine(String engine) {
		this.engine = engine;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdAt
	 */
	public Timestamp getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((engine == null) ? 0 : engine.hashCode());
		result = prime * result + ((handler == null) ? 0 : handler.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + major;
		result = prime * result + micro;
		result = prime * result + minor;
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MigrationDefinition other = (MigrationDefinition) obj;
		if (engine == null) {
			if (other.engine != null)
				return false;
		} else if (!engine.equals(other.engine))
			return false;
		if (handler == null) {
			if (other.handler != null)
				return false;
		} else if (!handler.equals(other.handler))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (major != other.major)
			return false;
		if (micro != other.micro)
			return false;
		if (minor != other.minor)
			return false;
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (!project.equals(other.project))
			return false;
		return true;
	}

	@Override
	public String getArtefactName() {
		return getLocation();
	}

	@Override
	public String getArtefactLocation() {
		return getLocation();
	}

}
