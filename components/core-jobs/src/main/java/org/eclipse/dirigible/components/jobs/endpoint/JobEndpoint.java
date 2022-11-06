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
package org.eclipse.dirigible.components.jobs.endpoint;

import java.util.List;
import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.jobs.domain.Job;
import org.eclipse.dirigible.components.jobs.service.JobService;
import org.eclipse.dirigible.repository.api.IRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Front facing REST service serving the Jobs.
 */
@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_CORE + "jobs")
public class JobEndpoint extends BaseEndpoint {

    /** The job service. */
    @Autowired
    private JobService jobService ;

    /**
     * List jobs.
     *
     * @return the response entity
     */
    @GetMapping()
    public ResponseEntity<List<Job>> listJobs(){
        return ResponseEntity.ok(jobService.getAll());
    }

    /**
     * Enable job.
     *
     * @param name the name
     * @return the response entity
     */
    @PostMapping("enable/{name}")
    public ResponseEntity<Job> enableJob(@PathVariable("name") String name)
    {
        return ResponseEntity.ok(jobService.enable(IRepository.SEPARATOR + name));
    }

    /**
     * Disable job.
     *
     * @param name the name
     * @return the response entity
     */
    @PostMapping("disable/{name}")
    public ResponseEntity<Job> disableJob(@PathVariable("name") String name)
    {
        return ResponseEntity.ok(jobService.disable(IRepository.SEPARATOR + name));
    }
}
