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
package org.eclipse.dirigible.commons.health;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.commons.health.HealthStatus.Jobs.JobStatus;
import org.eclipse.dirigible.commons.timeout.TimeLimited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HealthStatus {
	
	private static final Logger logger = LoggerFactory.getLogger(HealthStatus.class);

	private static final HealthStatus INSTANCE = new HealthStatus();
	
	private long started = System.currentTimeMillis();

	public static HealthStatus getInstance() {
		if (!INSTANCE.status.equals(Status.Ready) && (System.currentTimeMillis() - INSTANCE.started) > TimeLimited.getTimeoutInMillis()) {
			INSTANCE.status = Status.Ready;
			logger.warn("Health status: one or more synchronizers still in progress...");
		}
		return INSTANCE;
	}

	private static void setCurrentStatus() {
		HealthStatus healthStatus = getInstance();
		healthStatus.currentStatus = Status.Ready;
		for (JobStatus next : healthStatus.getJobs().getJobsStatuses()) {
			if (next.equals(JobStatus.Running)) {
				healthStatus.currentStatus = Status.Running;
				break;
			} else if (next.equals(JobStatus.Failed)) {
				healthStatus.currentStatus = Status.NotReady;
				break;
			}
		}
		if (!healthStatus.status.equals(Status.Ready) && healthStatus.currentStatus.equals(Status.Ready)) {
			healthStatus.status = Status.Ready;
		}
	}

	public enum Status {
		Ready, NotReady, Running
	}

	private Status status = Status.NotReady;
	private Status currentStatus = Status.NotReady;
	private Jobs jobs = new Jobs();

	private HealthStatus() {
	
	}

	public Status getStatus() {
		return status;
	}

	public Status getCurrentStatus() {
		return currentStatus;
	}

	public Jobs getJobs() {
		return jobs;
	}


	public static class Jobs {
		public enum JobStatus {
			Running, Succeeded, Failed 
		}

		private Map<String, JobStatus> statuses = new HashMap<String, JobStatus>();


		public Map<String, JobStatus> getStatuses() {
			return statuses;
		}

		public void setStatus(String name, JobStatus status) {
			statuses.put(name, status);
			setCurrentStatus();
		}

		private Collection<JobStatus> getJobsStatuses() {
			return statuses.values();
		}

	}
}
