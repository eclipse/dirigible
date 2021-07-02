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
package org.eclipse.dirigible.engine.odata2.sql.edm;

import org.apache.olingo.odata2.api.annotation.edm.*;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty.Multiplicity;

import java.util.ArrayList;
import java.util.List;

@EdmEntityType
@EdmEntitySet(name = "Entities2")
public class Entity2 {

    @EdmKey
    @EdmProperty
    private Long id;

    @EdmProperty
    private String name;

    @EdmProperty
    private String value;

    @EdmNavigationProperty(toMultiplicity = Multiplicity.ZERO_OR_ONE, toType = Entity1.class, association = "Entities2OfEntity1")
    private Entity1 entity1;

    @EdmNavigationProperty(toMultiplicity = Multiplicity.MANY, toType = Entity3.class, association = "Entities3OfEntity2")
    private List<Entity3> entity3 = new ArrayList<Entity3>();

}
