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

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.jobs.domain.JobEmail;
import org.eclipse.dirigible.components.jobs.domain.JobLog;
import org.eclipse.dirigible.components.jobs.repository.JobEmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * The Class JobEmailService.
 */
@Service
@Transactional
public class JobEmailService implements ArtefactService<JobEmail> {

    /** The job email repository. */
    @Autowired
    private JobEmailRepository jobEmailRepository;

    /**
     * Gets the all.
     *
     * @return the all
     */
    @Override
    @Transactional(readOnly = true)
    public List<JobEmail> getAll() {
        return jobEmailRepository.findAll();
    }

    /**
     * Find all.
     *
     * @param pageable the pageable
     * @return the page
     */
    @Override
    @Transactional(readOnly = true)
    public Page<JobEmail> findAll(Pageable pageable) {
        return jobEmailRepository.findAll(pageable);
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the job email
     */
    @Override
    @Transactional(readOnly = true)
    public JobEmail findById(Long id) {
        Optional<JobEmail> jobEmail = jobEmailRepository.findById(id);
        if (jobEmail.isPresent()) {
            return jobEmail.get();
        } else {
            throw new IllegalArgumentException("JobEmail with id does not exist: " + id);
        }
    }

    /**
     * Find by name.
     *
     * @param name the name
     * @return the job email
     */
    @Override
    @Transactional(readOnly = true)
    public JobEmail findByName(String name) {
        JobEmail filter = new JobEmail();
        filter.setName(name);
        Example<JobEmail> example = Example.of(filter);
        Optional<JobEmail> jobEmail = jobEmailRepository.findOne(example);
        if (jobEmail.isPresent()) {
            return jobEmail.get();
        } else {
            throw new IllegalArgumentException("JobEmail with name does not exist: " + name);
        }
    }

    /**
     * Find by name.
     *
     * @param name the name
     * @return the job email
     */

    @Transactional(readOnly = true)
    public List<JobEmail> findAllByName(String name) {
        JobEmail filter = new JobEmail();
        filter.setName(name);
        Example<JobEmail> example = Example.of(filter);
        List<JobEmail> jobEmail = jobEmailRepository.findAll(example);
        return jobEmail;
    }
    
    /**
     * Find by job name.
     *
     * @param jobName the job name
     * @return the job email
     */

    @Transactional(readOnly = true)
    public List<JobEmail> findAllByJobName(String jobName) {
        JobEmail filter = new JobEmail();
        filter.setJobName(jobName);
        Example<JobEmail> example = Example.of(filter);
        List<JobEmail> jobEmail = jobEmailRepository.findAll(example);
        return jobEmail;
    }

    /**
     * Save.
     *
     * @param jobEmail the job email
     * @return the job email
     */
    @Override
    public JobEmail save(JobEmail jobEmail) {
        return jobEmailRepository.saveAndFlush(jobEmail);
    }

    /**
     * Delete.
     *
     * @param jobEmail the job email
     */
    @Override
    public void delete(JobEmail jobEmail) {
        jobEmailRepository.delete(jobEmail);
    }

	/**
	 * Delete all by job name.
	 *
	 * @param jobName the job name
	 */
	public void deleteAllByJobName(String jobName) {
		JobEmail filter = new JobEmail();
        filter.setJobName(jobName);
        Example<JobEmail> example = Example.of(filter);
        List<JobEmail> jobLogs = jobEmailRepository.findAll(example);
        jobEmailRepository.deleteAll(jobLogs);
	}
}
