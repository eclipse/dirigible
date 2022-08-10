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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.eclipse.dirigible.commons.api.artefacts.IArtefactDefinition;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;

/**
 * The Job Definition transfer object.
 */
@Table(name = "DIRIGIBLE_JOBS")
public class JobDefinition implements IArtefactDefinition {

	/** The name. */
	@Id
	@Column(name = "JOB_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String name;

	/** The group. */
	@Column(name = "JOB_GROUP", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String group;

	/** The clazz. */
	@Column(name = "JOB_CLASS", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String clazz = "";

	/** The description. */
	@Column(name = "JOB_DESCRIPTION", columnDefinition = "VARCHAR", nullable = false, length = 1024)
	private String description;

	/** The expression. */
	@Column(name = "JOB_EXPRESSION", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String expression;

	/** The handler. */
	@Column(name = "JOB_HANDLER", columnDefinition = "VARCHAR", nullable = true, length = 255)
	private String handler;

	/** The engine. */
	@Column(name = "JOB_ENGINE", columnDefinition = "VARCHAR", nullable = true, length = 100)
	private String engine;

	/** The singleton. */
	@Column(name = "JOB_SINGLETON", columnDefinition = "BOOLEAN", nullable = false)
	private boolean singleton = false;

	/** The enabled. */
	@Column(name = "JOB_ENABLED", columnDefinition = "BOOLEAN", nullable = false)
	private boolean enabled = true;

	/** The created by. */
	@Column(name = "JOB_CREATED_BY", columnDefinition = "VARCHAR", nullable = false, length = 128)
	private String createdBy;

	/** The created at. */
	@Column(name = "JOB_CREATED_AT", columnDefinition = "TIMESTAMP", nullable = false)
	private Timestamp createdAt;
	
	/** The parameters. */
	@Transient
	private List<JobParameterDefinition> parameters = new ArrayList<JobParameterDefinition>();
	
	/** The status. */
	@Column(name = "JOB_STATUS", columnDefinition = "SMALLINT", nullable = true)
	private short status = 99;
	
	/** The message. */
	@Column(name = "JOBLOG_MESSAGE", columnDefinition = "VARCHAR", nullable = true, length = 2000)
	private String message;
	
	/** The executed at. */
	@Column(name = "JOB_EXECUTED_AT", columnDefinition = "TIMESTAMP", nullable = true)
	private Timestamp executedAt;

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
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the group.
	 *
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * Sets the group.
	 *
	 * @param group
	 *            the new group
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * Gets the clazz.
	 *
	 * @return the clazz
	 */
	public String getClazz() {
		return clazz;
	}

	/**
	 * Sets the clazz.
	 *
	 * @param clazz
	 *            the new clazz
	 */
	public void setClazz(String clazz) {
		this.clazz = clazz;
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
	 * @param description
	 *            the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the expression.
	 *
	 * @return the expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * Sets the expression.
	 *
	 * @param expression
	 *            the new expression
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}

	/**
	 * Gets the handler.
	 *
	 * @return the handler
	 */
	public String getHandler() {
		return handler;
	}

	/**
	 * Sets the handler.
	 *
	 * @param handler            the handler
	 */
	public void setHandler(String handler) {
		this.handler = handler;
	}

	/**
	 * Gets the engine type.
	 *
	 * @return the engine type
	 */
	public String getEngine() {
		return engine;
	}

	/**
	 * Sets the engine type.
	 *
	 * @param engine            the engine type
	 */
	public void setEngine(String engine) {
		this.engine = engine;
	}

	/**
	 * Checks if is singleton.
	 *
	 * @return true, if is singleton
	 */
	public boolean isSingleton() {
		return singleton;
	}

	/**
	 * Sets the singleton.
	 *
	 * @param singleton
	 *            the new singleton
	 */
	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

	/**
	 * Checks if is enabled.
	 *
	 * @return true, if is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets the enabled.
	 *
	 * @param enabled
	 *            the new enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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
	 * @param createdBy
	 *            the new created by
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Gets the created at.
	 *
	 * @return the created at
	 */
	public Timestamp getCreatedAt() {
		if (createdAt == null) {
			return null;
		}
		return new Timestamp(createdAt.getTime());
	}

	/**
	 * Sets the created at.
	 *
	 * @param createdAt
	 *            the new created at
	 */
	public void setCreatedAt(Timestamp createdAt) {
		if (createdAt == null) {
			this.createdAt = null;
			return;
		}
		this.createdAt = new Timestamp(createdAt.getTime());
	}

	/**
	 * From json.
	 *
	 * @param json
	 *            the json
	 * @return the job definition
	 */
	public static JobDefinition fromJson(String json) {
		return GsonHelper.GSON.fromJson(json, JobDefinition.class);
	}

	/**
	 * To json.
	 *
	 * @return the string
	 */
	public String toJson() {
		return GsonHelper.GSON.toJson(this, JobDefinition.class);
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toJson();
	}

	/**
	 * Gets the artefact name.
	 *
	 * @return the artefact name
	 */
	@Override
	public String getArtefactName() {
		return getName();
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		return Objects.hash(clazz, description, enabled, engine, expression, group, handler, name, parameters,
				singleton);
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
		JobDefinition other = (JobDefinition) obj;
		return Objects.equals(clazz, other.clazz) && Objects.equals(description, other.description)
				&& enabled == other.enabled && Objects.equals(engine, other.engine)
				&& Objects.equals(expression, other.expression) && Objects.equals(group, other.group)
				&& Objects.equals(handler, other.handler) && Objects.equals(name, other.name)
				&& Objects.equals(parameters, other.parameters) && singleton == other.singleton;
	}

	/**
	 * Gets the artefact location.
	 *
	 * @return the artefact location
	 */
	@Override
	public String getArtefactLocation() {
		return getName();
	}
	
	
	/**
	 * Adds the parameter.
	 *
	 * @param name the name
	 * @param type the type
	 * @param defaultValue the default value
	 * @param choices the choices
	 * @param description the description
	 */
	public void addParameter(String name, String type, String defaultValue, String choices, String description) {
		JobParameterDefinition parameter = new JobParameterDefinition();
		parameter.setId(this.name, name);
		parameter.setJobName(this.name);
		parameter.setName(name);
		parameter.setType(type);
		parameter.setDefaultValue(defaultValue);
		parameter.setChoices(choices);
		parameter.setDescription(description);
		removeParameter(name);
		parameters.add(parameter);
	}
	
	/**
	 * Removes the parameter.
	 *
	 * @param name the name
	 */
	public void removeParameter(String name) {
		for (JobParameterDefinition p : parameters) {
			if (p.getName().equals(name)) {
				parameters.remove(p);
				break;
			}
		}
	}
	
	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	public Collection<JobParameterDefinition> getParameters() {
		return Collections.unmodifiableCollection(parameters);
	}
	
	/**
	 * Gets the latest status.
	 *
	 * @return the latest status
	 */
	public short getStatus() {
		return status;
	}
	
	/**
	 * Sets the latest status.
	 *
	 * @param status            the latest status
	 */
	public void setStatus(short status) {
		this.status = status;
	}
	
	/**
	 * Gets the latest message.
	 *
	 * @return the latest message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Sets the latest message.
	 *
	 * @param message            the latest message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Gets the executed at.
	 *
	 * @return the executed at
	 */
	public Timestamp getExecutedAt() {
		if (executedAt == null) {
			return null;
		}
		return new Timestamp(executedAt.getTime());
	}

	/**
	 * Sets the executed at.
	 *
	 * @param executedAt
	 *            the new executed at
	 */
	public void setExecutedAt(Timestamp executedAt) {
		if (executedAt == null) {
			this.executedAt = null;
			return;
		}
		this.executedAt = new Timestamp(executedAt.getTime());
	}

}
