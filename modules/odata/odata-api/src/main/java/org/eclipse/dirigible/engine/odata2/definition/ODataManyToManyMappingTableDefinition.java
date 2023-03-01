/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.definition;

/**
 * The Class ODataManyToManyMappingTableDefinition.
 */
public class ODataManyToManyMappingTableDefinition {

    /** The mapping table name. */
    private String mappingTableName;

    /** The mapping table join column. */
    private String mappingTableJoinColumn;

    /**
     * Gets the mapping table name.
     *
     * @return the mapping table name
     */
    public String getMappingTableName() {
        return mappingTableName;
    }

    /**
     * Sets the mapping table name.
     *
     * @param mappingTableName the new mapping table name
     */
    public void setMappingTableName(String mappingTableName) {
        this.mappingTableName = mappingTableName;
    }

    /**
     * Gets the mapping table join column.
     *
     * @return the mapping table join column
     */
    public String getMappingTableJoinColumn() {
        return mappingTableJoinColumn;
    }

    /**
     * Sets the mapping table join column.
     *
     * @param mappingTableJoinColumn the new mapping table join column
     */
    public void setMappingTableJoinColumn(String mappingTableJoinColumn) {
        this.mappingTableJoinColumn = mappingTableJoinColumn;
    }
}
