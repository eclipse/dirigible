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

import java.util.ArrayList;
import java.util.List;

/**
 * One record layout of an {@link ExportIntent}. A {@code line} record is written once per source
 * row; a {@code header} once before the rows and a {@code trailer} once after them, so those two
 * carry literals, parameters and totals rather than row values.
 */
public class ExportRecordIntent {

    /** {@code header}, {@code line} or {@code trailer}. */
    private String type;

    /** {@code xml}: the record's element name, its {@link #type} when omitted. */
    private String element;

    /** The fields, in the order they are written. */
    private List<ExportFieldIntent> fields = new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public List<ExportFieldIntent> getFields() {
        return fields;
    }

    public void setFields(List<ExportFieldIntent> fields) {
        this.fields = fields == null ? new ArrayList<>() : fields;
    }
}
