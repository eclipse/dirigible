package org.eclipse.dirigible.engine.odata2.sql;

import java.io.IOException;

import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Category;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Customer;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.CustomerDemographic;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Employee;

public abstract class AbstractODataNorthwindTest extends AbstractSQLPropcessorTest {

	@Override
	protected Class<?>[] getODataEntities() {
		Class<?>[] classes = { //
				Category.class, //
				CustomerDemographic.class, //
				Customer.class, //
				Employee .class //
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
				.replaceAll(": ", ":");
	}
}
