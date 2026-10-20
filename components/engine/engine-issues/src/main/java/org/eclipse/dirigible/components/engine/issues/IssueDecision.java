/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.issues;

/**
 * What an operator decides about an issue, and where that leaves it. Triage confirms or rejects a
 * {@link IssueStatus#NEW new} issue; work on an {@link IssueStatus#OPEN open} one resolves it,
 * rejects it or asks the reporter for something.
 */
public enum IssueDecision {

    /** Triage: the issue is real, work on it. */
    CONFIRM(IssueStatus.OPEN),

    /** Triage or work: the issue is not one. */
    REJECT(IssueStatus.REJECTED),

    /** Work: the issue is solved. */
    RESOLVE(IssueStatus.RESOLVED),

    /** Work: the reporter must answer or do something first. */
    REQUEST(IssueStatus.WAITING_FOR_REPORTER);

    private final IssueStatus outcome;

    IssueDecision(IssueStatus outcome) {
        this.outcome = outcome;
    }

    /**
     * The status the decision leaves the issue in.
     *
     * @return the status
     */
    IssueStatus outcome() {
        return outcome;
    }

    /**
     * The task an issue in a status waits in for this decision.
     *
     * @param current the issue's status
     * @return the task key, {@code null} when the decision cannot be taken in that status
     */
    String taskFor(IssueStatus current) {
        if (current == IssueStatus.NEW && (this == CONFIRM || this == REJECT)) {
            return IssueLifecycle.TRIAGE_TASK;
        }
        if (current == IssueStatus.OPEN && this != CONFIRM) {
            return IssueLifecycle.WORK_TASK;
        }
        return null;
    }
}
