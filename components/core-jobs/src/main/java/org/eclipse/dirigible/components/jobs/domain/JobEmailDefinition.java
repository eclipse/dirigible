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
package org.eclipse.dirigible.components.jobs.domain;

import org.eclipse.dirigible.components.base.artefact.Artefact;
import javax.persistence.*;

@Entity
@Table(name = "DIRIGIBLE_JOB_EMAILS")
public class JobEmailDefinition extends Artefact {

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JOBEMAIL_ID", nullable = false)
    private Long id;

    /** The job name. */
    @Column(name = "JOBEMAIL_JOB_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255)
    private String jobName;

    /** The name. */
    @Column(name = "JOBEMAIL_EMAIL", columnDefinition = "VARCHAR", nullable = false, length = 255)
    private String email;

    public JobEmailDefinition() {
        super();
    }

    public JobEmailDefinition(String location, String name, String type, String description, String dependencies, Long id, String jobName, String email) {
        super(location, name, type, description, dependencies);
        this.id = id;
        this.jobName = jobName;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "JobEmailDefinition{" +
                "id=" + id +
                ", jobName='" + jobName + '\'' +
                ", email='" + email + '\'' +
                ", location='" + location + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", key='" + key + '\'' +
                ", dependencies='" + dependencies + '\'' +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                ", updatedBy=" + updatedBy +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
