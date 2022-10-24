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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "DIRIGIBLE_JOBS")
public class Job extends Artefact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JOB_ID", nullable = false)
    private Long id;

    /**
     * The group.
     */
    @Column(name = "JOB_GROUP", columnDefinition = "VARCHAR", nullable = false, length = 255)
    private String group;

    /**
     * The clazz.
     */
    @Column(name = "JOB_CLASS", columnDefinition = "VARCHAR", nullable = false, length = 255)
    private String clazz = "";

    /**
     * The expression.
     */
    @Column(name = "JOB_EXPRESSION", columnDefinition = "VARCHAR", nullable = false, length = 255)
    private String expression;

    /**
     * The handler.
     */
    @Column(name = "JOB_HANDLER", columnDefinition = "VARCHAR", nullable = true, length = 255)
    private String handler;

    /**
     * The engine.
     */
    @Column(name = "JOB_ENGINE", columnDefinition = "VARCHAR", nullable = true, length = 100)
    private String engine;

    /**
     * The singleton.
     */
    @Column(name = "JOB_SINGLETON", columnDefinition = "BOOLEAN", nullable = false)
    private boolean singleton = false;

    /**
     * The enabled.
     */
    @Column(name = "JOB_ENABLED", columnDefinition = "BOOLEAN", nullable = false)
    private boolean enabled = true;

    /**
     * The parameters.
     */
//    @Transient
//    private List<JobParameter> parameters = new ArrayList<>();

    /** The status. */
    @Column(name = "JOB_STATUS", columnDefinition = "SMALLINT", nullable = true)
    private short status = 99;

    /**
     * The message.
     */
    @Column(name = "JOBLOG_MESSAGE", columnDefinition = "VARCHAR", nullable = true, length = 2000)
    private String message;

    /**
     * The executed at.
     */
    @Column(name = "JOB_EXECUTED_AT", columnDefinition = "TIMESTAMP", nullable = true)
    private Timestamp executedAt;

    public Job(String location, String name, String type, String description, String dependencies, Long id, String group, String clazz, String expression, String handler, String engine, boolean singleton, boolean enabled, short status, String message, Timestamp executedAt) {
        super(location, name, type, description, dependencies);
        this.id = id;
        this.group = group;
        this.clazz = clazz;
        this.expression = expression;
        this.handler = handler;
        this.engine = engine;
        this.singleton = singleton;
        this.enabled = enabled;
        this.status = status;
        this.message = message;
        this.executedAt = executedAt;
    }

    public Job() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(Timestamp executedAt) {
        this.executedAt = executedAt;
    }

    @Override
    public String toString() {
        return "Job{" + "id=" + id + ", group='" + group + '\'' + ", clazz='" + clazz + '\'' + ", expression='"
                + expression + '\'' + ", handler='" + handler + '\'' + ", engine='" + engine + '\'' + ", singleton="
                + singleton + ", enabled=" + enabled + ", status=" + status + ", message='" + message + '\'' + ", executedAt="
                + executedAt + ", location='" + location + '\'' + ", name='" + name + '\'' + ", type='" + type + '\'' + ", description='"
                + description + '\'' + ", key='" + key + '\'' + ", dependencies='" + dependencies + '\'' + ", createdBy="
                + createdBy + ", createdAt=" + createdAt + ", updatedBy=" + updatedBy + ", updatedAt=" + updatedAt + '}';
    }
}
