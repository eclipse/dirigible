package org.eclipse.dirigible.core.security.definition;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="DIRIGIBLE_SECURITY_ROLES")
public class RoleDefinition {
	
	@Id
	@Column(name="ROLE_NAME", columnDefinition="VARCHAR", nullable=false, length=64)
	private String name;
	
	@Column(name="ROLE_LOCATION", columnDefinition="VARCHAR", nullable=false, length=255)
	private String location;
	
	@Column(name="ROLE_DESCRIPTION", columnDefinition="VARCHAR", nullable=true, length=1024)
	private String description;
	
	@Column(name="ROLE_CREATED_BY", columnDefinition="VARCHAR", nullable=false, length=32)
	private String createdBy;
	
	@Column(name="ROLE_CREATED_AT", columnDefinition="TIMESTAMP", nullable=false)
	private Timestamp createdAt;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
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
		return new Timestamp(createdAt.getTime());
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = new Timestamp(createdAt.getTime());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		RoleDefinition other = (RoleDefinition) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
