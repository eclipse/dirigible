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
package org.eclipse.dirigible.database.databases.definition;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.dirigible.commons.api.artefacts.IArtefactDefinition;

/**
 * Database Definition transfer object
 *
 */
@Table(name = "DIRIGIBLE_DATABASES")
public class DatabaseDefinition implements IArtefactDefinition {
	
	/** The id. */
	@Id
	@GeneratedValue
	@Column(name = "JOBLOG_ID", columnDefinition = "BIGINT", nullable = false)
	private long id;
	
	/** The name */
	@Column(name = "DATABASE_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String name;
	
	/** The driver */
	@Column(name = "DATABASE_DRIVER", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String driver;
	
	/** The url */
	@Column(name = "DATABASE_URL", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String url;
	
	/** The username */
	@Column(name = "DATABASE_USERNAME", columnDefinition = "VARCHAR", nullable = true, length = 255)
	private String username;
	
	/** The password */
	@Column(name = "DATABASE_PASSWORD", columnDefinition = "VARCHAR", nullable = true, length = 255)
	private String password;
	
	/** The parameters */
	@Column(name = "DATABASE_PARAMETERS", columnDefinition = "VARCHAR", nullable = true, length = 2000)
	private String parameters;
	
	
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
	 * @return the driver
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * @param driver the driver to set
	 */
	public void setDriver(String driver) {
		this.driver = driver;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the parameters
	 */
	public String getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(driver, id, name, parameters, password, url, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DatabaseDefinition other = (DatabaseDefinition) obj;
		return Objects.equals(driver, other.driver) && id == other.id && Objects.equals(name, other.name)
				&& Objects.equals(parameters, other.parameters) && Objects.equals(password, other.password)
				&& Objects.equals(url, other.url) && Objects.equals(username, other.username);
	}

	@Override
	public String getArtefactName() {
		return getName();
	}

	@Override
	public String getArtefactLocation() {
		return getName();
	}
	
	

}
