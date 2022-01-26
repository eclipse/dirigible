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
package org.eclipse.dirigible.engine.odata2.sql.entities.northwind;

import java.util.List;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty.Multiplicity;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;

@EdmEntityType(name = "Territory")
@EdmEntitySet(name = "Territories", container = "NorthwindEntities")
public class Territory {

	@EdmKey
    @EdmProperty(name = "TerritoryID", facets = @EdmFacets(nullable = false, maxLength = 20))
    private String territoryId;

	@EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 50))
    private String territoryDescription;

	@EdmProperty(name = "RegionID", facets = @EdmFacets(nullable = false))
    private Integer regionId;

	@EdmNavigationProperty( //
			toMultiplicity = Multiplicity.MANY, //
			toType = Territory.class, //
			toRole = "Employees", //
			association = "EmployeeTerritories" //
	)
	private List<Employee> employees;

	@EdmNavigationProperty( //
			toMultiplicity = Multiplicity.ONE, //
			toType = Region.class, //
			toRole = "Region", //
			association = "FK_Territories_Region" //
	)
	private Region region;
}
