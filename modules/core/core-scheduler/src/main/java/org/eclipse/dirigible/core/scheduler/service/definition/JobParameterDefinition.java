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
import javax.persistence.Transient;

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
	
	/** The value. */
	@Transient
	private String value;
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param jobName the jobName to set
	 * @param name the name to set
	 */
	public void setId(String jobName, String name) {
		this.id = "\"" + jobName + "\":\"" + name + "\"";
	}
	
	/**
	 * Gets the job name.
	 *
	 * @return the job name
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * Sets the job name.
	 *
	 * @param jobName the job name to set
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
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
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the default value.
	 *
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Sets the default value.
	 *
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Gets the choices.
	 *
	 * @return the choices
	 */
	public String getChoices() {
		return choices;
	}

	/**
	 * Sets the choices.
	 *
	 * @param choices the choices to set
	 */
	public void setChoices(String choices) {
		this.choices = choices;
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		return Objects.hash(choices, defaultValue, description, id, jobName, name, type);
	}

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
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
	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Sets the value.
	 *
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	

}
