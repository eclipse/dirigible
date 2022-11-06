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
package org.eclipse.dirigible.components.security.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.dirigible.components.base.artefact.Artefact;

/**
 * The Class SecurityAccess.
 */

@Entity
@Table(name = "DIRIGIBLE_SECURITY_ACCESS")
public class Access extends Artefact {

    /**
     * The Constant ARTEFACT_TYPE.
     */
    public static final String ARTEFACT_TYPE = "access";

    /**
     * The id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACCESS_ID", nullable = false)
    private Long id;

    /**
     * The scope.
     */
    @Column(name = "ACCESS_SCOPE", columnDefinition = "VARCHAR", nullable = false, length = 255)
    private String scope;

    /**
     * The path.
     */
    @Column(name = "ACCESS_PATH", columnDefinition = "VARCHAR", nullable = false, length = 255)
    private String path;

    /**
     * The method.
     */
    @Column(name = "ACCESS_METHOD", columnDefinition = "VARCHAR", nullable = false, length = 20)
    private String method;

    /**
     * The role.
     */
    @Column(name = "ACCESS_ROLE", columnDefinition = "VARCHAR", nullable = false, length = 64)
    private String role;

    public Access(String location, String name, String description, String scope, String path, String method, String role) {
        super(location, name, ARTEFACT_TYPE, description, null);
        this.scope = scope;
        this.path = path;
        this.method = method;
        this.role = role;
    }

    public Access() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "SecurityAccess{" +
                "id=" + id +
                ", scope='" + scope + '\'' +
                ", path='" + path + '\'' +
                ", method='" + method + '\'' +
                ", role='" + role + '\'' +
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
