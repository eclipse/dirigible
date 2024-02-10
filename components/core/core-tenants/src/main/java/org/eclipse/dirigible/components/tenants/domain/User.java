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
package org.eclipse.dirigible.components.tenants.domain;

import org.eclipse.dirigible.components.base.http.roles.Roles;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * The Class User.
 */
@Entity
@Table(name = "DIRIGIBLE_USERS", uniqueConstraints = {@UniqueConstraint(columnNames = {"USER_TENANT_ID", "USER_EMAIL"})})
@ConditionalOnProperty(name = "tenants.enabled", havingValue = "true")
public class User {

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID", nullable = false)
    private long id;

    /** The tenant. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_TENANT_ID")
    private Tenant tenant;

    /** The email. */
    @Column(name = "USER_EMAIL", nullable = false)
    private String email;

    /** The password. */
    @Column(name = "USER_PASSWORD", nullable = false)
    private String password;

    /** The role. */
    @Column(name = "USER_ROLE", nullable = false)
    private Roles role;

    /**
     * Instantiates a new user.
     */
    public User() {}

    /**
     * Instantiates a new user.
     *
     * @param tenant the tenant
     * @param email the email
     * @param password the password
     * @param role the role
     */
    public User(Tenant tenant, String email, String password, Roles role) {
        this.tenant = tenant;
        this.email = email;
        this.password = password;
        this.role = role;
    }

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
     * Gets the tenant.
     *
     * @return the tenant
     */
    public Tenant getTenant() {
        return tenant;
    }

    /**
     * Sets the tenant.
     *
     * @param tenant the new tenant
     */
    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    /**
     * Gets the email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     *
     * @param email the new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the role.
     *
     * @return the role
     */
    public Roles getRole() {
        return role;
    }

    /**
     * Sets the role.
     *
     * @param role the new role
     */
    public void setRole(Roles role) {
        this.role = role;
    }
}
