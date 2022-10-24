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
import org.eclipse.dirigible.components.jobs.repository.JobEmailRepository;
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
public class JobEmailService implements ArtefactService<JobEmail> {

    @Autowired
    private JobEmailRepository jobEmailRepository;

    @Override
    @Transactional(readOnly = true)
    public List<JobEmail> getAll() {
        return jobEmailRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobEmail> findAll(Pageable pageable) {
        return jobEmailRepository.findAll(pageable);
    }

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

    @Override
    public JobEmail save(JobEmail jobEmail) {
        return jobEmailRepository.saveAndFlush(jobEmail);
    }

    @Override
    public void delete(JobEmail jobEmail) {
        jobEmailRepository.delete(jobEmail);
    }
}
