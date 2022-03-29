package org.eclipse.dirigible.engine.odata2.sql.entities;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;

@EdmEntityType(name = "UsersToGroup")
@EdmEntitySet(name = "UsersToGroup")
public class UsersToGroup {

//    @EdmKey
    @EdmProperty
    private String UserId;

//    @EdmKey
    @EdmProperty
    private String GroupId;
}
