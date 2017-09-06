/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.persistence.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.dirigible.database.persistence.PersistenceFactory;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.junit.Test;

public class PersistenceFactoryInheritanceTest {

	@Test
	public void createModelFromPojo() {
		GoldCustomer customer = new GoldCustomer();
		PersistenceTableModel persistenceModel = PersistenceFactory.createModel(customer);
		assertEquals("CUSTOMERS", persistenceModel.getTableName());
		assertEquals("FACTORY", persistenceModel.getSchemaName());
		assertTrue(persistenceModel.getColumns().size() == 4);
		PersistenceTableColumnModel persistenceCoulmnModelCustomerId = null;
		for (PersistenceTableColumnModel persistenceCoulmnModel : persistenceModel.getColumns()) {
			if ("CUSTOMER_ID".equals(persistenceCoulmnModel.getName())) {
				persistenceCoulmnModelCustomerId = persistenceCoulmnModel;
			}
		}
		assertNotNull(persistenceCoulmnModelCustomerId);
		assertEquals("INTEGER", persistenceCoulmnModelCustomerId.getType());
	}

}
