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
@EdmEntitySet(name = "Products", container = "DemoService")
public class Product {

    @EdmKey
    @EdmProperty(name = "ID", facets = @EdmFacets(nullable = false))
    private Integer id;

    @EdmProperty(facets = @EdmFacets(nullable = false))
    private String name;

    @EdmProperty
    private String description;

    @EdmProperty(facets = @EdmFacets(nullable = false))
    private Date releaseDate = null;

    @EdmProperty
    private Date discontinuedDate;

    @EdmProperty(type = EdmType.INT16, facets = @EdmFacets(nullable = false))
    private Integer rating;

    @EdmProperty(facets = @EdmFacets(nullable = false))
    private Double price;

    @EdmNavigationProperty( //
			toMultiplicity = Multiplicity.MANY, //
			toType = Category.class, //
			toRole = "Category_Products", //
			association = INorthwindODataAssociations.Product_Categories_Category_Products //
	)
    private List<Category> categories;

    @EdmNavigationProperty( //
			toMultiplicity = Multiplicity.ZERO_OR_ONE, //
			toType = Supplier.class, //
			toRole = "Supplier_Products", //
			association = INorthwindODataAssociations.Product_Supplier_Supplier_Products //
	)
    private Supplier supplier;

    @EdmNavigationProperty( //
			toMultiplicity = Multiplicity.ZERO_OR_ONE, //
			toType = ProductDetail.class, //
			toRole = "ProductDetail_Product", //
			association = INorthwindODataAssociations.Product_ProductDetail_ProductDetail_Product //
	)
    private ProductDetail productDetail;
}
