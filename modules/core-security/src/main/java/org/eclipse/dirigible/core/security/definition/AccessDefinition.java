package org.eclipse.dirigible.core.security.definition;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="DIRIGIBLE_SECURITY_ACCESS")
public class AccessDefinition {
	
	public static final transient String METHOD_ANY = "*";

	@Id
	@GeneratedValue
	@Column(name="ACCESS_ID", columnDefinition="BIGINT", nullable=false)
	private long id;
	
	@Column(name="ACCESS_LOCATION", columnDefinition="VARCHAR", nullable=false, length=255)
	private String location;
	
	@Column(name="ACCESS_URI", columnDefinition="VARCHAR", nullable=false, length=255)
	private String uri;
	
	@Column(name="ACCESS_METHOD", columnDefinition="VARCHAR", nullable=false, length=20)
	private String method = METHOD_ANY;
	
	@Column(name="ACCESS_ROLE", columnDefinition="VARCHAR", nullable=false, length=64)
	private String role;
	
	@Column(name="ACCESS_DESCRIPTION", columnDefinition="VARCHAR", nullable=true, length=1024)
	private String description;
	
	@Column(name="ACCESS_CREATED_BY", columnDefinition="VARCHAR", nullable=false, length=32)
	private String createdBy;
	
	@Column(name="ACCESS_CREATED_AT", columnDefinition="TIMESTAMP", nullable=false)
	private Timestamp createdAt;
	
	public long getId() {
		return id;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public String getMethod() {
		return method;
	}
	
	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
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
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		AccessDefinition other = (AccessDefinition) obj;
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
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

}
