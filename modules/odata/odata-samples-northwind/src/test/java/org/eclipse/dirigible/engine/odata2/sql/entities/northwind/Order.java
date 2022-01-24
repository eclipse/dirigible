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
import java.util.List;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty.Multiplicity;

@EdmEntityType(name = "Order")
@EdmEntitySet(name = "Orders", container = "NorthwindEntities")
public class Order {

	@EdmKey
    @EdmProperty(name = "OrderID", facets = @EdmFacets(nullable = false))
    private Integer orderId;

	@EdmProperty(name = "CustomerID", facets = @EdmFacets(maxLength = 5))
	private String customerId;

	@EdmProperty(name = "EmployeeID")
	private Integer employeeId;

	@EdmProperty
	private Date orderDate;

	@EdmProperty
	private Date requiredDate;

	@EdmProperty
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

	@EdmNavigationProperty( //
			toMultiplicity = Multiplicity.ZERO_OR_ONE, //
			toType = Customer.class, //
			toRole = "Customers", //
			association = "FK_Orders_Customers" //
	)
	private Customer customer;

	@EdmNavigationProperty( //
			toMultiplicity = Multiplicity.ZERO_OR_ONE, //
			toType = Employee.class, //
			toRole = "Employees", //
			association = "FK_Orders_Employees" //
	)
	private Employee employee;

	@EdmNavigationProperty( //
			name = "Order_Details", //
			toMultiplicity = Multiplicity.MANY, //
			toType = OrderDetail.class, //
			toRole = "Order_Details", //
			association = "FK_Order_Details_Orders" //
	)
    private List<OrderDetail> orderDetails;

	@EdmNavigationProperty( //
			toMultiplicity = Multiplicity.ZERO_OR_ONE, //
			toType = Shipper.class, //
			toRole = "Shippers", //
			association = "FK_Orders_Shippers" //
	)
	private Shipper shipper;
}
