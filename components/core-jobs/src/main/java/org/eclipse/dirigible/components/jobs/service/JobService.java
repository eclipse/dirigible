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
package org.eclipse.dirigible.components.jobs.service;

import java.util.List;
import java.util.Optional;

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.jobs.domain.Job;
import org.eclipse.dirigible.components.jobs.email.JobEmailProcessor;
import org.eclipse.dirigible.components.jobs.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class JobService.
 */
@Service
@Transactional
public class JobService implements ArtefactService<Job>  {

    /** The job repository. */
    @Autowired
    private JobRepository jobRepository;
    
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
    public List<Job> getAll() {
        return jobRepository.findAll();
    }

    /**
     * Find all.
     *
     * @param pageable the pageable
     * @return the page
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Job> findAll(Pageable pageable) {
        return jobRepository.findAll(pageable);
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the job
     */
    @Override
    @Transactional(readOnly = true)
    public Job findById(Long id) {
        Optional<Job> job = jobRepository.findById(id);
        if (job.isPresent()) {
            return job.get();
        } else {
            throw new IllegalArgumentException("Job with id does not exist: " + id);
        }
    }

    /**
     * Find by name.
     *
     * @param name the name
     * @return the job
     */
    @Override
    @Transactional(readOnly = true)
    public Job findByName(String name) {
        Job filter = new Job();
        filter.setName(name);
        Example<Job> example = Example.of(filter);
        Optional<Job> job = jobRepository.findOne(example);
        if (job.isPresent()) {
            return job.get();
        } else {
            throw new IllegalArgumentException("Job with name does not exist: " + name);
        }
    }
    
    /**
     * Find by key.
     *
     * @param key the key
     * @return the job
     */
    @Override
    @Transactional(readOnly = true)
    public Job findByKey(String key) {
    	Job filter = new Job();
        filter.setKey(key);
        Example<Job> example = Example.of(filter);
        Optional<Job> job = jobRepository.findOne(example);
        if (job.isPresent()) {
            return job.get();
        }
        return null;
    }

    /**
     * Save.
     *
     * @param job the job
     * @return the job
     */
    @Override
    public Job save(Job job) {
    	Job existing = null;
		try {
			existing = findByName(job.getName());
		} catch (Exception e) {
			// ignore if does not exist yet
		}
		if (existing != null) {
			if (existing.isEnabled() && !job.isEnabled()) {
				String content = jobEmailProcessor.prepareEmail(job, jobEmailProcessor.emailTemplateDisable, jobEmailProcessor.EMAIL_TEMPLATE_DISABLE);
				jobEmailProcessor.sendEmail(job, jobEmailProcessor.emailSubjectDisable, content);
			} else if (!existing.isEnabled() && job.isEnabled()) {
				String content = jobEmailProcessor.prepareEmail(job, jobEmailProcessor.emailTemplateEnable, jobEmailProcessor.EMAIL_TEMPLATE_ENABLE);
				jobEmailProcessor.sendEmail(job, jobEmailProcessor.emailSubjectEnable, content);
			}
		}
        return jobRepository.saveAndFlush(job);
    }

    /**
     * Delete.
     *
     * @param job the job
     */
    @Override
    public void delete(Job job) {
        jobRepository.delete(job);
    }

    /**
     * Enable.
     *
     * @param name the name
     * @return the job
     */
    public Job enable(String name) {
        Job job = findByName(name);
        job.setEnabled(true);
        return jobRepository.saveAndFlush(job);
    }

    /**
     * Disable.
     *
     * @param name the name
     * @return the job
     */
    public Job disable(String name) {
        Job job = findByName(name);
        job.setEnabled(false);
        return jobRepository.saveAndFlush(job);
    }
}
