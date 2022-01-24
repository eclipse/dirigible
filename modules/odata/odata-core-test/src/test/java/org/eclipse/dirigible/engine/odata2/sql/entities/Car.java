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
package org.eclipse.dirigible.engine.odata2.sql.entities;

import org.apache.olingo.odata2.api.annotation.edm.*;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty.Multiplicity;

import java.util.Date;
import java.util.List;

@EdmEntityType(name = "Car")
@EdmEntitySet(name = "Cars")
public class Car {
    static final String CAR_2_TOWNER_ASSOCIATION = "Car2TOwnerAssociation";
    static final String CAR_2_DRIVERS_ASSOCIATION = "Car2DriversAssociation";

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

    @EdmNavigationProperty(toMultiplicity = Multiplicity.MANY, toType = Driver.class, association = CAR_2_DRIVERS_ASSOCIATION)
    private List<Driver> drivers;

    @EdmNavigationProperty(toMultiplicity = Multiplicity.MANY, toType = Owner.class, association = CAR_2_TOWNER_ASSOCIATION)
    private List<Owner> owners;
}