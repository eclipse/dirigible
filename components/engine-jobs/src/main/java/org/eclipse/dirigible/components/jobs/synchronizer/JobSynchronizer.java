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
package org.eclipse.dirigible.components.jobs.synchronizer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactPhase;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.helpers.JsonHelper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizersOrder;
import org.eclipse.dirigible.components.jobs.domain.Job;
import org.eclipse.dirigible.components.jobs.domain.JobParameter;
import org.eclipse.dirigible.components.jobs.manager.JobsManager;
import org.eclipse.dirigible.components.jobs.service.JobEmailService;
import org.eclipse.dirigible.components.jobs.service.JobLogService;
import org.eclipse.dirigible.components.jobs.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The Class JobSynchronizer.
 *
 * @param <A> the generic type
 */
@Component
@Order(SynchronizersOrder.JOB)
public class JobSynchronizer<A extends Artefact> implements Synchronizer<Job> {

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(JobSynchronizer.class);

    /**
     * The Constant FILE_JOB_EXTENSION.
     */
    public static final String FILE_EXTENSION_JOB = ".job";

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
    
    /** The Scheduler manager. */
    @Autowired
    private JobsManager schedulerManager;

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
        return file.toString().endsWith(getFileExtension());
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
     * @throws ParseException 
     */
    @Override
    public List<Job> parse(String location, byte[] content) throws ParseException {
        Job job = JsonHelper.fromJson(new String(content, StandardCharsets.UTF_8), Job.class);
        Configuration.configureObject(job);
        job.setLocation(location);
        job.setName(FilenameUtils.getBaseName(location));
        job.setType(Job.ARTEFACT_TYPE);
        job.updateKey();
        job.getParameters().forEach(j -> j.setJob(job));
        try {
        	Job maybe = getService().findByKey(job.getKey());
			if (maybe != null) {
				job.setId(maybe.getId());
				job.getParameters().forEach(p -> {
					JobParameter m = maybe.getParameter(p.getName());
					if (m != null) {
						p.setId(m.getId());
						p.setValue(m.getValue());
					}
				});
			}
			Job result = getService().save(job);
			return List.of(result);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
            if (logger.isErrorEnabled()) {logger.error("job: {}", job);}
            if (logger.isErrorEnabled()) {logger.error("content: {}", new String(content));}
            throw new ParseException(e.getMessage(), 0);
        }
    }
    
    /**
	 * Retrieve.
	 *
	 * @param location the location
	 * @return the list
	 */
	@Override
	public List<Job> retrieve(String location) {
		return getService().getAll();
	}
	
	/**
	 * Sets the status.
	 *
	 * @param artefact the artefact
	 * @param lifecycle the lifecycle
	 * @param error the error
	 */
	@Override
	public void setStatus(Artefact artefact, ArtefactLifecycle lifecycle, String error) {
		artefact.setLifecycle(lifecycle);
		artefact.setError(error);
		getService().save((Job) artefact);
	}

    /**
     * Gets the service.
     *
     * @return the service
     */
    @Override
    public ArtefactService<Job> getService() {return jobService;}

    /**
     * Complete.
     *
     * @param wrapper the wrapper
     * @param flow the flow
     * @return true, if successful
     */
    @Override
    public boolean complete(TopologyWrapper<Artefact> wrapper, ArtefactPhase flow) {
        Job job = null;
		if (wrapper.getArtefact() instanceof Job) {
			job = (Job) wrapper.getArtefact();
		} else {
			throw new UnsupportedOperationException(String.format("Trying to process %s as Job", wrapper.getArtefact().getClass()));
		}
		
		switch (flow) {
		case CREATE:
			if (ArtefactLifecycle.NEW.equals(job.getLifecycle())) {
				try {
					schedulerManager.scheduleJob(job);
					job.setRunning(true);
					getService().save(job);
					callback.registerState(this, wrapper, ArtefactLifecycle.CREATED, "");
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
		            callback.addError(e.getMessage());
					callback.registerState(this, wrapper, ArtefactLifecycle.CREATED, "");
				}
			}
			break;
		case UPDATE:
			if (ArtefactLifecycle.MODIFIED.equals(job.getLifecycle())) {
				try {
            		schedulerManager.unscheduleJob(job.getName(), job.getGroup());
            		job.setRunning(false);
            		getService().save(job);
					schedulerManager.scheduleJob(job);
					job.setRunning(true);
					getService().save(job);
					callback.registerState(this, wrapper, ArtefactLifecycle.UPDATED, "");
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
		            callback.addError(e.getMessage());
					callback.registerState(this, wrapper, ArtefactLifecycle.UPDATED, e.getMessage());
				}
			}
			break;
		case DELETE:
			if (ArtefactLifecycle.CREATED.equals(job.getLifecycle())
					|| ArtefactLifecycle.UPDATED.equals(job.getLifecycle())) {
				try {
            		schedulerManager.unscheduleJob(job.getName(), job.getGroup());
            		job.setRunning(false);
            		getService().delete(job);
					callback.registerState(this, wrapper, ArtefactLifecycle.DELETED, "");
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
		            callback.addError(e.getMessage());
					callback.registerState(this, wrapper, ArtefactLifecycle.DELETED, e.getMessage());
				}
			}
			break;
		case START:
			if (job.getRunning() == null || !job.getRunning()) {
				try {
					schedulerManager.scheduleJob(job);
					job.setRunning(true);
					getService().save(job);
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
		            callback.addError(e.getMessage());
					callback.registerState(this, wrapper, ArtefactLifecycle.CREATED, "");
				}
			}
			break;
		case STOP:
			if (job.getRunning()) {
				try {
					schedulerManager.unscheduleJob(job.getName(), job.getGroup());
					job.setRunning(false);
					getService().save(job);
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
		            callback.addError(e.getMessage());
					callback.registerState(this, wrapper, ArtefactLifecycle.CREATED, "");
				}
			}
			break;
		}
		
		return true;
    }
    
    /**
     * Cleanup.
     *
     * @param job the artefact
     */
    @Override
    public void cleanup(Job job) {
        try {
        	schedulerManager.unscheduleJob(job.getName(), job.getGroup());
        	jobLogService.deleteAllByJobName(job.getName());
            jobEmailService.deleteAllByJobName(job.getName());
            getService().delete(job);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
            callback.addError(e.getMessage());
            callback.registerState(this, job, ArtefactLifecycle.DELETED, e.getMessage());
        }
    }

    /**
     * Sets the callback.
     *
     * @param callback the new callback
     */
    @Override
    public void setCallback(SynchronizerCallback callback) {
    	this.callback = callback;
    }
    
    /**
     * Gets the file extension.
     *
     * @return the file extension
     */
    @Override
	public String getFileExtension() {
		return FILE_EXTENSION_JOB;
	}

	/**
	 * Gets the artefact type.
	 *
	 * @return the artefact type
	 */
	@Override
	public String getArtefactType() {
		return Job.ARTEFACT_TYPE;
	}
}
