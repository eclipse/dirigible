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
package org.eclipse.dirigible.components.websockets.domain;

import org.eclipse.dirigible.components.base.artefact.Artefact;

import javax.persistence.*;

@Entity
@Table(name = "DIRIGIBLE_WEBSOCKETS")
public class Websocket extends Artefact {

    public static final String ARTEFACT_TYPE = "websocket";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WEBSOCKET_ID", nullable = false)
    private Long id;

    @Column(name = "WEBSOCKET_ENDPOINT_NAME", columnDefinition = "VARCHAR", nullable = false, length = 255, unique = true)
    private String endpoint;

    @Column(name = "WEBSOCKET_HANDLER", columnDefinition = "VARCHAR", nullable = false, length = 255)
    private String handler;

    @Column(name = "WEBSOCKET_ENGINE", columnDefinition = "VARCHAR", nullable = true, length = 255)
    private String engine;

    public Websocket(String location, String name, String description,
                     String endpoint, String handler, String engine) {
        super(location, name, ARTEFACT_TYPE, description, null);
        this.endpoint = endpoint;
        this.handler = handler;
        this.engine = engine;
    }

    public Websocket() {
        super();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    @Override
    public String toString() {
        return "Websocket{" +
                "id=" + id +
                ", endpoint='" + endpoint + '\'' +
                ", handler='" + handler + '\'' +
                ", engine='" + engine + '\'' +
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
