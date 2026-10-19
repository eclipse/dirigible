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
 * Where an issue stands. {@link IssueService} is its only writer, and it moves the issue's process
 * in the same transaction.
 */
public enum IssueStatus {

    /** Filed, waiting for an operator's triage. */
    NEW,

    /** Confirmed, being worked on. */
    OPEN,

    /** The operator asked the reporter for something; the reporter's next message reopens it. */
    WAITING_FOR_REPORTER,

    /** Resolved by an operator. */
    RESOLVED,

    /** Rejected by an operator, at triage or later. */
    REJECTED,

    /** Withdrawn by the reporter. */
    CANCELLED;

    /**
     * Whether nothing can happen to the issue any more.
     *
     * @return whether the issue is closed
     */
    boolean isClosed() {
        return this == RESOLVED || this == REJECTED || this == CANCELLED;
    }
}
