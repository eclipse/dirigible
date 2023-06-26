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
package org.eclipse.dirigible.components.jobs.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.jobs.domain.Job;
import org.eclipse.dirigible.components.jobs.domain.JobLog;
import org.eclipse.dirigible.components.jobs.email.JobEmailProcessor;
import org.eclipse.dirigible.components.jobs.repository.JobLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.icu.text.SimpleDateFormat;

/**
 * The Class JobLogService.
 */
@Service
@Transactional
public class JobLogService implements ArtefactService<JobLog> {

    /** The job log repository. */
    @Autowired
    private JobLogRepository jobLogRepository;
    
    /** The job service. */
    @Autowired
    private JobService jobService;
    
    /** The job email processor. */
    @Autowired
    private JobEmailProcessor jobEmailProcessor;

    /**
     * Gets the all.
     *
     * @return the all
     */
    @Override
    @Transactional(readOnly = true)
    public List<JobLog> getAll() {
        return jobLogRepository.findAll();
    }

    /**
     * Find all.
     *
     * @param pageable the pageable
     * @return the page
     */
    @Override
    @Transactional(readOnly = true)
    public Page<JobLog> getPages(Pageable pageable) {
        return jobLogRepository.findAll(pageable);
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the job log
     */
    @Override
    @Transactional(readOnly = true)
    public JobLog findById(Long id) {
        Optional<JobLog> jobLog = jobLogRepository.findById(id);
        if (jobLog.isPresent()) {
            return jobLog.get();
        } else {
            throw new IllegalArgumentException("JobLog with id does not exist: " + id);
        }
    }

    /**
     * Find by name.
     *
     * @param name the name
     * @return the job log
     */
    @Override
    @Transactional(readOnly = true)
    public JobLog findByName(String name) {
        JobLog filter = new JobLog();
        filter.setName(name);
        Example<JobLog> example = Example.of(filter);
        Optional<JobLog> jobLog = jobLogRepository.findOne(example);
        if (jobLog.isPresent()) {
            return jobLog.get();
        } else {
            throw new IllegalArgumentException("JobLog with name does not exist: " + name);
        }
    }
    
    /**
     * Find by name.
     *
     * @param name the name
     * @return the job log
     */
    @Transactional(readOnly = true)
    public List<JobLog> findByJob(String name) {
        JobLog filter = new JobLog();
        if (name != null && name.startsWith("/")) {
        	name = name.substring(1);
        }
        filter.setJobName(name);
        filter.setStatus(null);
        Example<JobLog> example = Example.of(filter);
        List<JobLog> jobLog = jobLogRepository.findAll(example);
        return jobLog;
    }
    
    /**
     * Find by location.
     *
     * @param location the location
     * @return the list
     */
    @Override
    @Transactional(readOnly = true)
    public List<JobLog> findByLocation(String location) {
    	JobLog filter = new JobLog();
        filter.setLocation(location);
        Example<JobLog> example = Example.of(filter);
        List<JobLog> list = jobLogRepository.findAll(example);
        return list;
    }
    
    /**
     * Find by key.
     *
     * @param key the key
     * @return the job log
     */
    @Override
    @Transactional(readOnly = true)
    public JobLog findByKey(String key) {
    	JobLog filter = new JobLog();
        filter.setKey(key);
        Example<JobLog> example = Example.of(filter);
        Optional<JobLog> jobLog = jobLogRepository.findOne(example);
        if (jobLog.isPresent()) {
            return jobLog.get();
        }
        return null;
    }

    /**
     * Save.
     *
     * @param jobLog the job log
     * @return the job log
     */
    @Override
    public JobLog save(JobLog jobLog) {
        return jobLogRepository.saveAndFlush(jobLog);
    }

    /**
     * Delete.
     *
     * @param jobLog the job log
     */
    @Override
    public void delete(JobLog jobLog) {
        jobLogRepository.delete(jobLog);
    }
    
    private String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    /**
     * Job triggered.
     *
     * @param name the name
     * @param handler the handler
     * @return the job log definition
     */
    public JobLog jobTriggered(String name, String handler) {
        JobLog jobLog = new JobLog();
        jobLog.setName(name);
        jobLog.setJobName(name);
        jobLog.setHandler(handler);
        jobLog.setStatus(JobLog.JOB_LOG_STATUS_TRIGGRED);
        jobLog.setTriggeredAt(new Timestamp(new Date().getTime()));
        jobLog.setLocation(new SimpleDateFormat(DATE_FORMAT).format(new Date()));
        jobLog.updateKey();
        save(jobLog);
        return jobLog;
    }

    /**
     * Job logged.
     *
     * @param name the name
     * @param handler the handler
     * @param message the message
     * @param severity the severity
     * @return the job log definition
     */
    private JobLog jobLogged(String name, String handler, String message, short severity){
        JobLog jobLog = new JobLog();
        jobLog.setName(name);
        jobLog.setJobName(name);
        jobLog.setHandler(handler);
        jobLog.setMessage(message);
        jobLog.setStatus(severity);
        jobLog.setTriggeredAt(new Timestamp(new Date().getTime()));
        jobLog.setLocation(new SimpleDateFormat(DATE_FORMAT).format(new Date()));
        jobLog.updateKey();
        save(jobLog);
        return jobLog;
    }

    /**
     * Job logged.
     *
     * @param name the name
     * @param handler the handler
     * @param message the message
     * @return the job log definition
     */
    public JobLog jobLogged(String name, String handler, String message) {
        return jobLogged(name, handler, message, JobLog.JOB_LOG_STATUS_LOGGED);
    }

    /**
     * Job logged error.
     *
     * @param name the name
     * @param handler the handler
     * @param message the message
     * @return the job log definition
     */
    public JobLog jobLoggedError(String name, String handler, String message) {
        return jobLogged(name, handler, message, JobLog.JOB_LOG_STATUS_ERROR);
    }

    /**
     * Job logged warning.
     *
     * @param name the name
     * @param handler the handler
     * @param message the message
     * @return the job log definition
     */
    public JobLog jobLoggedWarning(String name, String handler, String message) {
        return jobLogged(name, handler, message, JobLog.JOB_LOG_STATUS_WARN);
    }

    /**
     * Job logged info.
     *
     * @param name the name
     * @param handler the handler
     * @param message the message
     * @return the job log definition
     */
    public JobLog jobLoggedInfo(String name, String handler, String message) {
        return jobLogged(name, handler, message, JobLog.JOB_LOG_STATUS_INFO);
    }

    /**
     * Job finished.
     *
     * @param name the name
     * @param handler the handler
     * @param triggeredId the triggered id
     * @param triggeredAt the triggered at
     * @return the job log definition
     */
    public JobLog jobFinished(String name, String handler, long triggeredId, Date triggeredAt) {
        JobLog jobLog = new JobLog();
        jobLog.setName(name);
        jobLog.setJobName(name);
        jobLog.setHandler(handler);
        jobLog.setStatus(JobLog.JOB_LOG_STATUS_FINISHED);
        jobLog.setTriggeredId(triggeredId);
        jobLog.setTriggeredAt(new Timestamp(triggeredAt.getTime()));
        jobLog.setFinishedAt(new Timestamp(new Date().getTime()));
        jobLog.setLocation(new SimpleDateFormat(DATE_FORMAT).format(new Date()));
        jobLog.updateKey();
        save(jobLog);
        Job job = jobService.findByName(name);
		boolean statusChanged = job.getStatus() != JobLog.JOB_LOG_STATUS_FINISHED;
		job.setStatus(JobLog.JOB_LOG_STATUS_FINISHED);
		job.setMessage("");
		job.setExecutedAt(jobLog.getFinishedAt());
		if (statusChanged) {
			String content = jobEmailProcessor.prepareEmail(job, jobEmailProcessor.emailTemplateNormal, jobEmailProcessor.EMAIL_TEMPLATE_NORMAL);
			jobEmailProcessor.sendEmail(job, jobEmailProcessor.emailSubjectNormal, content);
		}
        return jobLog;
    }

    /**
     * Job failed.
     *
     * @param name the name
     * @param handler the handler
     * @param triggeredId the triggered id
     * @param triggeredAt the triggered at
     * @param message the message
     * @return the job log definition
     */
    public JobLog jobFailed(String name, String handler, long triggeredId, Date triggeredAt, String message) {
        JobLog jobLog = new JobLog();
        jobLog.setName(name);
        jobLog.setJobName(name);
        jobLog.setHandler(handler);
        jobLog.setStatus(JobLog.JOB_LOG_STATUS_FAILED);
        jobLog.setTriggeredId(triggeredId);
        jobLog.setTriggeredAt(new Timestamp(triggeredAt.getTime()));
        jobLog.setFinishedAt(new Timestamp(new Date().getTime()));
        jobLog.setMessage(message);
        jobLog.setLocation(new SimpleDateFormat(DATE_FORMAT).format(new Date()));
        jobLog.updateKey();
        save(jobLog);
        Job job = jobService.findByName(name);
		boolean statusChanged = job.getStatus() != JobLog.JOB_LOG_STATUS_FAILED;
		job.setStatus(JobLog.JOB_LOG_STATUS_FAILED);
		job.setMessage(message);
		job.setExecutedAt(jobLog.getFinishedAt());
		if (statusChanged) {
			String content = jobEmailProcessor.prepareEmail(job, jobEmailProcessor.emailTemplateError, jobEmailProcessor.EMAIL_TEMPLATE_ERROR);
			jobEmailProcessor.sendEmail(job, jobEmailProcessor.emailSubjectError, content);
		}
        return jobLog;
    }

	/**
	 * Delete all by job name.
	 *
	 * @param jobName the job name
	 */
	public void deleteAllByJobName(String jobName) {
		JobLog filter = new JobLog();
		if (jobName != null && jobName.startsWith("/")) {
			jobName = jobName.substring(1);
        }
        filter.setJobName(jobName);
        Example<JobLog> example = Example.of(filter);
        List<JobLog> jobLogs = jobLogRepository.findAll(example);
        jobLogRepository.deleteAll(jobLogs);
		
	}
}
