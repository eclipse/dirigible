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

var configurations = require("core/v4/configurations");

exports.getJobs = function () {
	let jobs = new Array();
	let list = JSON.parse(org.eclipse.dirigible.api.v3.job.JobFacade.getJobs());
	for (let i in list) {
		let data = list[i];
		let job = new Job(data);
		jobs.push(job);
	}
	return jobs;
};

exports.getJob = function (name) {
	let data = JSON.parse(org.eclipse.dirigible.api.v3.job.JobFacade.getJob(name));
	let job = new Job(data);
	return job;
};

exports.getParameter = function (name) {
	return configurations.get(name);
};

exports.setParameter = function (name, value) {
	return configurations.set(name, value);
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
