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
package org.eclipse.dirigible.database.persistence.model;

/**
 * The relation element of the persistence model transport object.
 */
public class PersistenceTableRelationModel {

    private String fromTableName;

    private String toTableName;

    private String fkColumnName;

    private String pkColumnName;
    
    private String fkName;
    
    private String pkName;

    public PersistenceTableRelationModel() {
    }

    public PersistenceTableRelationModel(String fromTableName, String toTableName, String fkColumnName, String pkColumnName, String fkName, String pkName) {
        this.fromTableName = fromTableName;
        this.toTableName = toTableName;
        this.fkColumnName = fkColumnName;
        this.pkColumnName = pkColumnName;
        this.fkName = fkName;
        this.pkName = pkName;
    }

    /**
     * Gets the name of the table with the foreign key.
     * <p>
     * the new columns
     */
    public String getFromTableName() {
        return fromTableName;
    }

    /**
     * Sets the name of the table with the foreign key.
     *
     * @param fromTableName
     */
    public void setFromTableName(String fromTableName) {
        this.fromTableName = fromTableName;
    }

    /**
     * Gets the name of the table with the primary key.
     */
    public String getToTableName() {
        return toTableName;
    }

    /**
     * Sets the name of the table with the primary key.
     *
     * @param toTableName
     */
    public void setToTableName(String toTableName) {
        this.toTableName = toTableName;
    }

    /**
     * Gets the name of the foreign key column.
     */
    public String getFkColumnName() {
        return fkColumnName;
    }

    /**
     * Sets the name of the foreign key column.
     *
     * @param fkColumnName
     */
    public void setFkColumnName(String fkColumnName) {
        this.fkColumnName = fkColumnName;
    }

    /**
     * Gets the name of the primary key column.
     */
    public String getPkColumnName() {
        return pkColumnName;
    }

    /**
     * Sets the name of the primary key column.
     *
     * @param pkColumnName
     */
    public void setPkColumnName(String pkColumnName) {
        this.pkColumnName = pkColumnName;
    }
    
    /**
     * Gets the name of the foreign key.
     */
    public String getFkName() {
        return fkName;
    }

    /**
     * Sets the name of the foreign key.
     *
     * @param fkName
     */
    public void setFkName(String fkName) {
        this.fkName = fkName;
    }

    /**
     * Gets the name of the primary key.
     */
    public String getPkName() {
        return pkName;
    }

    /**
     * Sets the name of the primary key.
     *
     * @param pkName
     */
    public void setPkName(String pkName) {
        this.pkName = pkName;
    }
}

