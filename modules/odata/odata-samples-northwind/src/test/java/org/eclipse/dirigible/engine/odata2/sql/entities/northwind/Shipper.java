package org.eclipse.dirigible.engine.odata2.sql.entities.northwind;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;

@EdmEntityType(name = "Shipper")
@EdmEntitySet(name = "Shippers", container = "NorthwindEntities")
public class Shipper {

	@EdmKey
    @EdmProperty(name = "ShipperID", facets = @EdmFacets(nullable = false))
    private Integer shipperId;

	@EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 40))
    private String companyName;

	@EdmProperty(facets = @EdmFacets(maxLength = 24))
    private String phone;

}
