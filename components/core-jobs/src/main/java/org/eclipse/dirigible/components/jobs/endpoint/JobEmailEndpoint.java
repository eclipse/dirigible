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

import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.jobs.domain.JobEmail;
import org.eclipse.dirigible.components.jobs.service.JobEmailService;
import org.eclipse.dirigible.repository.api.IRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.ws.rs.PathParam;
import java.util.List;

/**
 * The Class JobEmailEndpoint.
 */
@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_CORE + "jobEmail")
public class JobEmailEndpoint extends BaseEndpoint {

    /** The job email service. */
    @Autowired
    private JobEmailService jobEmailService;

    /**
     * List job emails.
     *
     * @return the response entity
     */
    @GetMapping()
    public ResponseEntity<List<JobEmail>> listJobEmails(){
        return ResponseEntity.ok(jobEmailService.getAll());
    }

    /**
     * Gets the job emails.
     *
     * @param name the name
     * @return the job emails
     */
    @GetMapping("emails/{name}")
    public ResponseEntity<JobEmail> getJobEmails(@PathParam("name") String name)
    {
        return ResponseEntity.ok(jobEmailService.findByName(IRepository.SEPARATOR + name));
    }
}
