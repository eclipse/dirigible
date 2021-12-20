package org.eclipse.dirigible.engine.odata2.sql.entities.northwind;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;

@EdmEntityType(name = "Sales_by_Category")
@EdmEntitySet(name = "Sales_by_Categories", container = "NorthwindEntities")
public class SalesByCategory {

	@EdmKey
	@EdmProperty(name = "CategoryID", facets = @EdmFacets(nullable = false))
	private Integer categoryId;

	@EdmKey
	@EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 15))
	private String categoryName;

	@EdmKey
	@EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 40))
	private String productName;

	@EdmProperty(type = EdmType.DECIMAL, facets = @EdmFacets(precision = 19, scale = 4))
	private Double productSales;
}
