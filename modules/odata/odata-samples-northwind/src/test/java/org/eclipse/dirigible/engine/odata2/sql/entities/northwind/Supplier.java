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

import java.util.List;

import org.apache.olingo.odata2.api.annotation.edm.EdmConcurrencyControl;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty.Multiplicity;

@EdmEntityType(name = "Supplier")
@EdmEntitySet(name = "Suppliers",container = "DemoService")
public class Supplier {

	@EdmKey
	@EdmProperty(name = "ID", facets = @EdmFacets(nullable = false))
	private Integer id;

	@EdmProperty
	private String name;

	@EdmProperty
	private Address address;

	@EdmProperty(facets = @EdmFacets(nullable = false))
	@EdmConcurrencyControl
	private Integer concurrency;

	@EdmNavigationProperty( //
			toMultiplicity = Multiplicity.MANY, //
			toType = Category.class, //
			toRole = "Product_Supplier", //
			association = INorthwindODataAssociations.Product_Supplier_Supplier_Products //
	)
	private List<Product> products;
}
