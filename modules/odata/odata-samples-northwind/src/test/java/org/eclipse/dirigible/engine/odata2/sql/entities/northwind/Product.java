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
import org.apache.olingo.odata2.api.annotation.edm.EdmType;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty.Multiplicity;

@EdmEntityType(name = "Product")
@EdmEntitySet(name = "Products", container = "NorthwindEntities")
public class Product {

	@EdmKey
    @EdmProperty(name = "ProductID", facets = @EdmFacets(nullable = false))
    private Integer productId;

	@EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 40))
	private String productName;

	@EdmProperty(name = "SupplierID")
    private Integer supplierId;

	@EdmProperty(name = "CategoryID")
    private Integer categoryId;

	@EdmProperty(facets = @EdmFacets(maxLength = 20))
	private String quantityPerUnit;

	@EdmProperty(type = EdmType.DECIMAL,facets = @EdmFacets(precision = 19, scale = 4))
	private Double unitPrice;

	@EdmProperty
	private Short unitsInStock;

	@EdmProperty
	private Short unitsOnOrder;

	@EdmProperty
	private Short reorderLevel;

	@EdmProperty(facets = @EdmFacets(nullable = false))
	private Boolean discontinued;

	@EdmNavigationProperty( //
			toMultiplicity = Multiplicity.ZERO_OR_ONE, //
			toType = Category.class, //
			toRole = "Categories", //
			association = "FK_Products_Categories" //
	)
	private Category category;

	@EdmNavigationProperty( //
			name = "Order_Details", //
			toMultiplicity = Multiplicity.MANY, //
			toType = OrderDetail.class, //
			toRole = "Order_Details", //
			association = "FK_Order_Details_Products" //
	)
    private List<OrderDetail> orderDetails;

	@EdmNavigationProperty( //
			toMultiplicity = Multiplicity.ZERO_OR_ONE, //
			toType = Supplier.class, //
			toRole = "Suppliers", //
			association = "FK_Products_Suppliers" //
	)
	private Supplier supplier;
}
