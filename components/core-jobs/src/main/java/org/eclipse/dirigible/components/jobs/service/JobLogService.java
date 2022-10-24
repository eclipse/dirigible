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
import org.eclipse.dirigible.components.jobs.domain.JobLog;
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
public class JobLogService implements ArtefactService<JobLog> {

    @Autowired
    private JobLogRepository jobLogRepository;

    @Override
    @Transactional(readOnly = true)
    public List<JobLog> getAll() {
        return jobLogRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobLog> findAll(Pageable pageable) {
        return jobLogRepository.findAll(pageable);
    }

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

    @Override
    public JobLog save(JobLog jobLog) {
        return jobLogRepository.saveAndFlush(jobLog);
    }

    @Override
    public void delete(JobLog jobLog) {
        jobLogRepository.delete(jobLog);
    }

    public void deleteJobByName(String jobLogName){
        JobLog jobLog = findByName(jobLogName);
        delete(jobLog);
    }
}
