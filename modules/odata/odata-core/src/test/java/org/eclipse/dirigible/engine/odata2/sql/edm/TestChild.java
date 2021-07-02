/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.sql.edm;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty.Multiplicity;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;

@EdmEntityType(name = "TestChild")
@EdmEntitySet(name = "TestChilds")

public class TestChild {
    @EdmKey
    @EdmProperty
    private String id;

    @EdmProperty
    private String childName;

    @EdmProperty
    private String childValue;

    @EdmNavigationProperty(toMultiplicity = Multiplicity.ZERO_OR_ONE, toType = TestRoot.class, association = "TestChilds")
    private TestRoot root;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public TestRoot getRoot() {
        return root;
    }

    public void setRoot(final TestRoot root) {
        this.root = root;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(final String childName) {
        this.childName = childName;
    }

    public String getChildValue() {
        return childValue;
    }

    public void setChildValue(final String childValue) {
        this.childValue = childValue;

    }

}
