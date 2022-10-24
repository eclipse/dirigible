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
import org.springframework.data.annotation.Transient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The JobParameterDefinition serialization object.
 */
@Entity
@Table(name = "DIRIGIBLE_JOB_PARAMETERS")
public class JobParameter extends Artefact {

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JOBPARAM_ID", nullable = false)
    private Long id;

    /** The job name. */
    @Transient
    @Column(name = "JOBPARAM_JOB_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255)
    private String jobName;

    /** The type. */
    @Column(name = "JOBPARAM_TYPE", columnDefinition = "VARCHAR", nullable = false, length = 255)
    private String type; // string, number, boolean

    /** The default value. */
    @Column(name = "JOBPARAM_DEFAULT_VALUE", columnDefinition = "VARCHAR", nullable = true, length = 2000)
    private String defaultValue = "";

    /** The choices. */
    @Column(name = "JOBPARAM_CHOICES", columnDefinition = "VARCHAR", nullable = true, length = 2000)
    private String choices = "";

    /** The value. */
    @Transient
    @Column(name = "JOBPARAM_VALUE", columnDefinition = "VARCHAR", nullable = true, length = 2000)
    private String value;

    public JobParameter() {
        super();
    }

    public JobParameter(String location, String name, String type, String description, String dependencies, Long id, String jobName, String type1, String defaultValue, String choices, String value) {
        super(location, name, type, description, dependencies);
        this.id = id;
        this.jobName = jobName;
        this.type = type1;
        this.defaultValue = defaultValue;
        this.choices = choices;
        this.value = value;
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

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getChoices() {
        return choices;
    }

    public void setChoices(String choices) {
        this.choices = choices;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "JobParameter{" +
                "id='" + id + '\'' +
                ", jobName='" + jobName + '\'' +
                ", type='" + type + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", choices='" + choices + '\'' +
                ", value='" + value + '\'' +
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
