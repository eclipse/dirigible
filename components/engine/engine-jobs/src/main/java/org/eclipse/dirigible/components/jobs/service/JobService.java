/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.jobs.service;

import static java.text.MessageFormat.format;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.jobs.domain.Job;
import org.eclipse.dirigible.components.jobs.email.JobEmailProcessor;
import org.eclipse.dirigible.components.jobs.manager.JobsManager;
import org.eclipse.dirigible.components.jobs.repository.JobRepository;
import org.eclipse.dirigible.graalium.core.DirigibleJavascriptCodeRunner;
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
public class JobService implements ArtefactService<Job> {

    /** The job repository. */
    @Autowired
    private JobRepository jobRepository;

    /** The job email service. */
    @Autowired
    private JobEmailProcessor jobEmailProcessor;

    @Autowired
    private JobsManager jobsManager;

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
    public Page<Job> getPages(Pageable pageable) {
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
        }
        throw new IllegalArgumentException("Job with id does not exist: " + id);
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
        if (name != null && name.startsWith("/")) {
            name = name.substring(1);
        }
        filter.setName(name);
        filter.setEnabled(null);
        filter.setStatus(null);
        Example<Job> example = Example.of(filter);
        Optional<Job> job = jobRepository.findOne(example);
        if (job.isPresent()) {
            return job.get();
        }
        throw new IllegalArgumentException("Job with name does not exist: " + name);
    }

    /**
     * Find by location.
     *
     * @param location the location
     * @return the list
     */
    @Override
    @Transactional(readOnly = true)
    public List<Job> findByLocation(String location) {
        Job filter = new Job();
        filter.setLocation(location);
        Example<Job> example = Example.of(filter);
        return jobRepository.findAll(example);
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
        filter.setEnabled(null);
        filter.setStatus(null);
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
                String content = jobEmailProcessor.prepareEmail(job, JobEmailProcessor.emailTemplateDisable,
                        JobEmailProcessor.EMAIL_TEMPLATE_DISABLE);
                jobEmailProcessor.sendEmail(job, JobEmailProcessor.emailSubjectDisable, content);
            } else if (!existing.isEnabled() && job.isEnabled()) {
                String content =
                        jobEmailProcessor.prepareEmail(job, JobEmailProcessor.emailTemplateEnable, JobEmailProcessor.EMAIL_TEMPLATE_ENABLE);
                jobEmailProcessor.sendEmail(job, JobEmailProcessor.emailSubjectEnable, content);
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
     * @throws Exception
     */
    public Job enable(String name) throws Exception {
        Job job = findByName(name);
        job.setEnabled(true);
        jobsManager.scheduleJob(job);
        return jobRepository.saveAndFlush(job);
    }

    /**
     * Disable.
     *
     * @param name the name
     * @return the job
     * @throws Exception
     */
    public Job disable(String name) throws Exception {
        Job job = findByName(name);
        job.setEnabled(false);
        jobsManager.unscheduleJob(job.getName(), job.getGroup());
        return jobRepository.saveAndFlush(job);
    }

    /**
     * Trigger.
     *
     * @param name the name
     * @param parametersMap the parameters map
     * @return true, if successful
     * @throws Exception the exception
     */
    public boolean trigger(String name, Map<String, String> parametersMap) throws Exception {
        Job job = findByName(name);
        if (job == null) {
            String error = format("Job with name {0} does not exist, hence cannot be triggered", name);
            throw new Exception(error);
        }
        Map<String, String> memento = new HashMap<String, String>();
        try {
            for (Map.Entry<String, String> entry : parametersMap.entrySet()) {
                memento.put(entry.getKey(), Configuration.get(entry.getKey()));
                Configuration.set(entry.getKey(), entry.getValue());
            }

            String handler = job.getHandler();
            Path handlerPath = Path.of(handler);

            try (DirigibleJavascriptCodeRunner runner = new DirigibleJavascriptCodeRunner()) {
                runner.run(handlerPath);
            } catch (Exception e) {
                throw new Exception(e);
            }
        } finally {
            for (Map.Entry<String, String> entry : memento.entrySet()) {
                Configuration.set(entry.getKey(), entry.getValue());
            }
        }

        return true;
    }

}
