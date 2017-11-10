/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.persistence.test;

import static org.junit.Assert.assertEquals;

import org.eclipse.dirigible.database.persistence.PersistenceFactory;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.parser.PersistenceJsonParser;
import org.junit.Test;

import com.google.gson.JsonSyntaxException;

public class PersistenceJsonParserTest {

	@Test
	public void modelFromJson() throws JsonSyntaxException, ClassNotFoundException {
		PersistenceJsonParser<Order> parser = new PersistenceJsonParser<Order>();
		PersistenceTableModel persistenceModel = parser.parseModel(
				"{\"className\":\"org.eclipse.dirigible.database.persistence.test.Customer\",\"tableName\":\"CUSTOMERS\",\"schemaName\":\"FACTORY\",\"columns\":[{\"field\":\"id\",\"name\":\"CUSTOMER_ID\",\"type\":\"INTEGER\",\"length\":255,\"nullable\":false,\"primaryKey\":true,\"precision\":0,\"scale\":0,\"generated\":false,\"unique\":false},{\"field\":\"firstName\",\"name\":\"CUSTOMER_FIRST_NAME\",\"type\":\"VARCHAR\",\"length\":512,\"nullable\":false,\"primaryKey\":false,\"precision\":0,\"scale\":0,\"generated\":false,\"unique\":false},{\"field\":\"lastName\",\"name\":\"CUSTOMER_LAST_NAME\",\"type\":\"VARCHAR\",\"length\":512,\"nullable\":false,\"primaryKey\":false,\"precision\":0,\"scale\":0,\"generated\":false,\"unique\":false},{\"field\":\"age\",\"name\":\"CUSTOMER_AGE\",\"type\":\"INTEGER\",\"length\":255,\"nullable\":false,\"primaryKey\":false,\"precision\":0,\"scale\":0,\"generated\":false,\"unique\":false}]}");
		assertEquals("Subject 1", persistenceModel.getTableName(), "CUSTOMERS");
	}

	@Test
	public void modelToJson() {
		Customer customer = new Customer();
		PersistenceTableModel persistenceModel = PersistenceFactory.createModel(customer);
		PersistenceJsonParser<?> parser = new PersistenceJsonParser<>();
		String json = parser.serializeModel(persistenceModel);
		assertEquals(
				"{\"className\":\"org.eclipse.dirigible.database.persistence.test.Customer\",\"tableName\":\"CUSTOMERS\",\"schemaName\":\"FACTORY\",\"columns\":[{\"field\":\"id\",\"name\":\"CUSTOMER_ID\",\"type\":\"INTEGER\",\"length\":255,\"nullable\":false,\"primaryKey\":true,\"precision\":0,\"scale\":0,\"unique\":false},{\"field\":\"firstName\",\"name\":\"CUSTOMER_FIRST_NAME\",\"type\":\"VARCHAR\",\"length\":512,\"nullable\":false,\"primaryKey\":false,\"precision\":0,\"scale\":0,\"unique\":false},{\"field\":\"lastName\",\"name\":\"CUSTOMER_LAST_NAME\",\"type\":\"VARCHAR\",\"length\":512,\"nullable\":false,\"primaryKey\":false,\"precision\":0,\"scale\":0,\"unique\":false},{\"field\":\"age\",\"name\":\"CUSTOMER_AGE\",\"type\":\"INTEGER\",\"length\":255,\"nullable\":false,\"primaryKey\":false,\"precision\":0,\"scale\":0,\"unique\":false}]}",
				json);
	}

}
