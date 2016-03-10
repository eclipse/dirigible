package org.eclipse.dirigible.runtime.job;

import java.util.UUID;

public class Job {

	private String jobUUID = UUID.randomUUID().toString();

	private String name;

	private String description;

	private String expression;

	private String type;

	private String module;

	public String getJobUUID() {
		return jobUUID;
	}

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

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

}
