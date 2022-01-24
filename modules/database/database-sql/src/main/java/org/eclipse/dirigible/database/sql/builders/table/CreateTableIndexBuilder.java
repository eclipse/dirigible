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
package org.eclipse.dirigible.database.sql.builders.table;

import org.eclipse.dirigible.database.sql.ISqlDialect;

import java.util.Set;

public class CreateTableIndexBuilder extends AbstractCreateTableConstraintBuilder<CreateTableIndexBuilder> {

    private String indexType;

    private String order;

    private Boolean unique;

    private Set<String> indexColumns ;

    /**
     * Instantiates a new abstract create table constraint builder.
     *
     * @param dialect the dialect
     * @param name
     */
    public CreateTableIndexBuilder(ISqlDialect dialect, String name) {
        super(dialect, name);
    }

    public String getIndexType() {
        return indexType;
    }

    public String getOrder() {
        return order;
    }

    public Boolean isUnique() {
        return unique;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    public Set<String> getIndexColumns() {
        return indexColumns;
    }

    public void setIndexColumns(Set<String> indexColumns) {
        this.indexColumns = indexColumns;
    }
}
