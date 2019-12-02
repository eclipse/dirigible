/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p>
 * Contributors:
 * SAP - initial API and implementation
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

    public PersistenceTableRelationModel() {
    }

    public PersistenceTableRelationModel(String fromTableName, String toTableName, String fkColumnName, String pkColumnName) {
        this.fromTableName = fromTableName;
        this.toTableName = toTableName;
        this.fkColumnName = fkColumnName;
        this.pkColumnName = pkColumnName;
    }

    /**
     * Gets the name of the table with the foreign key.
     *
     *            the new columns
     */
    public String getFromTableName() {
        return fromTableName;
    }

    /**
     * Sets the name of the table with the foreign key.
     *
     * @param fromTableName
     *
     */
    public void setFromTableName(String fromTableName) {
        this.fromTableName = fromTableName;
    }

    /**
     * Gets the name of the table with the primary key.
     *
     *
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
     *
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
     *
     *
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
}

