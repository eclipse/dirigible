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
import org.eclipse.dirigible.components.jobs.domain.JobLogs;
import org.eclipse.dirigible.components.jobs.repository.JobLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class JobLogsService implements ArtefactService<JobLogs> {

    @Autowired
    private JobLogRepository jobLogRepository;

    @Override
    @Transactional(readOnly = true)
    public List<JobLogs> getAll() {
        return jobLogRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobLogs> findAll(Pageable pageable) {
        return jobLogRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public JobLogs findById(Long id) {
        Optional<JobLogs> jobLogs = jobLogRepository.findById(id);
        if (jobLogs.isPresent()) {
            return jobLogs.get();
        } else {
            throw new IllegalArgumentException("JobLogs with id does not exist: " + id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public JobLogs findByName(String name) {
        JobLogs filter = new JobLogs();
        filter.setName(name);
        Example<JobLogs> example = Example.of(filter);
        Optional<JobLogs> jobLogs = jobLogRepository.findOne(example);
        if (jobLogs.isPresent()) {
            return jobLogs.get();
        } else {
            throw new IllegalArgumentException("JobLog with name does not exist: " + name);
        }
    }

    @Override
    public JobLogs save(JobLogs jobLogs) {
        return jobLogRepository.saveAndFlush(jobLogs);
    }

    @Override
    public void delete(JobLogs jobLogs) {
        jobLogRepository.delete(jobLogs);
    }

    public void deleteJobByName(String jobLogName){
        JobLogs jobLogs = findByName(jobLogName);
        delete(jobLogs);
    }
}
