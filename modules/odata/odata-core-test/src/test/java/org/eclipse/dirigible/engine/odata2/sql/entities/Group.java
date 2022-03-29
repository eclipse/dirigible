package org.eclipse.dirigible.engine.odata2.sql.entities;

import org.apache.olingo.odata2.api.annotation.edm.*;

import java.util.List;

@EdmEntityType(name = "Group")
@EdmEntitySet(name = "Groups")
public class Group {
    static final String USER_2_GROUP_ASSOCIATION = "UserToGroup";

    @EdmKey
    @EdmProperty
    private String id;

    @EdmProperty
    private String name;

    @EdmNavigationProperty(toMultiplicity = EdmNavigationProperty.Multiplicity.MANY, toType = User.class, association = USER_2_GROUP_ASSOCIATION)
    private List<User> users;
}
