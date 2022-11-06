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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.Transient;

import javax.persistence.*;

/**
 * The JobParameterDefinition serialization object.
 */
@Entity
@Table(name = "DIRIGIBLE_JOB_PARAMETERS")
public class JobParameter extends Artefact {

    /** The Constant ARTEFACT_TYPE. */
    public static final String ARTEFACT_TYPE = "jobParameter";

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

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "JOB_ID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Job job;

    /**
     * Instantiates a new job parameter.
     */
    public JobParameter() {
        super();
    }

    /**
     * Instantiates a new job parameter.
     *
     * @param location the location
     * @param name the name
     * @param type the type
     * @param description the description
     * @param dependencies the dependencies
     * @param id the id
     * @param jobName the job name
     * @param type1 the type 1
     * @param defaultValue the default value
     * @param choices the choices
     * @param value the value
     */
    public JobParameter(String location, String name, String type, String description, String dependencies, Long id, String jobName, String type1, String defaultValue, String choices, String value, Job job) {
        super(location, name, type, description, dependencies);
        this.id = id;
        this.jobName = jobName;
        this.type = type1;
        this.defaultValue = defaultValue;
        this.choices = choices;
        this.value = value;
        this.job = job;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the job name.
     *
     * @return the job name
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * Sets the job name.
     *
     * @param jobName the new job name
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    @Override
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the default value.
     *
     * @return the default value
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the default value.
     *
     * @param defaultValue the new default value
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Gets the choices.
     *
     * @return the choices
     */
    public String getChoices() {
        return choices;
    }

    /**
     * Sets the choices.
     *
     * @param choices the new choices
     */
    public void setChoices(String choices) {
        this.choices = choices;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value.
     *
     * @param value the new value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the job.
     *
     * @return the job
     */
    public Job getJob() {
        return job;
    }

    /**
     * Sets the job.
     *
     * @param job the new job
     */
    public void setJob(Job job) {
        this.job = job;
    }

    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "JobParameter{" +
                "id=" + id +
                ", jobName='" + jobName + '\'' +
                ", type='" + type + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", choices='" + choices + '\'' +
                ", value='" + value + '\'' +
                ", job=" + job +
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
