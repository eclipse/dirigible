package test.org.eclipse.dirigible.repository.ext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.dirigible.repository.ext.db.model.DataStructureModel;
import org.eclipse.dirigible.repository.ext.db.model.DependencyModel;
import org.eclipse.dirigible.repository.ext.db.model.EDataStructureModelFormatException;
import org.eclipse.dirigible.repository.ext.db.model.TopologicalSorter;
import org.junit.Test;

public class DataStructureTopologySorter {

	@Test
	public void testSort() {
		Map<String, DataStructureModel> models = new HashMap<String, DataStructureModel>();

		DataStructureModel customers_view = new DataStructureModel();
		customers_view.setName("customers_view");
		customers_view.getDependencies().add(new DependencyModel("customer", "TABLE"));
		customers_view.getDependencies().add(new DependencyModel("external", "TABLE"));
		models.put("customers_view", customers_view);
		DataStructureModel users_view = new DataStructureModel();
		users_view.setName("users_view");
		users_view.getDependencies().add(new DependencyModel("user", "TABLE"));
		models.put("users_view", users_view);
		DataStructureModel customer = new DataStructureModel();
		customer.setName("customer");
		customer.getDependencies().add(new DependencyModel("address", "TABLE"));
		models.put("customer", customer);
		DataStructureModel address = new DataStructureModel();
		address.setName("address");
		address.getDependencies().add(new DependencyModel("city", "TABLE"));
		models.put("address", address);
		DataStructureModel city = new DataStructureModel();
		city.setName("city");
		models.put("city", city);
		DataStructureModel user = new DataStructureModel();
		user.setName("user");
		user.getDependencies().add(new DependencyModel("address", "TABLE"));
		models.put("user", user);

		System.out.println("======= Unsorted =======");

		for (Entry<String, DataStructureModel> entry : models.entrySet()) {
			System.out.println(entry.getKey());
		}

		List<String> output = new ArrayList<String>();
		List<String> external = new ArrayList<String>();

		try {
			TopologicalSorter.sort(models, output, external);
		} catch (EDataStructureModelFormatException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		System.out.println("======= Sorted =======");

		for (String name : output) {
			System.out.println(name);
		}

		System.out.println("======= External =======");

		for (String name : external) {
			System.out.println(name);
		}

		assertEquals(output.get(0), "city");
		assertEquals(output.get(5), "users_view");
		assertEquals(external.get(0), "external");

		/*
		 * ======= Unsorted =======
		 * customers_view
		 * address
		 * city
		 * users_view
		 * user
		 * customer
		 * ======= Sorted =======
		 * city
		 * address
		 * customer
		 * customers_view
		 * user
		 * users_view
		 * ======= External =======
		 * external
		 */

	}

	@Test
	public void testSortCyclic() {
		Map<String, DataStructureModel> models = new HashMap<String, DataStructureModel>();

		DataStructureModel customers_view = new DataStructureModel();
		customers_view.setName("customers_view");
		customers_view.getDependencies().add(new DependencyModel("customer", "TABLE"));
		customers_view.getDependencies().add(new DependencyModel("external", "TABLE"));
		models.put("customers_view", customers_view);
		DataStructureModel users_view = new DataStructureModel();
		users_view.setName("users_view");
		users_view.getDependencies().add(new DependencyModel("user", "TABLE"));
		models.put("users_view", users_view);
		DataStructureModel customer = new DataStructureModel();
		customer.setName("customer");
		customer.getDependencies().add(new DependencyModel("address", "TABLE"));
		models.put("customer", customer);
		DataStructureModel address = new DataStructureModel();
		address.setName("address");
		address.getDependencies().add(new DependencyModel("city", "TABLE"));

		address.getDependencies().add(new DependencyModel("customers_view", "TABLE"));

		models.put("address", address);
		DataStructureModel city = new DataStructureModel();
		city.setName("city");
		models.put("city", city);
		DataStructureModel user = new DataStructureModel();
		user.setName("user");
		user.getDependencies().add(new DependencyModel("address", "TABLE"));
		models.put("user", user);

		List<String> output = new ArrayList<String>();
		List<String> external = new ArrayList<String>();

		try {
			TopologicalSorter.sort(models, output, external);
		} catch (EDataStructureModelFormatException e) {
			assertTrue(e.getMessage().startsWith("Cyclic"));
			System.out.println(">>> Sort Cyclic Dependencies - caught!");
			return;
		}

	}
}
