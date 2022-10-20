/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.openapi.domain;

import org.eclipse.dirigible.components.base.artefact.Artefact;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * The WebsocketDefinition Entity.
 */
@Entity
@Table(name = "DIRIGIBLE_OPENAPI")
public class OpenAPI extends Artefact {

    /**
     * The Constant ARTEFACT_TYPE.
     */
    public static final String ARTEFACT_TYPE = "openapi";

    /**
     * The id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OPENAPI_ID", nullable = false)
    private Long id;

    /**
     * The hash.
     */
    @Column(name = "OPENAPI_HASH", columnDefinition = "VARCHAR", nullable = false, length = 32)
    private String hash;

    public OpenAPI(String location, String name, String description, String hash) {
        super(location, name, ARTEFACT_TYPE, description, null);
        this.hash = hash;
    }

    public OpenAPI() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "OpenAPI{" +
                "id=" + id +
                ", hash='" + hash + '\'' +
                ", location='" + location + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", key='" + key + '\'' +
                ", dependencies='" + dependencies + '\'' +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                ", updatedBy=" + updatedBy +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
