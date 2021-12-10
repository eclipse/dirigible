package org.eclipse.dirigible.engine.odata2.sql.entities.northwind;

import java.util.Date;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;

@EdmEntityType(name = "Order")
@EdmEntitySet(name = "Orders", container = "NorthwindEntities")
public class Order {

	@EdmKey
    @EdmProperty(name = "OrderID", facets = @EdmFacets(nullable = false))
    private Integer orderId;

	@EdmProperty(name = "CustomerID", facets = @EdmFacets(maxLength = 5))
	private String customerId;

	@EdmProperty(name = "EmployeeID")
	private Integer employeeId;

	@EdmProperty
	private Date orderDate;

	@EdmProperty
	private Date requiredDate;

	@EdmProperty
	private Date shippedDate;

	@EdmProperty
	private Integer shipVia;

	@EdmProperty(type = EdmType.DECIMAL, facets = @EdmFacets(precision = 19, scale = 4))
	private Double freight;

	@EdmProperty(facets = @EdmFacets(maxLength = 40))
	private String shipName;

	@EdmProperty(facets = @EdmFacets(maxLength = 60))
	private String shipAddress;

	@EdmProperty(facets = @EdmFacets(maxLength = 15))
	private String shipCity;

	@EdmProperty(facets = @EdmFacets(maxLength = 15))
	private String shipRegion;

	@EdmProperty(facets = @EdmFacets(maxLength = 10))
	private String shipPostalCode;

	@EdmProperty(facets = @EdmFacets(maxLength = 15))
	private String shipCountry;

}
