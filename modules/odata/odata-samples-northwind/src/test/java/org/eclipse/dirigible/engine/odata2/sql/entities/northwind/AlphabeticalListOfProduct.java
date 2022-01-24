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

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;


@EdmEntityType(name = "Alphabetical_list_of_product")
@EdmEntitySet(name = "Alphabetical_list_of_products", container = "NorthwindEntities")
public class AlphabeticalListOfProduct {

	@EdmKey
    @EdmProperty(name = "ProductID", facets = @EdmFacets(nullable = false))
	private Integer productId;

	@EdmKey
    @EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 40))
	private String productName;

	@EdmProperty(name = "SupplierID")
	private Integer supplierId;

	@EdmProperty(name = "CategoryID")
	private Integer categoryId;

	@EdmProperty(facets = @EdmFacets(maxLength = 20))
	private String quantityPerUnit;

	@EdmProperty(type = EdmType.DECIMAL, facets = @EdmFacets(precision = 19, scale = 4))
	private Double unitPrice;

	@EdmProperty
	private Short unitsInStock;

	@EdmProperty
	private Short unitsOnOrder;

	@EdmProperty
	private Short reorderLevel;

	@EdmKey
	@EdmProperty(facets = @EdmFacets(nullable = false))
	private Boolean discontinued;

	@EdmKey
	@EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 15))
	private String categoryName;

}
