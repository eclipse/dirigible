/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * API Job
 */

const configurations = dirigibleRequire("core/configurations");
const JobFacade = Java.type("org.eclipse.dirigible.components.api.job.JobFacade");

export function getJobs() {
    let jobs = new Array();
    let list = JSON.parse(JobFacade.getJobs());
    for (let i in list) {
        let data = list[i];
        let job = new Job(data);
        jobs.push(job);
    }
    return jobs;
};

export function getJob(name) {
    let jobData = JobFacade.getJob(name);
    let data = JSON.parse(jobData);
    let job = new Job(data);
    return job;
};

export function enable(name) {
    JobFacade.enable(name);
};

export function disable(name) {
    JobFacade.disable(name);
};

export function trigger(name, parameters) {
    JobFacade.trigger(name, JSON.stringify(parameters));
};

export function log(name, message) {
    JobFacade.log(name, message);
};

export function error(name, message) {
    JobFacade.error(name, message);
};

export function warn(name, message) {
    JobFacade.warn(name, message);
};

export function info(name, message) {
    JobFacade.info(name, message);
};

/**
 * Job object
 */
class Job {

    constructor(private data) { }

    getName() {
        return this.data.name;
    };

    getGroup() {
        return this.data.group;
    };

    getClazz() {
        return this.data.clazz;
    };

    getDescription() {
        return this.data.description;
    };

    getExpression() {
        return this.data.expression;
    };

    getHandler() {
        return this.data.handler;
    };

    getEngine() {
        return this.data.engine;
    };

    getSingleton() {
        return this.data.singleton;
    };

    getEnabled() {
        return this.data.enabled;
    };

    getCreatedBy() {
        return this.data.createdBy;
    };

    getCreatedAt() {
        return this.data.createdAt;
    };

    getParameters() {
        return new JobParameters(this.data.parameters);
    };

    getParameter(name) {
        if (this.data) {
            for (let i in this.data.parameters) {
                if (this.data.parameters[i].name === name) {
                    let value = configurations.get(name);
                    return value && value !== null ? value : this.data.parameters[i].defaultValue;
                }
            }
        } else {
            console.error("Job is not valid");
        }
        return null;
    };

    enable() {
        JobFacade.enable(this.getName());
    };

    disable() {
        JobFacade.disable(this.getName());
    };

    trigger(parameters) {
        JobFacade.trigger(this.getName(), JSON.stringify(parameters));
    };

    log(message) {
        JobFacade.log(this.getName(), message);
    };

    error(message) {
        JobFacade.error(this.getName(), message);
    };

    warn(message) {
        JobFacade.warn(this.getName(), message);
    };

    info(message) {
        JobFacade.info(this.getName(), message);
    };

}

/**
 * Job Parameters object
 */
class JobParameters {

    constructor(private data) { }

    get(i) {
        return new JobParameter(this.data[i]);
    };

    count() {
        return this.data.length;
    };

}

/**
 * Job Parameter object
 */
class JobParameter {

    constructor(private data) { }

    getName() {
        return this.data.name;
    };

    getDescription() {
        return this.data.description;
    };

    getType() {
        return this.data.type;
    };

    getDefaultValue() {
        return this.data.defaultValue;
    };

    getChoices() {
        return this.data.choices;
    };

}
