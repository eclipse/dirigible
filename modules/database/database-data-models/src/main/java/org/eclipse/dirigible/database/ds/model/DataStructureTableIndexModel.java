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
package org.eclipse.dirigible.database.ds.model;

import java.util.Set;

/**
 * The Class DataStructureTableIndexModel.
 */
public class DataStructureTableIndexModel {

    /** The name. */
    private String name ;
    
    /** The index type. */
    private String type;
    
    /** The unique. */
    private Boolean unique;
    
    /** The index columns. */
    private Set<String> columns;

    /**
     * Instantiates a new data structure table index model.
     */
    public DataStructureTableIndexModel() {
    }
    
    /**
     * Instantiates a new data structure table index model.
     *
     * @param name the name
     * @param type the type
     * @param unique the unique
     * @param columns the columns
     */
    public DataStructureTableIndexModel(String name, String type, Boolean unique, Set<String> columns) {
		super();
		this.name = name;
		this.type = type;
		this.unique = unique;
		this.columns = columns;
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
    public String getType() {
        return type;
    }

    /**
     * Sets the index type.
     *
     * @param type the new type
     */
    public void setType(String type) {
        this.type = type;
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
    public Set<String> getColumns() {
        return columns;
    }

    /**
     * Sets the index columns.
     *
     * @param columns the new index columns
     */
    public void setColumns(Set<String> columns) {
        this.columns = columns;
    }
}
