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
package org.eclipse.dirigible.engine.odata2.sql.api;

import org.apache.olingo.odata2.api.edm.*;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBinding;

public class SQLStatementParam {

    private final Object value;
    private final EdmType edmType;
    private final EdmTableBinding.ColumnInfo columnInfo;

    public enum TemporalType {
        DATE, TIME, TIMESTAMP
    }
    public SQLStatementParam(Object value, EdmProperty edmProperty, EdmTableBinding.ColumnInfo columnInfo) throws EdmException {
        this.value = value;
        this.edmType = edmProperty.getType();
        this.columnInfo = columnInfo;
    }


    public SQLStatementParam(Object value, EdmType edmType, EdmTableBinding.ColumnInfo columnInfo) {
        this.value = value;
        this.edmType = edmType;
        this.columnInfo = columnInfo;
    }

    public boolean isTemporalType() {
        return getTemporalType() != null;
    }

    public TemporalType getTemporalType() {
        EdmType edmType = getEdmType();
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

    public EdmType getEdmType() {
        return edmType;
    }

    public String getSqlType() {
        return columnInfo == null ? null: columnInfo.getJdbcType();
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        return (T) value;
    }
}
