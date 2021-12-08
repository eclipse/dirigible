package org.eclipse.dirigible.engine.odata2.sql;

import java.io.IOException;

import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Category;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Customer;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.CustomerDemographic;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Employee;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Invoice;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Order;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.OrderDetail;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Product;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Region;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Shipper;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Supplier;
import org.eclipse.dirigible.engine.odata2.sql.entities.northwind.Territory;

public abstract class AbstractODataNorthwindTest extends AbstractSQLPropcessorTest {

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
				Invoice.class //
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
