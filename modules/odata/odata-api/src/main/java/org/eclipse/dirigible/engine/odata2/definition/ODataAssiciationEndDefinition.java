/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.definition;

import java.util.ArrayList;

public class ODataAssiciationEndDefinition {

    private String entity;

    private ArrayList<String> property;

    private String column;

    private String multiplicity;

    /**
     * @return the entity
     */
    public String getEntity() {
        return entity;
    }

    /**
     * @param entity the entity to set
     */
    public void setEntity(String entity) {
        this.entity = entity;
    }

    /**
     * @return the property
     */
    public ArrayList<String> getProperty() {
        return property;
    }

    /**
     * @param property the property to set
     */
    public void setProperty(ArrayList<String> property) {
        this.property = property;
    }

    /**
     * @return the column
     */
    public String getColumn() {
        return column;
    }

    /**
     * @param column the column to set
     */
    public void setColumn(String column) {
        this.column = column;
    }

    /**
     * @return the multiplicity
     */
    public String getMultiplicity() {
        return multiplicity;
    }

    /**
     * @param multiplicity the multiplicity to set
     */
    public void setMultiplicity(String multiplicity) {
        this.multiplicity = multiplicity;
    }


}
