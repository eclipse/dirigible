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
package org.eclipse.dirigible.database.ds.model;

import java.util.Set;

/**
 * The Class DataStructureTableIndexModel.
 */
public class DataStructureTableIndexModel {

    /** The name. */
    private String name ;
    
    /** The index type. */
    private String indexType;
    
    /** The order. */
    private String order;
    
    /** The unique. */
    private Boolean unique;
    
    /** The index columns. */
    private Set<String> indexColumns;

    /**
     * Instantiates a new data structure table index model.
     */
    public DataStructureTableIndexModel() {
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the index type.
     *
     * @return the index type
     */
    public String getIndexType() {
        return indexType;
    }

    /**
     * Sets the index type.
     *
     * @param indexType the new index type
     */
    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    /**
     * Gets the order.
     *
     * @return the order
     */
    public String getOrder() {
        return order;
    }

    /**
     * Sets the order.
     *
     * @param order the new order
     */
    public void setOrder(String order) {
        this.order = order;
    }

    /**
     * Checks if is unique.
     *
     * @return the boolean
     */
    public Boolean isUnique() {
        return unique;
    }

    /**
     * Sets the unique.
     *
     * @param unique the new unique
     */
    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    /**
     * Gets the index columns.
     *
     * @return the index columns
     */
    public Set<String> getIndexColumns() {
        return indexColumns;
    }

    /**
     * Sets the index columns.
     *
     * @param indexColumns the new index columns
     */
    public void setIndexColumns(Set<String> indexColumns) {
        this.indexColumns = indexColumns;
    }
}
