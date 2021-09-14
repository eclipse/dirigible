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
package org.eclipse.dirigible.engine.odata2.sql.entities;

import java.util.Date;
import java.util.List;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty.Multiplicity;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;

@EdmEntityType(name = "Car")
@EdmEntitySet(name = "Cars")
public class Car {
    @EdmKey
    @EdmProperty
    private String id;

    @EdmProperty
    private String make;
    
    @EdmProperty
    private String model;
    
    @EdmProperty
    private Integer year;

    @EdmProperty
    private Double price;

    @EdmProperty(type = EdmType.DATE_TIME)
    private Date updated;
    
    @EdmNavigationProperty(toMultiplicity = Multiplicity.MANY, toType = Driver.class, association = "Car2DriversAssociation")
    private List<Driver> drivers;

}