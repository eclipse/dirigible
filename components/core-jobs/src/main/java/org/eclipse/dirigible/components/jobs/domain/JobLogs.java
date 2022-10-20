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
import java.sql.Timestamp;
import javax.persistence.*;

/**
 * The JobLogDefinition serialization object.
 */
@Entity
@Table(name = "DIRIGIBLE_JOB_LOGS")
public class JobLogs extends Artefact {

    /** The Constant JOB_LOG_STATUS_TRIGGRED. */
    public static final short JOB_LOG_STATUS_TRIGGRED = 0;

    /** The Constant JOB_LOG_STATUS_FINISHED. */
    public static final short JOB_LOG_STATUS_FINISHED = 1;

    /** The Constant JOB_LOG_STATUS_FAILED. */
    public static final short JOB_LOG_STATUS_FAILED = -1;

    /** The Constant JOB_LOG_STATUS_LOGGED. */
    public static final short JOB_LOG_STATUS_LOGGED = 2;

    /** The Constant JOB_LOG_STATUS_ERROR. */
    public static final short JOB_LOG_STATUS_ERROR = 3;

    /** The Constant JOB_LOG_STATUS_WARN. */
    public static final short JOB_LOG_STATUS_WARN = 4;

    /** The Constant JOB_LOG_STATUS_INFO. */
    public static final short JOB_LOG_STATUS_INFO = 5;

    /** The Constant JOB_LOG_STATUS_UNKNOWN. */
    public static final short JOB_LOG_STATUS_UNKNOWN = 99;

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JOB_LOG_ID", nullable = false)
    private long id;

    /** The handler. */
    @Column(name = "JOBLOG_HANDLER", columnDefinition = "VARCHAR", nullable = false, length = 255)
    private String handler;

    /** The started at. */
    @Column(name = "JOBLOG_TRIGGERED_AT", columnDefinition = "TIMESTAMP", nullable = true)
    private Timestamp triggeredAt;

    /** The triggered id. */
    @Column(name = "JOBLOG_TRIGGERED_ID", columnDefinition = "BIGINT", nullable = true)
    private long triggeredId;

    /** The finished at. */
    @Column(name = "JOBLOG_FINISHED_AT", columnDefinition = "TIMESTAMP", nullable = true)
    private Timestamp finishedAt;

    /**  The status. */
    @Column(name = "JOBLOG_STATUS", columnDefinition = "SMALLINT", nullable = false)
    private short status;

    /** The message. */
    @Column(name = "JOBLOG_MESSAGE", columnDefinition = "VARCHAR", nullable = true, length = 2000)
    private String message;

    public JobLogs() {
        super();
    }

    public JobLogs(String location, String name, String type, String description, String dependencies, long id, String handler, Timestamp triggeredAt, long triggeredId, Timestamp finishedAt, short status, String message) {
        super(location, name, type, description, dependencies);
        this.id = id;
        this.handler = handler;
        this.triggeredAt = triggeredAt;
        this.triggeredId = triggeredId;
        this.finishedAt = finishedAt;
        this.status = status;
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public Timestamp getTriggeredAt() {
        return triggeredAt;
    }

    public void setTriggeredAt(Timestamp triggeredAt) {
        this.triggeredAt = triggeredAt;
    }

    public long getTriggeredId() {
        return triggeredId;
    }

    public void setTriggeredId(long triggeredId) {
        this.triggeredId = triggeredId;
    }

    public Timestamp getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Timestamp finishedAt) {
        this.finishedAt = finishedAt;
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

    @Override
    public String toString() {
        return "JobLogs{" +
                "id=" + id +
                ", handler='" + handler + '\'' +
                ", triggeredAt=" + triggeredAt +
                ", triggeredId=" + triggeredId +
                ", finishedAt=" + finishedAt +
                ", status=" + status +
                ", message='" + message + '\'' +
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
