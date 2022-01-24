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

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.dirigible.commons.api.artefacts.IArtefactDefinition;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;

/**
 * The Job Definition transfer object.
 */
@Table(name = "DIRIGIBLE_JOBS")
public class JobDefinition implements IArtefactDefinition {

	@Id
	@Column(name = "JOB_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String name;

	@Column(name = "JOB_GROUP", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String group;

	@Column(name = "JOB_CLASS", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String clazz = "";

	@Column(name = "JOB_DESCRIPTION", columnDefinition = "VARCHAR", nullable = false, length = 1024)
	private String description;

	@Column(name = "JOB_EXPRESSION", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String expression;

	@Column(name = "JOB_HANDLER", columnDefinition = "VARCHAR", nullable = true, length = 255)
	private String handler;

	@Column(name = "JOB_ENGINE", columnDefinition = "VARCHAR", nullable = true, length = 100)
	private String engine;

	@Column(name = "JOB_SINGLETON", columnDefinition = "BOOLEAN", nullable = false)
	private boolean singleton = false;

	@Column(name = "JOB_ENABLED", columnDefinition = "BOOLEAN", nullable = false)
	private boolean enabled = true;

	@Column(name = "JOB_CREATED_BY", columnDefinition = "VARCHAR", nullable = false, length = 128)
	private String createdBy;

	@Column(name = "JOB_CREATED_AT", columnDefinition = "TIMESTAMP", nullable = false)
	private Timestamp createdAt;

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
	 * Gets the handler
	 *
	 * @return the handler
	 */
	public String getHandler() {
		return handler;
	}

	/**
	 * Sets the handler
	 *
	 * @param handler
	 *            the handler
	 */
	public void setHandler(String handler) {
		this.handler = handler;
	}

	/**
	 * Gets the engine type
	 *
	 * @return the engine type
	 */
	public String getEngine() {
		return engine;
	}

	/**
	 * Sets the engine type
	 *
	 * @param engine
	 *            the engine type
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toJson();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result + ((engine == null) ? 0 : engine.hashCode());
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + ((handler == null) ? 0 : handler.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (singleton ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobDefinition other = (JobDefinition) obj;
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (!clazz.equals(other.clazz))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (enabled != other.enabled)
			return false;
		if (engine == null) {
			if (other.engine != null)
				return false;
		} else if (!engine.equals(other.engine))
			return false;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		if (handler == null) {
			if (other.handler != null)
				return false;
		} else if (!handler.equals(other.handler))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (singleton != other.singleton)
			return false;
		return true;
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
