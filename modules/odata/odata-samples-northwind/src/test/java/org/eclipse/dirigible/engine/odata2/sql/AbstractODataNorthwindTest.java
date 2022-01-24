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
package org.eclipse.dirigible.engine.odata2.sql;

import java.io.IOException;

import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.AlphabeticalListOfProduct;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Category;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.CategorySalesFor1997;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.CurrentProductList;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Customer;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.CustomerAndSuppliersByCity;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.CustomerDemographic;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Employee;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Invoice;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Order;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.OrderDetail;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.OrderDetailsExtended;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.OrderSubtotal;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.OrdersQry;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Product;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.ProductSalesFor1997;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.ProductsAboveAveragePrice;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.ProductsByCategory;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Region;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.SalesByCategory;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.SalesTotalsByAmounts;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Shipper;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.SummaryOfSalesByQuarters;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.SummaryOfSalesByYears;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Supplier;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Territory;

public abstract class AbstractODataNorthwindTest extends AbstractSQLProcessorTest {

	@Override
	protected String getChangelogLocation() {
		return "liquibase/changelog-northwind.xml";
	}

	@Override
	protected Class<?>[] getODataEntities() {
		Class<?>[] classes = { //
				Category.class, //
				CustomerDemographic.class, //
				Customer.class, //
				Employee .class, //
				OrderDetail.class, //
				Order.class, //
				Product.class, //
				Region.class, //
				Shipper.class, //
				Supplier.class, //
				Territory.class, //
				Invoice.class, //
				AlphabeticalListOfProduct.class, //
				CategorySalesFor1997.class, //
				CurrentProductList.class, //
				CustomerAndSuppliersByCity.class, //
				OrderDetailsExtended.class, //
				OrderSubtotal.class, //
				OrdersQry.class, //
				ProductSalesFor1997.class, //
				ProductsAboveAveragePrice.class, //
				ProductsByCategory.class, //
				SalesByCategory.class, //
				SalesTotalsByAmounts.class, //
				SummaryOfSalesByQuarters.class, //
				SummaryOfSalesByYears.class //
		};
		return classes;
	}

	protected String loadExpectedMetadata() throws IOException {
		return loadExpectedData("metadata.xml");
	}

	protected String loadExpectedData(String fileName) throws IOException {
		String data = loadResource(fileName);
		return data //
				.replaceAll("\n", "") //
				.replaceAll("[^\\S\\r]{2,}", "")
				.replaceAll(": ", ":")
				.replaceAll(" />", "/>");
	}
}
