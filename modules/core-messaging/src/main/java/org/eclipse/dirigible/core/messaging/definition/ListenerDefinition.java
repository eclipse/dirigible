/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.messaging.definition;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;

@Table(name = "DIRIGIBLE_LISTENERS")
public class ListenerDefinition {

	@Id
	@Column(name = "LISTENER_LOCATION", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String location;

	@Column(name = "LISTENER_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255, unique = true)
	private String name;

	@Column(name = "LISTENER_TYPE", columnDefinition = "TINYINT", nullable = false)
	private byte type;

	@Column(name = "LISTENER_MODULE", columnDefinition = "VARCHAR", nullable = false, length = 255)
	private String module;

	@Column(name = "LISTENER_DESCRIPTION", columnDefinition = "VARCHAR", nullable = true, length = 1024)
	private String description;

	@Column(name = "LISTENER_CREATED_BY", columnDefinition = "VARCHAR", nullable = false, length = 32)
	private String createdBy;

	@Column(name = "LISTENER_CREATED_AT", columnDefinition = "TIMESTAMP", nullable = false)
	private Timestamp createdAt;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public static ListenerDefinition fromJson(String json) {
		return GsonHelper.GSON.fromJson(json, ListenerDefinition.class);
	}

	public String toJson() {
		return GsonHelper.GSON.toJson(this, ListenerDefinition.class);
	}

	@Override
	public String toString() {
		return toJson();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((description == null) ? 0 : description.hashCode());
		result = (prime * result) + ((location == null) ? 0 : location.hashCode());
		result = (prime * result) + ((module == null) ? 0 : module.hashCode());
		result = (prime * result) + ((name == null) ? 0 : name.hashCode());
		result = (prime * result) + type;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ListenerDefinition other = (ListenerDefinition) obj;
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (location == null) {
			if (other.location != null) {
				return false;
			}
		} else if (!location.equals(other.location)) {
			return false;
		}
		if (module == null) {
			if (other.module != null) {
				return false;
			}
		} else if (!module.equals(other.module)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

}
