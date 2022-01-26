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

@EdmEntityType(name = "Customer")
@EdmEntitySet(name = "Customers", container = "NorthwindEntities")
public class Customer {

	@EdmKey
	@EdmProperty(name = "CustomerID", facets = @EdmFacets(nullable = false, maxLength = 5))
	private String customerId;

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

	@EdmNavigationProperty( //
			toMultiplicity = Multiplicity.MANY, //
			toType = Order.class, //
			toRole = "Orders", //
			association = "FK_Orders_Customers" //
	)
	private List<Order> orders;

	@EdmNavigationProperty( //
			toMultiplicity = Multiplicity.MANY, //
			toType = CustomerDemographic.class, //
			toRole = "CustomerDemographics", //
			association = "CustomerCustomerDemo" //
	)
	private List<CustomerDemographic> customerDemographics;
}
