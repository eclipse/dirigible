package org.eclipse.dirigible.engine.odata2.sql.entities.northwind;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;

@EdmEntityType(name = "Region")
@EdmEntitySet(name = "Regions", container = "NorthwindEntities")
public class Region {

	@EdmKey
    @EdmProperty(name = "RegionID", facets = @EdmFacets(nullable = false))
    private Integer regionId;

    @EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 50))
    private String regionDescription;
}
