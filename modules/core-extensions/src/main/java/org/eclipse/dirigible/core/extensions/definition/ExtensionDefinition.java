package org.eclipse.dirigible.core.extensions.definition;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;

@Table(name="DIRIGIBLE_EXTENSIONS")
public class ExtensionDefinition {
	
	@Id
	@Column(name="EXTENSION_LOCATION", columnDefinition="VARCHAR", nullable=false, length=255)
	private String location;
	
	@Column(name="EXTENSION_EXTENSIONPOINT_NAME", columnDefinition="VARCHAR", nullable=false, length=255)
	private String extensionPoint;
	
	@Column(name="EXTENSION_MODULE", columnDefinition="VARCHAR", nullable=false, length=255)
	private String module;
	
	@Column(name="EXTENSION_DESCRIPTION", columnDefinition="VARCHAR", nullable=true, length=1024)
	private String description;
	
	@Column(name="EXTENSION_CREATED_BY", columnDefinition="VARCHAR", nullable=false, length=32)
	private String createdBy;
	
	@Column(name="EXTENSION_CREATED_AT", columnDefinition="TIMESTAMP", nullable=false)
	private Timestamp createdAt;
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getExtensionPoint() {
		return extensionPoint;
	}

	public void setExtensionPoint(String extensionPoint) {
		this.extensionPoint = extensionPoint;
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
		return new Timestamp(createdAt.getTime());
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
	
	public static ExtensionDefinition fromJson(String json) {
		return GsonHelper.GSON.fromJson(json, ExtensionDefinition.class);
	}
	
	public String toJson() {
		return GsonHelper.GSON.toJson(this, ExtensionDefinition.class);
	}
	
	@Override
	public String toString() {
		return toJson();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((extensionPoint == null) ? 0 : extensionPoint.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((module == null) ? 0 : module.hashCode());
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
		ExtensionDefinition other = (ExtensionDefinition) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (extensionPoint == null) {
			if (other.extensionPoint != null)
				return false;
		} else if (!extensionPoint.equals(other.extensionPoint))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (module == null) {
			if (other.module != null)
				return false;
		} else if (!module.equals(other.module))
			return false;
		return true;
	}

}
