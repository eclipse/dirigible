package org.eclipse.dirigible.engine.odata2.sql.entities;

import org.apache.olingo.odata2.api.annotation.edm.*;

import java.util.List;

@EdmEntityType(name = "User")
@EdmEntitySet(name = "Users")
public class User {
    static final String USER_2_GROUP_ASSOCIATION = "UserToGroup";

    @EdmKey
    @EdmProperty
    private String id;

    @EdmProperty
    private String firstname;

    @EdmNavigationProperty(toMultiplicity = EdmNavigationProperty.Multiplicity.MANY, toType = Group.class, association = USER_2_GROUP_ASSOCIATION)
    private List<Group> groups;
}
