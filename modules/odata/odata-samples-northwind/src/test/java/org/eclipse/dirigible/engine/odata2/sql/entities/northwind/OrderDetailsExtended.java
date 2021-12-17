package org.eclipse.dirigible.engine.odata2.sql.entities.northwind;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;

@EdmEntityType(name = "Order_Details_Extended")
@EdmEntitySet(name = "Order_Details_Extendeds", container = "NorthwindEntities")
public class OrderDetailsExtended {

	@EdmKey
	@EdmProperty(name = "OrderID", facets = @EdmFacets(nullable = false))
	private Integer orderId;

	@EdmKey
	@EdmProperty(name = "ProductID", facets = @EdmFacets(nullable = false))
	private Integer productId;

	@EdmKey
	@EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 40))
	private String productName;

	@EdmKey
	@EdmProperty(type = EdmType.DECIMAL, facets = @EdmFacets(nullable = false, precision = 19, scale = 4))
	private Double unitPrice;

	@EdmKey
	@EdmProperty(facets = @EdmFacets(nullable = false))
	private Short quantity;

	@EdmKey
	@EdmProperty(facets = @EdmFacets(nullable = false))
	private Float discount;

	@EdmProperty(type = EdmType.DECIMAL, facets = @EdmFacets(precision = 19, scale = 4))
	private Double extendedPrice;

}
