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
/**
 * API v4 Job
 * 
 */

var configurations = require("core/configurations");

exports.getJobs = function () {
	let jobs = new Array();
	let list = JSON.parse(org.eclipse.dirigible.components.api.job.JobFacade.getJobs());
	for (let i in list) {
		let data = list[i];
		let job = new Job(data);
		jobs.push(job);
	}
	return jobs;
};

exports.getJob = function (name) {
	let jobData = org.eclipse.dirigible.components.api.job.JobFacade.getJob(name);
	let data = JSON.parse(jobData);
	let job = new Job(data);
	return job;
};

exports.enable = function (name) {
	org.eclipse.dirigible.components.api.job.JobFacade.enable(name);
};

exports.disable = function (name) {
	org.eclipse.dirigible.components.api.job.JobFacade.disable(name);
};

exports.trigger = function (name, parameters) {
	org.eclipse.dirigible.components.api.job.JobFacade.trigger(name, JSON.stringify(parameters));
};

exports.log = function (name, message) {
	org.eclipse.dirigible.components.api.job.JobFacade.log(name, message);
};

exports.error = function (name, message) {
	org.eclipse.dirigible.components.api.job.JobFacade.error(name, message);
};

exports.warn = function (name, message) {
	org.eclipse.dirigible.components.api.job.JobFacade.warn(name, message);
};

exports.info = function (name, message) {
	org.eclipse.dirigible.components.api.job.JobFacade.info(name, message);
};

/**
 * Job object
 */
function Job(data) {

	this.data = data;

	this.getName = function () {
		return data.name;
	};

	this.getGroup = function () {
		return data.group;
	};

	this.getClazz = function () {
		return data.clazz;
	};

	this.getDescription = function () {
		return data.description;
	};

	this.getExpression = function () {
		return data.expression;
	};

	this.getHandler = function () {
		return data.handler;
	};

	this.getEngine = function () {
		return data.engine;
	};

	this.getSingleton = function () {
		return data.singleton;
	};

	this.getEnabled = function () {
		return data.enabled;
	};

	this.getCreatedBy = function () {
		return data.createdBy;
	};

	this.getCreatedAt = function () {
		return data.createdAt;
	};

	this.getParameters = function () {
		return new JobParameters(data.parameters);
	};

	this.getParameter = function (name) {
		if (this.data) {
			for (let i in this.data.parameters) {
				if (this.data.parameters[i].name === name) {
					let value = configurations.get(name);
					return value && value !== null ? value : data.parameters[i].defaultValue;
				}
			}
		} else {
			console.error("Job is not valid");
		}
		return null;
	};

	this.enable = function () {
		org.eclipse.dirigible.components.api.job.JobFacade.enable(this.getName());
	};

	this.disable = function () {
		org.eclipse.dirigible.components.api.job.JobFacade.disable(this.getName());
	};

	this.trigger = function (parameters) {
		org.eclipse.dirigible.components.api.job.JobFacade.trigger(this.getName(), JSON.stringify(parameters));
	};

	this.log = function (message) {
		org.eclipse.dirigible.components.api.job.JobFacade.log(this.getName(), message);
	};

	this.error = function (message) {
		org.eclipse.dirigible.components.api.job.JobFacade.error(this.getName(), message);
	};

	this.warn = function (message) {
		org.eclipse.dirigible.components.api.job.JobFacade.warn(this.getName(), message);
	};

	this.info = function (message) {
		org.eclipse.dirigible.components.api.job.JobFacade.info(this.getName(), message);
	};

}

/**
 * Job Parameters object
 */
function JobParameters(data) {

	this.data = data;

	this.get = function (i) {
		return new JobParameter(data[i]);
	};

	this.count = function () {
		return data.length;
	};

}

/**
 * Job Parameter object
 */
function JobParameter(data) {

	this.data = data;

	this.getName = function () {
		return data.name;
	};

	this.getDescription = function () {
		return data.description;
	};

	this.getType = function () {
		return data.type;
	};

	this.getDefaultValue = function () {
		return data.defaultValue;
	};

	this.getChoices = function () {
		return data.choices;
	};

}
