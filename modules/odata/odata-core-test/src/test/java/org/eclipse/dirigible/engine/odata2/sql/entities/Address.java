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

import static org.eclipse.dirigible.engine.odata2.sql.entities.Owner.OWNER_2_ADDRESS_ASSOCIATION;


@EdmEntityType(name = "Address")
@EdmEntitySet(name = "Addresses")
public class Address {


    @EdmKey
    @EdmProperty
    private String id;

    @EdmProperty
    private String street;

    @EdmProperty
    private String city;

    @EdmProperty
    private Integer postalCode;


    @EdmNavigationProperty(toMultiplicity = EdmNavigationProperty.Multiplicity.ZERO_OR_ONE, toType = Owner.class, association = OWNER_2_ADDRESS_ASSOCIATION)
    private Owner owner;

}
