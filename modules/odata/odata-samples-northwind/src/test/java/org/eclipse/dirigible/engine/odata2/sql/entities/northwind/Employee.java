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

import java.util.Date;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;

@EdmEntityType(name = "Employee")
@EdmEntitySet(name = "Person", container = "DemoService")
public class Employee extends Person {

	@EdmProperty(name = "EmployeeID", facets = @EdmFacets(nullable = false))
	private Long employeeId;

	@EdmProperty(facets = @EdmFacets(nullable = false))
	private Date hireDate;

	@EdmProperty(facets = @EdmFacets(nullable = false))
	private Float salary;
}
