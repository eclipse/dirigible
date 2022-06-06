package org.eclipse.dirigible.engine.odata2.sql.entities;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;

@EdmEntityType(name = "View")
@EdmEntitySet(name = "Views")
public class View {

    @EdmKey
    @EdmProperty
    private String id;

    @EdmProperty
    private String name;
}
