/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.sql.api;

import org.apache.olingo.odata2.api.edm.EdmSimpleType;
import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;

public class SQLStatementParam {

    private final Object value;
    private final EdmSimpleType edmSimpleType;
    private final String sqlType;

    public enum TemporalType {
        DATE, TIME, TIMESTAMP
    }

    public SQLStatementParam(Object value) {
        this(value, null, null);
    }

    public SQLStatementParam(Object value, String customSqlType) {
        this(value, null, customSqlType);
    }

    public SQLStatementParam(Object value, EdmSimpleType edmSimpleType, String sqlType) {
        this.value = value;
        this.edmSimpleType = edmSimpleType;
        this.sqlType = sqlType;
    }

    public boolean isTemporalType() {
        return getTemporalType() != null;
    }

    public TemporalType getTemporalType() {
        EdmSimpleType edmType = getEdmType();
        if (EdmSimpleTypeKind.DateTime.getEdmSimpleTypeInstance().equals(edmType)) {
            return TemporalType.TIMESTAMP;
        } else if (EdmSimpleTypeKind.Time.getEdmSimpleTypeInstance().equals(edmType)) {
            return TemporalType.TIME;
        } else if (EdmSimpleTypeKind.DateTimeOffset.getEdmSimpleTypeInstance().equals(edmType)) {
            return TemporalType.DATE;
        } else {
            return null;
        }
    }

    public EdmSimpleType getEdmType() {
        return edmSimpleType;
    }

    public String getSqlType() {
        return this.sqlType;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        return (T) value;
    }
}
