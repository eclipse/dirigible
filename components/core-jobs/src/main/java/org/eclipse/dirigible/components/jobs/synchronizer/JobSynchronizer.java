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
package org.eclipse.dirigible.components.jobs.synchronizer;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.topology.TopologicalDepleter;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.base.artefact.ArtefactState;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.jobs.domain.Job;
import org.eclipse.dirigible.components.jobs.domain.JobEmail;
import org.eclipse.dirigible.components.jobs.domain.JobLifecycle;
import org.eclipse.dirigible.components.jobs.domain.JobLog;
import org.eclipse.dirigible.components.jobs.service.JobEmailService;
import org.eclipse.dirigible.components.jobs.service.JobLogService;
import org.eclipse.dirigible.components.jobs.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

/**
 * The Class JobSynchronizer.
 *
 * @param A the generic type
 */
@Component
public class JobSynchronizer<A extends Artefact> implements Synchronizer<Job> {

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(JobSynchronizer.class);

    /**
     * The Constant FILE_JOB_EXTENSION.
     */
    public static final String FILE_JOB_EXTENSION = ".job";

    /**
     * The job service.
     */
    private JobService jobService;

    /**
     * The jobEmail service.
     */
    @Autowired
    private JobEmailService jobEmailService;

    /**
     * The jobLog service.
     */
    @Autowired
    private JobLogService jobLogService;

    /**
     * The synchronization callback.
     */
    private SynchronizerCallback callback;

    /**
     * Instantiates a new job synchronizer.
     *
     * @param jobService the job service
     */
    @Autowired
    public JobSynchronizer(JobService jobService){this.jobService = jobService;}

    /**
     * Checks if is accepted.
     *
     * @param file the file
     * @param attrs the attrs
     * @return true, if is accepted
     */
    @Override
    public boolean isAccepted(Path file, BasicFileAttributes attrs) {
        return file.toString().endsWith(FILE_JOB_EXTENSION);
    }

    /**
     * Checks if is accepted.
     *
     * @param type the type
     * @return true, if is accepted
     */
    @Override
    public boolean isAccepted(String type) {return Job.ARTEFACT_TYPE.equals(type);}

    /**
     * Load.
     *
     * @param location the location
     * @param content the content
     * @return the list
     */
    @Override
    public List<Job> load(String location, byte[] content) {
        Job job = GsonHelper.GSON.fromJson(new String(content, StandardCharsets.UTF_8), Job.class);
        job.setLocation(location);
        job.setName("");
        job.setType(Job.ARTEFACT_TYPE);
        job.updateKey();
        try {
            getService().save(job);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
            if (logger.isErrorEnabled()) {logger.error("job: {}", job);}
            if (logger.isErrorEnabled()) {logger.error("content: {}", new String(content));}
        }
        return List.of(job);
    }

    /**
     * Prepare.
     *
     * @param wrappers the wrappers
     * @param depleter the depleter
     */
    @Override
    public void prepare(List<TopologyWrapper<? extends Artefact>> wrappers, TopologicalDepleter<TopologyWrapper<? extends Artefact>> depleter) {
    }

    /**
     * Process.
     *
     * @param wrappers the wrappers
     * @param depleter the depleter
     */
    @Override
    public void process(List<TopologyWrapper<? extends Artefact>> wrappers, TopologicalDepleter<TopologyWrapper<? extends Artefact>> depleter) {
        try {
            List<TopologyWrapper<? extends Artefact>> results = depleter.deplete(wrappers, ArtefactLifecycle.CREATED.toString());
            callback.registerErrors(this, results, ArtefactLifecycle.CREATED.toString(), ArtefactState.FAILED_CREATE_UPDATE);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
            callback.addError(e.getMessage());
        }
    }

    /**
     * Gets the service.
     *
     * @return the service
     */
    @Override
    public ArtefactService<Job> getService() {return jobService;}

    /**
     * Cleanup.
     *
     * @param artefact the artefact
     */
    @Override
    public void cleanup(Job artefact) {
        try {
            getService().delete(artefact);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            callback.addError(e.getMessage());
        }
    }

    /**
     * Complete.
     *
     * @param wrapper the wrapper
     * @param flow the flow
     * @return true, if successful
     */
    @Override
    public boolean complete(TopologyWrapper<Artefact> wrapper, String flow) {
        Job job = null;
        if (wrapper.getArtefact() instanceof Job){
            job = (Job) wrapper.getArtefact();
            Job existingJob = jobService.findByName(job.getName());
            JobLog jobLog = jobLogService.findById(existingJob.getId());
            JobLifecycle jobLifecycleState = JobLifecycle.valueOf(flow);

            switch (jobLifecycleState){
                case CREATE:
                    jobLogService.jobLogged(existingJob.getName(), existingJob.getHandler(), existingJob.getMessage());
                    break;
                case UPDATE:
                    List<JobEmail> emailDefinitions = jobEmailService.findAllByName(existingJob.getName());
                    JobEmailProcessor jobEmailProcessor = new JobEmailProcessor();
                    jobEmailProcessor.createAndSendJobEmail(job, existingJob, emailDefinitions);
                    break;
                case DROP:
                    jobLogService.delete(jobLog);
                    break;
                case TRIGGERED:
                    jobLogService.jobTriggered(existingJob.getName(), existingJob.getHandler());
                    break;
                case FAILED:
                    jobLogService.jobFailed(existingJob.getName(), existingJob.getHandler(), jobLog.getTriggeredId(), jobLog.getTriggeredAt(), jobLog.getMessage());
                    break;
                case FINISHED:
                    jobLogService.jobFinished(existingJob.getName(), existingJob.getHandler(), jobLog.getTriggeredId(),jobLog.getTriggeredAt());
                    break;
            }
        }
        else {
            throw new UnsupportedOperationException(String.format("Trying to process %s as Job", wrapper.getArtefact().getClass()));
        }

        callback.registerState(this, wrapper, ArtefactLifecycle.CREATED.toString(), ArtefactState.SUCCESSFUL_CREATE_UPDATE);
        return true;
    }

    /**
     * Sets the callback.
     *
     * @param callback the new callback
     */
    @Override
    public void setCallback(SynchronizerCallback callback) {this.callback = callback;}
}
