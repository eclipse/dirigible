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

import java.util.Date;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;

@EdmEntityType(name = "Orders_Qry")
@EdmEntitySet(name = "Orders_Qries", container = "NorthwindEntities")
public class OrdersQry {

	@EdmKey
	@EdmProperty(name = "OrderID", facets = @EdmFacets(nullable = false))
	private Integer orderId;

	@EdmProperty(name = "CustomerID", facets = @EdmFacets(maxLength = 5))
	private String customerId;

	@EdmProperty(name = "EmployeeID")
	private Integer employeeId;

	@EdmProperty(type = EdmType.DATE_TIME)
	private Date orderDate;
	
	@EdmProperty(type = EdmType.DATE_TIME)
	private Date requiredDate;

	@EdmProperty(type = EdmType.DATE_TIME)
	private Date shippedDate;

	@EdmProperty
	private Integer shipVia;

	@EdmProperty(type = EdmType.DECIMAL, facets = @EdmFacets(precision = 19, scale = 4))
	private Double freight;

	@EdmProperty(facets = @EdmFacets(maxLength = 40))
	private String shipName;

	@EdmProperty(facets = @EdmFacets(maxLength = 60))
	private String shipAddress;

	@EdmProperty(facets = @EdmFacets(maxLength = 15))
	private String shipCity;

	@EdmProperty(facets = @EdmFacets(maxLength = 15))
	private String shipRegion;

	@EdmProperty(facets = @EdmFacets(maxLength = 10))
	private String shipPostalCode;

	@EdmProperty(facets = @EdmFacets(maxLength = 15))
	private String shipCountry;

	@EdmKey
	@EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 40))
	private String companyName;

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

}
