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
 * One {@code refuse:} entry of a {@link ReversalIntent}: a condition over the ORIGINAL record (one
 * comparison or an ANDed list, the {@code forbidWhen} vocabulary over the record's own fields and
 * to-ones) that refuses the reversal with {@link #message}. Decided under a row lock on the
 * original, so two concurrent clicks cannot both pass it.
 */
public class ReversalRefusalIntent {

    /** The condition - a comparison string or a list of them. */
    private Object when;

    /** The message the refused caller receives. */
    private String message;

    public Object getWhen() {
        return when;
    }

    public void setWhen(Object when) {
        this.when = when;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
