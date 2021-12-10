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
package org.eclipse.dirigible.engine.odata2.sql.entities.northwind;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;

@EdmEntityType(name = "Shipper")
@EdmEntitySet(name = "Shippers", container = "NorthwindEntities")
public class Shipper {

	@EdmKey
    @EdmProperty(name = "ShipperID", facets = @EdmFacets(nullable = false))
    private Integer shipperId;

	@EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 40))
    private String companyName;

	@EdmProperty(facets = @EdmFacets(maxLength = 24))
    private String phone;

}
