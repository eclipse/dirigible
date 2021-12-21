package org.eclipse.dirigible.engine.odata2.sql.entities.northwind;

import java.util.Date;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;

@EdmEntityType(name = "Summary_of_Sales_by_Year")
@EdmEntitySet(name = "Summary_of_Sales_by_Years", container = "NorthwindEntities")
public class SummaryOfSalesByYears {

	@EdmProperty(type = EdmType.DATE_TIME)
	private Date shippedDate;

	@EdmKey
	@EdmProperty(name = "OrderID", facets = @EdmFacets(nullable = false))
	private Integer orderId;

	@EdmProperty(type = EdmType.DECIMAL, facets = @EdmFacets(precision = 19, scale = 4))
	private Double subtotal;

}
