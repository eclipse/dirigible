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
package org.eclipse.dirigible.database.ds.test;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.api.topology.TopologicalSorter;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.eclipse.dirigible.database.ds.model.DataStructureDependencyModel;
import org.eclipse.dirigible.database.ds.model.DataStructureModel;
import org.eclipse.dirigible.database.ds.synchronizer.DataStructuresSynchronizer;
import org.eclipse.dirigible.database.ds.synchronizer.TopologyDataStructureModelWrapper;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class GenericTopologySorterTest.
 */
public class GenericTopologySorterTest extends AbstractDirigibleTest {
	
	/** The data structure core service. */
	private DataStructuresSynchronizer dataStructuresSynchronizer;

	/** The datasource */
	private DataSource dataSource;

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.dataStructuresSynchronizer = new DataStructuresSynchronizer();
		this.dataSource = (DataSource) StaticObjects.get(StaticObjects.DATASOURCE);
	}

	/**
	 * Test sort.
	 * @throws SQLException 
	 */
	@Test
	public void testSort() throws SQLException {
		Map<String, DataStructureModel> models = new HashMap<String, DataStructureModel>();

		DataStructureModel customers_view = new DataStructureModel();
		customers_view.setName("customers_view");
		customers_view.setLocation("/project1/customers_view.view");
		customers_view.getDependencies().add(new DataStructureDependencyModel("customer", "TABLE"));
		customers_view.getDependencies().add(new DataStructureDependencyModel("external", "TABLE"));
		models.put("customers_view", customers_view);
		DataStructureModel users_view = new DataStructureModel();
		users_view.setName("users_view");
		users_view.setLocation("/project1/users_view.view");
		users_view.getDependencies().add(new DataStructureDependencyModel("user", "TABLE"));
		models.put("users_view", users_view);
		DataStructureModel customer = new DataStructureModel();
		customer.setName("customer");
		customer.setLocation("/project1/customers.table");
		customer.getDependencies().add(new DataStructureDependencyModel("address", "TABLE"));
		models.put("customer", customer);
		DataStructureModel address = new DataStructureModel();
		address.setName("address");
		address.setLocation("/project1/address.table");
		address.getDependencies().add(new DataStructureDependencyModel("city", "TABLE"));
		models.put("address", address);
		DataStructureModel city = new DataStructureModel();
		city.setName("city");
		city.setLocation("/project1/city.table");
		models.put("city", city);
		DataStructureModel user = new DataStructureModel();
		user.setName("user");
		user.setLocation("/project1/user.table");
		user.getDependencies().add(new DataStructureDependencyModel("address", "TABLE"));
		models.put("user", user);

		System.out.println("======= Unsorted =======");

		for (Entry<String, DataStructureModel> entry : models.entrySet()) {
			System.out.println(entry.getKey());
		}

		Connection connection = dataSource.getConnection();
		
		TopologicalSorter<TopologyDataStructureModelWrapper> sorter = new TopologicalSorter<>();
		
		List<TopologyDataStructureModelWrapper> list = new ArrayList<TopologyDataStructureModelWrapper>();
		Map<String, TopologyDataStructureModelWrapper> wrappers = new HashMap<String, TopologyDataStructureModelWrapper>();
		for (DataStructureModel model : models.values()) {
			TopologyDataStructureModelWrapper wrapper = new TopologyDataStructureModelWrapper(dataStructuresSynchronizer, connection, model, wrappers);
			list.add(wrapper);
		}
		
		// Topological sorting by dependencies
		list = sorter.sort(list);

		System.out.println("======= Sorted =======");

		for (TopologyDataStructureModelWrapper wrapper : list) {
			System.out.println(wrapper.getId());
		}

		assertEquals(list.get(0).getId(), "city");
		assertEquals(list.get(5).getId(), "users_view");

	}

	/**
	 * Test sort cyclic.
	 * @throws SQLException 
	 */
	@Test
	public void testSortCyclic() throws SQLException {
		Map<String, DataStructureModel> models = new HashMap<String, DataStructureModel>();

		DataStructureModel customers_view = new DataStructureModel();
		customers_view.setName("customers_view");
		customers_view.getDependencies().add(new DataStructureDependencyModel("customer", "TABLE"));
		customers_view.getDependencies().add(new DataStructureDependencyModel("external", "TABLE"));
		models.put("customers_view", customers_view);
		DataStructureModel users_view = new DataStructureModel();
		users_view.setName("users_view");
		users_view.getDependencies().add(new DataStructureDependencyModel("user", "TABLE"));
		models.put("users_view", users_view);
		DataStructureModel customer = new DataStructureModel();
		customer.setName("customer");
		customer.getDependencies().add(new DataStructureDependencyModel("address", "TABLE"));
		models.put("customer", customer);
		DataStructureModel address = new DataStructureModel();
		address.setName("address");
		address.getDependencies().add(new DataStructureDependencyModel("city", "TABLE"));

		address.getDependencies().add(new DataStructureDependencyModel("customers_view", "TABLE"));

		models.put("address", address);
		DataStructureModel city = new DataStructureModel();
		city.setName("city");
		models.put("city", city);
		DataStructureModel user = new DataStructureModel();
		user.setName("user");
		user.getDependencies().add(new DataStructureDependencyModel("address", "TABLE"));
		models.put("user", user);

		
		System.out.println("======= Unsorted =======");

		for (Entry<String, DataStructureModel> entry : models.entrySet()) {
			System.out.println(entry.getKey());
		}
		
		Connection connection = dataSource.getConnection();
		
		TopologicalSorter<TopologyDataStructureModelWrapper> sorter = new TopologicalSorter<>();
		
		List<TopologyDataStructureModelWrapper> list = new ArrayList<TopologyDataStructureModelWrapper>();
		Map<String, TopologyDataStructureModelWrapper> wrappers = new HashMap<String, TopologyDataStructureModelWrapper>();
		for (DataStructureModel model : models.values()) {
			TopologyDataStructureModelWrapper wrapper = new TopologyDataStructureModelWrapper(dataStructuresSynchronizer, connection, model, wrappers);
			list.add(wrapper);
		}
		
		// Topological sorting by dependencies
		list = sorter.sort(list);
		
		System.out.println("======= Sorted =======");

		for (TopologyDataStructureModelWrapper wrapper : list) {
			System.out.println(wrapper.getId());
		}

		assertEquals(list.get(0).getId(), "city");
		assertEquals(list.get(5).getId(), "users_view");

	}
}
