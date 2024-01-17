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

import * as configurations from "@dirigible/core/configurations";
const JobFacade = Java.type("org.eclipse.dirigible.components.api.job.JobFacade");

export function getJobs(): Job[] {
    let jobs = new Array();
    let list = JSON.parse(JobFacade.getJobs());
    for (let i in list) {
        let data = list[i];
        let job = new Job(data);
        jobs.push(job);
    }
    return jobs;
};

export function getJob(name: string): Job {
    let jobData = JobFacade.getJob(name);
    let data = JSON.parse(jobData);
    let job = new Job(data);
    return job;
};

export function enable(name: string): void{
    JobFacade.enable(name);
};

export function disable(name: string): void {
    JobFacade.disable(name);
};

export function trigger(name: string, parameters: Object): void {
    JobFacade.trigger(name, JSON.stringify(parameters));
};

export function log(name: string, message: string): void {
    JobFacade.log(name, message);
};

export function error(name: string, message: string): void {
    JobFacade.error(name, message);
};

export function warn(name: string, message: string): void {
    JobFacade.warn(name, message);
};

export function info(name: string, message: string): void {
    JobFacade.info(name, message);
};

/**
 * Job object
 */
class Job {

    constructor(private data) { }

    public getName(): string {
        return this.data.name;
    };

    public getGroup(): string {
        return this.data.group;
    };

    public getClazz(): string {
        return this.data.clazz;
    };

    public getDescription(): string {
        return this.data.description;
    };

    public getExpression(): string {
        return this.data.expression;
    };

    public getHandler(): string {
        return this.data.handler;
    };

    public getEngine(): string {
        return this.data.engine;
    };

    public getSingleton(): boolean {
        return this.data.singleton;
    };

    public getEnabled(): boolean {
        return this.data.enabled;
    };

    public getCreatedBy(): string {
        return this.data.createdBy;
    };

    public getCreatedAt(): number {
        return this.data.createdAt;
    };

    public getParameters(): JobParameters {
        return new JobParameters(this.data.parameters);
    };

    public getParameter(name): string {
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

    public enable(): void {
        JobFacade.enable(this.getName());
    };

    public disable(): void {
        JobFacade.disable(this.getName());
    };

    public trigger(parameters): void {
        JobFacade.trigger(this.getName(), JSON.stringify(parameters));
    };

    public log(message): void {
        JobFacade.log(this.getName(), message);
    };

    public error(message): void {
        JobFacade.error(this.getName(), message);
    };

    public warn(message): void {
        JobFacade.warn(this.getName(), message);
    };

    public info(message): void {
        JobFacade.info(this.getName(), message);
    };

}

/**
 * Job Parameters object
 */
class JobParameters {

    constructor(private data) { }

    public get(i): JobParameter {
        return new JobParameter(this.data[i]);
    };

    public count(): number {
        return this.data.length;
    };

}

/**
 * Job Parameter object
 */
class JobParameter {

    constructor(private data) { }

    public getName(): string {
        return this.data.name;
    };

    public getDescription(): string {
        return this.data.description;
    };

    public getType(): string {
        return this.data.type;
    };

    public getDefaultValue(): string {
        return this.data.defaultValue;
    };

    public getChoices(): string[] {
        return this.data.choices;
    };

}
