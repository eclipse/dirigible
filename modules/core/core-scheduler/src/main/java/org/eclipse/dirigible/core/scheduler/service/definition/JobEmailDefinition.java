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
package org.eclipse.dirigible.core.scheduler.service.definition;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The JobParameterDefinition serialization object.
 */
@Table(name = "DIRIGIBLE_JOB_EMAILS")
public class JobEmailDefinition {

	/** The id. */
	@Id
	@GeneratedValue
	@Column(name = "JOBEMAIL_ID", columnDefinition = "BIGINT", nullable = false)
	private long id;
	
	/** The job name. */
	@Column(name = "JOBEMAIL_JOB_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private transient String jobName;

	/** The name. */
	@Column(name = "JOBEMAIL_EMAIL", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String email;

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
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the job name.
	 *
	 * @return the jobName
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * Sets the job name.
	 *
	 * @param jobName the jobName to set
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 *
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}


	/**
	 * Equals.
	 *
	 * @param object the object
	 * @return true, if successful
	 */
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;
		if (!super.equals(object)) return false;
		JobEmailDefinition that = (JobEmailDefinition) object;
		return id == that.id && jobName.equals(that.jobName) && email.equals(that.email);
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	public int hashCode() {
		return java.util.Objects.hash(super.hashCode(), id, jobName, email);
	}
}
