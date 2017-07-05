package org.eclipse.dirigible.core.scheduler.service.definition;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;

@Table(name="DIRIGIBLE_JOBS")
public class JobDefinition {
	
	@Id
	@Column(name="JOB_NAME", columnDefinition="VARCHAR", nullable=false, length=255)
	private String name;
	
	@Column(name="JOB_GROUP", columnDefinition="VARCHAR", nullable=false, length=255)
	private String group;
	
	@Column(name="JOB_CLASS", columnDefinition="VARCHAR", nullable=false, length=255)
	private String clazz;
	
	@Column(name="JOB_DESCRIPTION", columnDefinition="VARCHAR", nullable=false, length=1024)
	private String description;
	
	@Column(name="JOB_EXPRESSION", columnDefinition="VARCHAR", nullable=false, length=255)
	private String expression;
	
	@Column(name="JOB_SINGLETON", columnDefinition="BOOLEAN", nullable=false)
	private boolean singleton;
	
	@Column(name="JOB_CREATED_BY", columnDefinition="VARCHAR", nullable=false, length=32)
	private String createdBy;
	
	@Column(name="JOBT_CREATED_AT", columnDefinition="TIMESTAMP", nullable=false)
	private Timestamp createdAt;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getGroup() {
		return group;
	}
	
	public void setGroup(String group) {
		this.group = group;
	}
	
	public String getClazz() {
		return clazz;
	}
	
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getExpression() {
		return expression;
	}
	
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	public boolean isSingleton() {
		return singleton;
	}
	
	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
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

	public static JobDefinition fromJson(String json) {
		return GsonHelper.GSON.fromJson(json, JobDefinition.class);
	}
	
	public String toJson() {
		return GsonHelper.GSON.toJson(this, JobDefinition.class);
	}
	
	@Override
	public String toString() {
		return toJson();
	}

}
