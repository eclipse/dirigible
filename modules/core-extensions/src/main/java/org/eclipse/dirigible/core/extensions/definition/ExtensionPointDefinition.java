package org.eclipse.dirigible.core.extensions.definition;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;

@Table(name="DIRIGIBLE_EXTENSION_POINTS")
public class ExtensionPointDefinition {
	
	@Id
	@Column(name="EXTENSIONPOINT_LOCATION", columnDefinition="VARCHAR", nullable=false, length=255)
	private String location;
	
	@Column(name="EXTENSIONPOINT_DESCRIPTION", columnDefinition="VARCHAR", nullable=false, length=1024)
	private String description;
	
	@Column(name="EXTENSIONPOINT_CREATED_BY", columnDefinition="VARCHAR", nullable=false, length=32)
	private String createdBy;
	
	@Column(name="EXTENSIONPOINT_CREATED_AT", columnDefinition="TIMESTAMP", nullable=false)
	private Timestamp createdAt;
	
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
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public static ExtensionPointDefinition fromJson(String json) {
		return GsonHelper.GSON.fromJson(json, ExtensionPointDefinition.class);
	}
	
	public String toJson() {
		return GsonHelper.GSON.toJson(this, ExtensionPointDefinition.class);
	}
	
	@Override
	public String toString() {
		return toJson();
	}

}
