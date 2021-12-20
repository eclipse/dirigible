package org.eclipse.dirigible.engine.odata2.sql.entities.northwind;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;

@EdmEntityType(name = "Products_by_Category")
@EdmEntitySet(name = "Products_by_Categories", container = "NorthwindEntities")
public class ProductsByCategory {

	@EdmKey
	@EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 15))
	private String categoryName;

	@EdmKey
	@EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 40))
	private String productName;

	@EdmProperty(facets = @EdmFacets(maxLength = 20))
	private String quantityPerUnit;

	@EdmProperty
	private Short unitsInStock;

	@EdmKey
	@EdmProperty(facets = @EdmFacets(nullable = false))
	private Boolean discontinued;
}
