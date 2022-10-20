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
import org.eclipse.dirigible.components.jobs.domain.JobEmailDefinition;
import org.eclipse.dirigible.components.jobs.repository.JobEmailDefinitionRepository;
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
public class JobEmailDefinitionService implements ArtefactService<JobEmailDefinition> {

    @Autowired
    private JobEmailDefinitionRepository jobEmailDefinitionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<JobEmailDefinition> getAll() {
        return jobEmailDefinitionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobEmailDefinition> findAll(Pageable pageable) {
        return jobEmailDefinitionRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public JobEmailDefinition findById(Long id) {
        Optional<JobEmailDefinition> jobEmailDefinition = jobEmailDefinitionRepository.findById(id);
        if (jobEmailDefinition.isPresent()) {
            return jobEmailDefinition.get();
        } else {
            throw new IllegalArgumentException("JobEmailDefinition with id does not exist: " + id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public JobEmailDefinition findByName(String name) {
        JobEmailDefinition filter = new JobEmailDefinition();
        filter.setName(name);
        Example<JobEmailDefinition> example = Example.of(filter);
        Optional<JobEmailDefinition> jobEmailDefinition = jobEmailDefinitionRepository.findOne(example);
        if (jobEmailDefinition.isPresent()) {
            return jobEmailDefinition.get();
        } else {
            throw new IllegalArgumentException("JobEmailDefinition with name does not exist: " + name);
        }
    }

    @Override
    public JobEmailDefinition save(JobEmailDefinition jobEmailDefinition) {
        return jobEmailDefinitionRepository.saveAndFlush(jobEmailDefinition);
    }

    @Override
    public void delete(JobEmailDefinition jobEmailDefinition) {
        jobEmailDefinitionRepository.delete(jobEmailDefinition);
    }
}
