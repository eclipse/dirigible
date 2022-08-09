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
@Table(name = "DIRIGIBLE_MIGRATIONS_STATUS")
public class MigrationStatusDefinition {

	/** The project. */
	@Id
	@Column(name = "MIGRATION_STATUS_PROJECT", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String project;
	
	/** The major. */
	@Column(name = "MIGRATION_STATUS_MAJOR", columnDefinition = "INTEGER", nullable = false)
	private int major;
	
	/** The minor. */
	@Column(name = "MIGRATION_STATUS_MINOR", columnDefinition = "INTEGER", nullable = false)
	private int minor;
	
	/** The micro. */
	@Column(name = "MIGRATION_STATUS_MICRO", columnDefinition = "INTEGER", nullable = false)
	private int micro;
	
	/** The location. */
	@Column(name = "MIGRATION_STATUS_LOCATION", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String location;

	/** The created by. */
	@Column(name = "MIGRATION_STATUS_CREATED_BY", columnDefinition = "VARCHAR", nullable = false, length = 128)
	private String createdBy;

	/** The created at. */
	@Column(name = "MIGRATION_STATUS_CREATED_AT", columnDefinition = "TIMESTAMP", nullable = false)
	private Timestamp createdAt;

	/**
	 * Gets the project.
	 *
	 * @return the project
	 */
	public String getProject() {
		return project;
	}

	/**
	 * Sets the project.
	 *
	 * @param project the project to set
	 */
	public void setProject(String project) {
		this.project = project;
	}

	/**
	 * Gets the major.
	 *
	 * @return the major
	 */
	public int getMajor() {
		return major;
	}

	/**
	 * Sets the major.
	 *
	 * @param major the major to set
	 */
	public void setMajor(int major) {
		this.major = major;
	}

	/**
	 * Gets the minor.
	 *
	 * @return the minor
	 */
	public int getMinor() {
		return minor;
	}

	/**
	 * Sets the minor.
	 *
	 * @param minor the minor to set
	 */
	public void setMinor(int minor) {
		this.minor = minor;
	}

	/**
	 * Gets the micro.
	 *
	 * @return the micro
	 */
	public int getMicro() {
		return micro;
	}

	/**
	 * Sets the micro.
	 *
	 * @param micro the micro to set
	 */
	public void setMicro(int micro) {
		this.micro = micro;
	}

	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Sets the location.
	 *
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Gets the created by.
	 *
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * Sets the created by.
	 *
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Gets the created at.
	 *
	 * @return the createdAt
	 */
	public Timestamp getCreatedAt() {
		return createdAt;
	}

	/**
	 * Sets the created at.
	 *
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + major;
		result = prime * result + micro;
		result = prime * result + minor;
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		return result;
	}

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
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
		MigrationStatusDefinition other = (MigrationStatusDefinition) obj;
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

}
