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
import org.eclipse.dirigible.components.jobs.domain.JobEmailDefinition;
import org.eclipse.dirigible.components.jobs.service.JobEmailDefinitionService;
import org.eclipse.dirigible.repository.api.IRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.ws.rs.PathParam;
import java.util.List;

@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_CORE + "jobEmailDefinition")
public class JobEmailDefinitionEndpoint extends BaseEndpoint {

    @Autowired
    private JobEmailDefinitionService jobEmailDefinitionService;

    @GetMapping()
    public ResponseEntity<List<JobEmailDefinition>> listJobEmailDefinitions(){
        return ResponseEntity.ok(jobEmailDefinitionService.getAll());
    }

    @GetMapping("emails/{name}")
    public ResponseEntity<JobEmailDefinition> getJobEmails(@PathParam("name") String name)
    {
        return ResponseEntity.ok(jobEmailDefinitionService.findByName(IRepository.SEPARATOR + name));
    }
}
