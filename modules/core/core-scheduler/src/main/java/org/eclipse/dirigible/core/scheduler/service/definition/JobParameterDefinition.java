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

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The JobParameterDefinition serialization object.
 */
@Table(name = "DIRIGIBLE_JOB_PARAMETERS")
public class JobParameterDefinition {

	/** The id. */
	@Id
	@Column(name = "JOBPARAM_ID", columnDefinition = "VARCHAR", nullable = false, length = 512)
	private transient String id;
	
	/** The job name. */
	@Column(name = "JOBPARAM_JOB_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private transient String jobName;

	/** The name. */
	@Column(name = "JOBPARAM_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String name;
	
	/** The description. */
	@Column(name = "JOBLOG_DESCRIPTION", columnDefinition = "VARCHAR", nullable = true, length = 2000)
	private String description;

	/** The type. */
	@Column(name = "JOBPARAM_TYPE", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String type; // string, number, boolean
	
	/** The default value. */
	@Column(name = "JOBPARAM_DEFAULT_VALUE", columnDefinition = "VARCHAR", nullable = true, length = 2000)
	private String defaultValue = "";
	
	/** The choices. */
	@Column(name = "JOBPARAM_CHOICES", columnDefinition = "VARCHAR", nullable = true, length = 2000)
	private String choices = "";
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param jobName the jobName to set
	 * @param name the name to set
	 *
	 */
	public void setId(String jobName, String name) {
		this.id = "\"" + jobName + "\":\"" + name + "\"";
	}
	
	/**
	 * @return the job name
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * @param jobName the job name to set
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the choices
	 */
	public String getChoices() {
		return choices;
	}

	/**
	 * @param choices the choices to set
	 */
	public void setChoices(String choices) {
		this.choices = choices;
	}

	@Override
	public int hashCode() {
		return Objects.hash(choices, defaultValue, description, id, jobName, name, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobParameterDefinition other = (JobParameterDefinition) obj;
		return Objects.equals(choices, other.choices) && Objects.equals(defaultValue, other.defaultValue)
				&& Objects.equals(description, other.description) && id == other.id
				&& Objects.equals(jobName, other.jobName) && Objects.equals(name, other.name)
				&& Objects.equals(type, other.type);
	}
	
	

}
