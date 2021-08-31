package org.eclipse.dirigible.engine.odata2.sql.entities;

import java.util.Date;
import java.util.List;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty.Multiplicity;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;

@EdmEntityType(name = "Car")
@EdmEntitySet(name = "Cars")
public class Car {
    @EdmKey
    @EdmProperty
    private String id;

    @EdmProperty
    private String make;
    
    @EdmProperty
    private String model;
    
    @EdmProperty
    private Integer year;

    @EdmProperty
    private Double price;

    @EdmProperty(type = EdmType.DATE_TIME)
    private Date updated;
    
    @EdmNavigationProperty(toMultiplicity = Multiplicity.MANY, toType = Driver.class, association = "Car2DriversAssociation")
    private List<Driver> drivers;

}