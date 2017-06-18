package org.eclipse.dirigible.core.security;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="DIRIGIBLE_SECURITY_ROLES")
public class RoleDefinition {
	
	@Id
	@Column(name="ROLE_NAME", columnDefinition="VARCHAR", nullable=false, length=64)
	private String name;
	
	@Column(name="ROLE_DESCRIPTION", columnDefinition="VARCHAR", nullable=false, length=1024)
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
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}


}
