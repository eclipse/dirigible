package org.eclipse.dirigible.engine.odata2.sql.entities.northwind;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;

@EdmEntityType(name = "Order_Subtotal")
@EdmEntitySet(name = "Order_Subtotals", container = "NorthwindEntities")
public class OrderSubtotal {

	@EdmKey
	@EdmProperty(name = "OrderID", facets = @EdmFacets(nullable = false))
	private Integer orderId;

	@EdmProperty(type = EdmType.DECIMAL, facets = @EdmFacets(precision = 19, scale = 4))
	private Double subtotal;
}
