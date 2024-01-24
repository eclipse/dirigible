/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.persistence.test;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * The Class Inquiry.
 */
@Table(name = "INQUIRIES")
public class Inquiry {

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "INQUIRY_ID", columnDefinition = "BIGINT", nullable = false)
    private long id;

    /** The subject. */
    @Column(name = "INQUIRY_SUBJECT", columnDefinition = "VARCHAR", nullable = false, length = 512)
    private String subject;

    /**
     * Gets the id.
     *
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the subject.
     *
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the subject.
     *
     * @param subject the new subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

}
