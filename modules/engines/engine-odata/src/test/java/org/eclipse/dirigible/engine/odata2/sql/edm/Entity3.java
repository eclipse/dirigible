/**
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.odata2.sql.edm;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty.Multiplicity;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;

@EdmEntityType
@EdmEntitySet(name = "Entities3")
public class Entity3 {

    @EdmKey
    @EdmProperty
    private Long id;

    @EdmProperty
    private String description;

    @EdmProperty
    private CTEntity complexTypeProperty;

    @EdmNavigationProperty(toMultiplicity = Multiplicity.ZERO_OR_ONE, toType = Entity2.class, association = "Entities3OfEntity2")
    private Entity2 entity2;

}
