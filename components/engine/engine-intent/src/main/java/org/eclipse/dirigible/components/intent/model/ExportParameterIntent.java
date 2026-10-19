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
 * One input of an {@link ExportIntent}. Over a report source it names one of the report's own
 * parameters and is passed through to it. Over an entity source it filters a field of the entity:
 * {@link #field} compared by {@link #op} with the value, converted to the field's type.
 */
public class ExportParameterIntent {

    /** The parameter name: the download's query parameter, the bean call's argument key. */
    private String name;

    /** Entity source: the filtered field, the parameter name when omitted. */
    private String field;

    /**
     * Entity source: {@code eq} (the default), {@code ne}, {@code gt}, {@code ge}, {@code lt} or
     * {@code le}.
     */
    private String op;

    /** Whether a run without the value is refused (400) rather than run unfiltered. */
    private Boolean required;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }
}
