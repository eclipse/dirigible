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
import org.eclipse.dirigible.components.jobs.domain.JobLog;
import org.eclipse.dirigible.components.jobs.service.JobLogService;
import org.eclipse.dirigible.repository.api.IRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.ws.rs.PathParam;
import java.util.List;

@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_CORE + "jobLog")
public class JobLogEndpoint extends BaseEndpoint {

    @Autowired
    private JobLogService jobLogService;

    @GetMapping()
    public ResponseEntity<List<JobLog>> listJobLog(){
        return ResponseEntity.ok(jobLogService.getAll());
    }

    @GetMapping("logs/{name}")
    public ResponseEntity getJobLog(@PathParam("name") String name)
    {
        return ResponseEntity.ok(jobLogService.findByName(IRepository.SEPARATOR + name));
    }

    @PostMapping("clear/{name}")
    public void clearJobLog(@PathParam("name") String name)
    {
        //TODO
        jobLogService.deleteJobByName(IRepository.SEPARATOR + name);
    }
}
