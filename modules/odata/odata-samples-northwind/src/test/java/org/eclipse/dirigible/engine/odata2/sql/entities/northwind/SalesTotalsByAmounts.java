package org.eclipse.dirigible.engine.odata2.sql.entities.northwind;

import java.util.Date;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;

@EdmEntityType(name = "Sales_Totals_by_Amount")
@EdmEntitySet(name = "Sales_Totals_by_Amounts", container = "NorthwindEntities")

public class SalesTotalsByAmounts {

	@EdmProperty(type = EdmType.DECIMAL, facets = @EdmFacets(precision = 19, scale = 4))
	private Double saleAmount;

	@EdmKey
	@EdmProperty(name = "OrderID", facets = @EdmFacets(nullable = false))
	private Integer orderId;

	@EdmKey
	@EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 40))
	private String companyName;

	@EdmProperty(type = EdmType.DATE_TIME)
	private Date shippedDate;
}
