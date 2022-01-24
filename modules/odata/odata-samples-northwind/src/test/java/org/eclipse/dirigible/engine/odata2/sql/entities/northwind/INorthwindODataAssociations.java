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

public interface INorthwindODataAssociations {

	public static final String FeaturedProduct_Advertisement_Advertisement_FeaturedProduct = "FeaturedProduct_Advertisement_Advertisement_FeaturedProduct";
	public static final String Product_ProductDetail_ProductDetail_Product = "Product_ProductDetail_ProductDetail_Product";
	public static final String Product_Categories_Category_Products = "Product_Categories_Category_Products";
	public static final String Product_Supplier_Supplier_Products = "Product_Supplier_Supplier_Products";
	public static final String Person_PersonDetail_PersonDetail_Person = "Person_PersonDetail_PersonDetail_Person";
}
