package org.eclipse.dirigible.engine.odata2.sql.entities.northwind;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;

@EdmEntityType(name = "Customer_and_Suppliers_by_City")
@EdmEntitySet(name = "Customer_and_Suppliers_by_Cities", container = "NorthwindEntities")
public class CustomerAndSuppliersByCity {

	@EdmProperty(facets = @EdmFacets(maxLength = 15))
	private String city;

	@EdmKey
	@EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 40))
	private String companyName;

	@EdmProperty(facets = @EdmFacets(maxLength = 30))
	private String contactName;

	@EdmKey
	@EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 9))
	private String relationship;
}
