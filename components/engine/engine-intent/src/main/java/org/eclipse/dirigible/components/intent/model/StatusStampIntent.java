/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.intent.model;

/**
 * A field's {@code stampOn: { status: X }} (dirigible #7874): the {@code timestamp} / {@code date}
 * field records when the record ENTERED status {@code X}.
 *
 * <pre>
 * fields:
 *   - { name: approvedAt, type: timestamp, stampOn: { status: APPROVED } }
 * </pre>
 *
 * The generated repository writes the moment in the same statement as every move into the status,
 * whoever performs it - a workflow setter, a transition button, a full update, a create that lands
 * there. A write that leaves the status where it was does not re-stamp, re-entering it records the
 * latest arrival, and leaving it keeps the value. The status is always the entity's
 * {@code function: EntityStatus} relation, as for {@code lifecycle:}, so no relation is named.
 *
 * <p>
 * The status is referenced by its seeded name or by its seed id; {@code StatusSymbolResolver}
 * rewrites a name to the id before the typed mapping, so {@link #status} is always the id.
 */
public class StatusStampIntent {

    /** The seed id of the status whose arrival the field records. */
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
