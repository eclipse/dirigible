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

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmMediaResourceContent;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty.Multiplicity;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;

@EdmEntityType(name = "TestRoot") // namespace = "com.sap.hci.api", 
@EdmEntitySet(name = "TestRoots")

public class TestRoot {
    //    @EdmKey
    //    @EdmProperty
    //    private long id;

    @EdmKey
    @EdmProperty
    private String changeId;

    @EdmProperty
    private String stringValue;

    @EdmProperty(type = EdmType.DATE_TIME)
    private Date timeValue;

    @EdmProperty
    private int intValue;

    @EdmProperty
    private String convertValue;

    @EdmMediaResourceContent
    private InputStream mediaValue;

    @EdmNavigationProperty(toMultiplicity = Multiplicity.MANY, toType = TestChild.class, association = "TestChilds")
    private List<TestChild> child;

    public String getChangeId() {
        return changeId;
    }

    public void setChangeId(String changeId) {
        this.changeId = changeId;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Date getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(Date timeValue) {
        this.timeValue = timeValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public List<TestChild> getChild() {
        return child;
    }

    public void setChild(List<TestChild> child) {
        this.child = child;
    }
}
