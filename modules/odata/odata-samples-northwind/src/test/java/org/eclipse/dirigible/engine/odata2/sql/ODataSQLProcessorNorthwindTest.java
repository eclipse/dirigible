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
package org.eclipse.dirigible.engine.odata2.sql;

import static org.apache.olingo.odata2.api.commons.ODataHttpMethod.GET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.Response;

import org.apache.cxf.helpers.IOUtils;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Address;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Advertisement;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Category;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Customer;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Employee;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.FeaturedProduct;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Person;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.PersonDetail;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Product;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.ProductDetail;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Supplier;
import org.junit.Ignore;
import org.junit.Test;

public class ODataSQLProcessorNorthwindTest extends AbstractSQLPropcessorTest {

	@Override
	protected Class<?>[] getODataEntities() {
		Class<?>[] classes = { //
				Product.class, //
				FeaturedProduct.class, //
				ProductDetail.class, //
				Category.class, //
				Person.class, //
				Supplier.class, //
				Customer.class, //
				Employee.class, //
				PersonDetail.class, //
				Address.class, //
				Advertisement.class //
		};
		return classes;
	}

	@Test
	public void testMetadataResponse() throws Exception {
		Response response = OData2RequestBuilder.createRequest(sf) //
				.segments("$metadata") //
				.executeRequest(GET);
		String content = IOUtils.toString((InputStream) response.getEntity());
		assertEquals(loadExpectedMetadata(), content);
	}

	@Ignore
	@Test
	public void testProductsResponse() throws Exception {
		Response response = OData2RequestBuilder.createRequest(sf) //
				.segments("Products") //
				.param("$format", "json") //
				.executeRequest(GET);
		String content = IOUtils.toString((InputStream) response.getEntity());
		assertEquals(loadExpectedData("products-all.json"), content);
	}

	private String loadExpectedMetadata() throws IOException {
		return loadExpectedData("metadata.xml");
	}

	private String loadExpectedData(String fileName) throws IOException {
		String data = loadResource(fileName);
		return data.replaceAll("\n", "").replaceAll("[^\\S\\r]{2,}", "");
	}
}