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

import org.apache.olingo.odata2.api.annotation.edm.*;

import java.util.Calendar;

import static org.eclipse.dirigible.engine.odata2.sql.entities.Car.CAR_2_TOWNER_ASSOCIATION;

@EdmEntityType(name = "Owner")
@EdmEntitySet(name = "Owners")
public class Owner {
    @EdmKey
    @EdmProperty
    private String id;

    @EdmProperty
    private String firstName;

    @EdmProperty
    private String lastName;

    @EdmNavigationProperty(toMultiplicity = EdmNavigationProperty.Multiplicity.ZERO_OR_ONE, toType = Car.class, association = CAR_2_TOWNER_ASSOCIATION)
    private Car car;

    @EdmProperty(type = EdmType.DATE_TIME)
    private Calendar purchaseDate;

}


