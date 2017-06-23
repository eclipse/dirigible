package org.eclipse.dirigible.core.security;

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
	
	@Column(name="ACCESS_METHOD", columnDefinition="VARCHAR", nullable=false, length=20)
	private String method = METHOD_ANY;
	
	@Column(name="ACCESS_ROLE", columnDefinition="VARCHAR", nullable=false, length=64)
	private String role;
	
	@Column(name="ACCESS_DESCRIPTION", columnDefinition="VARCHAR", nullable=false, length=1024)
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
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}


}
