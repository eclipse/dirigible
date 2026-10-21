/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.sdk.job;

import java.util.Map;

import org.eclipse.dirigible.components.api.job.JobFacade;

/**
 * Manages the jobs the platform knows about: lists and reads their definitions, enables and
 * disables them, runs them on demand and writes to a job's execution log - the same operations the
 * Jobs perspective offers. Useful for admin endpoints, health-check pages and jobs that report
 * their own progress.
 * <p>
 * Job registration itself is declarative: either drop a {@code .job} JSON descriptor under a
 * project (picked up by the Job synchronizer) or annotate a class with
 * {@link org.eclipse.dirigible.sdk.job.Scheduled @Scheduled}. This class does not create or remove
 * jobs - that would bypass the synchronizer contract and leave the live state out of sync with the
 * declarative source. A job is named as its definition names it; a client-Java job is named after
 * its class ({@code <fqn>}) or its {@code @Scheduled} method ({@code <fqn>.<method>}).
 */
public final class Scheduler {

    private Scheduler() {}

    /**
     * Every job definition - name, group, cron, handler, engine, enabled flag, owner.
     *
     * @return a JSON array of the job definitions
     */
    public static String list() {
        return JobFacade.getJobs();
    }

    /**
     * One job definition.
     *
     * @param name the job name
     * @return the job definition as JSON
     * @throws IllegalArgumentException if no job has that name
     */
    public static String getJob(String name) {
        return JobFacade.getJob(name);
    }

    /**
     * Enables a job, so it runs on its schedule again.
     *
     * @param name the job name
     * @throws Exception if the job does not exist or cannot be scheduled
     */
    public static void enable(String name) throws Exception {
        JobFacade.enable(name);
    }

    /**
     * Disables a job, so it no longer runs on its schedule.
     *
     * @param name the job name
     * @throws Exception if the job does not exist or cannot be unscheduled
     */
    public static void disable(String name) throws Exception {
        JobFacade.disable(name);
    }

    /**
     * Runs a job now, on the calling thread, leaving the same execution-log trail a scheduled run does.
     *
     * @param name the job name
     * @throws Exception if the job does not exist or its body fails
     */
    public static void trigger(String name) throws Exception {
        trigger(name, Map.of());
    }

    /**
     * Runs a job now with parameters, on the calling thread. Only the parameters the job declares may
     * be passed; the job body reads them as configurations, for the duration of this run only.
     *
     * @param name the job name
     * @param parameters the parameter values, keyed by the declared parameter name
     * @throws IllegalArgumentException if the job does not exist or does not declare a parameter
     * @throws Exception if the job body fails
     */
    public static void trigger(String name, Map<String, String> parameters) throws Exception {
        JobFacade.get()
                 .getJobService()
                 .trigger(name, parameters);
    }

    /**
     * Writes a message to a job's execution log.
     *
     * @param name the job name
     * @param message the message
     * @throws Exception if the job does not exist
     */
    public static void log(String name, String message) throws Exception {
        JobFacade.log(name, message);
    }

    /**
     * Writes an error to a job's execution log.
     *
     * @param name the job name
     * @param message the error message
     * @throws Exception if the job does not exist
     */
    public static void error(String name, String message) throws Exception {
        JobFacade.error(name, message);
    }

    /**
     * Writes a warning to a job's execution log.
     *
     * @param name the job name
     * @param message the warning message
     * @throws Exception if the job does not exist
     */
    public static void warn(String name, String message) throws Exception {
        JobFacade.warn(name, message);
    }

    /**
     * Writes an informational message to a job's execution log.
     *
     * @param name the job name
     * @param message the message
     * @throws Exception if the job does not exist
     */
    public static void info(String name, String message) throws Exception {
        JobFacade.info(name, message);
    }
}
