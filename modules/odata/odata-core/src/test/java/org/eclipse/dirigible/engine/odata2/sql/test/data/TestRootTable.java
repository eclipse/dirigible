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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;



@Entity
public class TestRootTable {
    @Id
    @TableGenerator(name = "TestRootTable", initialValue = 1, allocationSize = 50)
    @GeneratedValue(generator = "TestRootTable")
    private long id;

    @Column(length = 32) //, unique = true, nullable = false)
    private String changeId;

    @Column(length = 20)
    private String stringValue;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timeValue;

    @Column
    private int intValue;

    @Column
    private int convertValue;

    @Lob
    private byte[] mediaValue;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "root")
    private List<TestChildTable> children;

    //
    // Bean methods
    //

    public long getId() {
        return id;
    }

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

    public int getConvertValue() {
        return convertValue;
    }

    public void setConvertValue(int convertValue) {
        this.convertValue = convertValue;
    }

    public byte[] getMediaValue() {
        return mediaValue; // NOPMD
    }

    public void setMediaValue(final byte[] mediaValue) {
        this.mediaValue = mediaValue;
    }

    public List<TestChildTable> getChildren() {
        return children;
    }

    public void setChildren(List<TestChildTable> children) {
        this.children = children;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((changeId == null) ? 0 : changeId.hashCode());
        result = prime * result + ((children == null) ? 0 : children.hashCode());
        result = prime * result + convertValue;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + intValue;
        result = prime * result + Arrays.hashCode(mediaValue);
        result = prime * result + ((stringValue == null) ? 0 : stringValue.hashCode());
        result = prime * result + ((timeValue == null) ? 0 : timeValue.hashCode());
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
        TestRootTable other = (TestRootTable) obj;
        if (changeId == null) {
            if (other.changeId != null)
                return false;
        } else if (!changeId.equals(other.changeId))
            return false;
        if (children == null) {
            if (other.children != null)
                return false;
        } else if (!children.equals(other.children))
            return false;
        if (convertValue != other.convertValue)
            return false;
        if (id != other.id)
            return false;
        if (intValue != other.intValue)
            return false;
        if (!Arrays.equals(mediaValue, other.mediaValue))
            return false;
        if (stringValue == null) {
            if (other.stringValue != null)
                return false;
        } else if (!stringValue.equals(other.stringValue))
            return false;
        if (timeValue == null) {
            if (other.timeValue != null)
                return false;
        } else if (!timeValue.equals(other.timeValue))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TestRootTable [id=" + id + ", changeId=" + changeId + ", stringValue=" + stringValue + ", timeValue=" + timeValue
                + ", intValue=" + intValue + "]";
    }

}
