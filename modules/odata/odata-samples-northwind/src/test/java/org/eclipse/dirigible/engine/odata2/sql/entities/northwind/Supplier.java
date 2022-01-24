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
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty.Multiplicity;

@EdmEntityType(name = "Supplier")
@EdmEntitySet(name = "Suppliers", container = "NorthwindEntities")
public class Supplier {

	@EdmKey
    @EdmProperty(name = "SupplierID", facets = @EdmFacets(nullable = false))
    private Integer supplierId;

	@EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 40))
    private String companyName;

	@EdmProperty(facets = @EdmFacets(maxLength = 30))
    private String contactName;

	@EdmProperty(facets = @EdmFacets(maxLength = 30))
    private String contactTitle;

	@EdmProperty(facets = @EdmFacets(maxLength = 60))
    private String address;

	@EdmProperty(facets = @EdmFacets(maxLength = 15))
    private String city;

	@EdmProperty(facets = @EdmFacets(maxLength = 15))
    private String region;

	@EdmProperty(facets = @EdmFacets(maxLength = 10))
    private String postalCode;

	@EdmProperty(facets = @EdmFacets(maxLength = 15))
    private String country;

	@EdmProperty(facets = @EdmFacets(maxLength = 24))
    private String phone;

	@EdmProperty(facets = @EdmFacets(maxLength = 24))
    private String fax;

	@EdmProperty
    private String homePage;

	@EdmNavigationProperty( //
			toMultiplicity = Multiplicity.MANY, //
			toType = Product.class, //
			toRole = "Products", //
			association = "FK_Products_Suppliers" //
	)
	private List<Product> products;
}
