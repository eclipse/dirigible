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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty.Multiplicity;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;

@EdmEntityType
@EdmEntitySet(name = "Entities1")
public class Entity1 {

    @EdmKey
    @EdmProperty
    private String messageGuid;

    @EdmProperty(type = EdmType.DATE_TIME)
    private Date logStart;

    @EdmProperty(type = EdmType.DATE_TIME)
    private Date logEnd;

    @EdmProperty
    private String sender;

    @EdmProperty
    private String receiver;

    @EdmProperty
    private String status;

    @EdmProperty
    private String alternateWebLink;

    @EdmNavigationProperty(toMultiplicity = Multiplicity.MANY, toType = Entity2.class, association = "Entities2OfEntity1")
    private List<Entity2> entity2 = new ArrayList<Entity2>();

}
