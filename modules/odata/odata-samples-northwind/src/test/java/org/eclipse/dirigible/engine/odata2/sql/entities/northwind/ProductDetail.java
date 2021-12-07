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

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty.Multiplicity;

@EdmEntityType(name = "ProductDetail")
@EdmEntitySet(name = "ProductDetails", container = "DemoService")
public class ProductDetail {

	@EdmKey
	@EdmProperty(name = "ProductID", facets = @EdmFacets(nullable = false))
	private Integer productId;

	@EdmProperty
	private String details;

	@EdmNavigationProperty( //
			toMultiplicity = Multiplicity.ZERO_OR_ONE, //
			toType = Product.class, //
			toRole = "Product_ProductDetail", //
			association = INorthwindODataAssociations.Product_ProductDetail_ProductDetail_Product //
	)
	private Product product;
}
