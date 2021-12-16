package org.eclipse.dirigible.engine.odata2.sql.entities.northwind;

import java.util.List;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty.Multiplicity;

@EdmEntityType(name = "CustomerDemographic")
@EdmEntitySet(name = "CustomerDemographics", container = "NorthwindEntities")
public class CustomerDemographic {

	@EdmKey
	@EdmProperty(name = "CustomerTypeID", facets = @EdmFacets(nullable = false, maxLength = 10))
	private String customerTypeId;

	@EdmProperty
	private String customerDesc;

	@EdmNavigationProperty( //
			toMultiplicity = Multiplicity.MANY, //
			toType = Customer.class, //
			toRole = "Customers", //
			association = "CustomerCustomerDemo" //
	)
	private List<Customer> customers;
}
