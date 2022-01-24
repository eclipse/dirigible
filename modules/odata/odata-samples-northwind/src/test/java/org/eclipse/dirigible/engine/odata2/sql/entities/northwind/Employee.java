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

@EdmEntityType(name = "Employee")
@EdmEntitySet(name = "Employees", container = "NorthwindEntities")
public class Employee {

	@EdmKey
	@EdmProperty(name = "EmployeeID", facets = @EdmFacets(nullable = false))
	private Integer employeeId;

	@EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 20))
	private String lastName;

	@EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 10))
	private String firstName;

	@EdmProperty(facets = @EdmFacets(maxLength = 30))
	private String title;

	@EdmProperty(facets = @EdmFacets(maxLength = 25))
	private String titleOfCourtesy;

	@EdmProperty(type = EdmType.DATE_TIME)
	private Date birthDate;

	@EdmProperty(type = EdmType.DATE_TIME)
	private Date hireDate;

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
	private String homePhone;

	@EdmProperty(facets = @EdmFacets(maxLength = 4))
	private String extension;

	@EdmProperty(type = EdmType.BINARY)
	private String photo;

	@EdmProperty
	private String notes;

	@EdmProperty
	private Integer reportsTo;

	@EdmProperty(facets = @EdmFacets(maxLength = 255))
	private String photoPath;

	@EdmNavigationProperty( //
			name = "Employees1", //
			toMultiplicity = Multiplicity.MANY, //
			toType = Employee.class, //
			toRole = "Employees" //
	)
	private List<Employee> employees;

	@EdmNavigationProperty( //
			name = "Employee1", //
			toMultiplicity = Multiplicity.MANY, //
			toType = Employee.class, //
			toRole = "Employees1" //
	)
	private List<Employee> employees1;

	@EdmNavigationProperty( //
			toMultiplicity = Multiplicity.MANY, //
			toType = Order.class, //
			toRole = "Orders", //
			association = "FK_Orders_Employees" //
	)
	private List<Order> orders;

	@EdmNavigationProperty( //
			toMultiplicity = Multiplicity.MANY, //
			toType = Territory.class, //
			toRole = "Territories", //
			association = "EmployeeTerritories" //
	)
	private List<Territory> territories;
}
