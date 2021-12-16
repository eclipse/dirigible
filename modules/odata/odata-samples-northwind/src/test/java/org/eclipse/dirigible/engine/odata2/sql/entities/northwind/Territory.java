package org.eclipse.dirigible.engine.odata2.sql.entities.northwind;

import java.util.List;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty.Multiplicity;

@EdmEntityType(name = "Territory")
@EdmEntitySet(name = "Territories", container = "NorthwindEntities")
public class Territory {

	@EdmKey
    @EdmProperty(name = "TerritoryID", facets = @EdmFacets(nullable = false, maxLength = 20))
    private String territoryId;

	@EdmProperty(facets = @EdmFacets(nullable = false, maxLength = 50))
    private String territoryDescription;

	@EdmProperty(name = "RegionID", facets = @EdmFacets(nullable = false))
    private Integer regionId;

	@EdmNavigationProperty( //
			toMultiplicity = Multiplicity.MANY, //
			toType = Territory.class, //
			toRole = "Employees", //
			association = "EmployeeTerritories" //
	)
	private List<Employee> emplpyees;
}
