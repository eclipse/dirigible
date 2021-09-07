package org.eclipse.dirigible.engine.odata2.sql.entities;

import java.util.Calendar;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty.Multiplicity;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;

@EdmEntityType(name = "Driver")
@EdmEntitySet(name = "Drivers")
public class Driver {

      @EdmKey
      @EdmProperty
      private String id;
      
      @EdmProperty
      private String firstName;
      
      @EdmProperty
      private String lastName;
      
      @EdmNavigationProperty(toMultiplicity = Multiplicity.ZERO_OR_ONE, toType = Car.class, association = "Car2DriversAssociation")
      private Car car;
      
      @EdmProperty(type = EdmType.DATE_TIME)
      private Calendar updated;

}
