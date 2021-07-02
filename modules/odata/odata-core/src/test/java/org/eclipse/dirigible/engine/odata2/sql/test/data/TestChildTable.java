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
package org.eclipse.dirigible.engine.odata2.sql.test.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

@Entity
public class TestChildTable {
    @Id
    @TableGenerator(name = "TestChildTable", initialValue = 1, allocationSize = 50)
    @GeneratedValue(generator = "TestChildTable")
    private String id;

    @Column(name = "CHILDNAME", length = 10)
    private String childName;

    @Column(name = "CHILDVALUE", length = 20)
    private String childValue;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ROOT_ID")
    private TestRootTable root;

    public String getId() {
        return id;
    }

    public TestRootTable getRoot() {
        return root;
    }

    public void setRoot(final TestRootTable root) {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((childName == null) ? 0 : childName.hashCode());
        result = prime * result + ((childValue == null) ? 0 : childValue.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((root == null) ? 0 : root.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TestChildTable other = (TestChildTable) obj;
        if (childName == null) {
            if (other.childName != null)
                return false;
        } else if (!childName.equals(other.childName))
            return false;
        if (childValue == null) {
            if (other.childValue != null)
                return false;
        } else if (!childValue.equals(other.childValue))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (root == null) {
            if (other.root != null)
                return false;
        } else if (!root.equals(other.root))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TestChildTable [id=" + id + ", childName=" + childName + ", childValue=" + childValue + ", root=" + root + "]";
    }

}
