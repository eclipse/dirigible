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
package org.eclipse.dirigible.components.tenants.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * The Class CustomUserDetails.
 */
public class CustomUserDetails extends User {

    /** The user id. */
    private final long userId;

    /** The tenant id. */
    private final Long tenantId;

    /**
     * Instantiates a new custom user details.
     *
     * @param username the username
     * @param password the password
     * @param userId the user id
     * @param tenantId the tenant id
     * @param authorities the authorities
     */
    public CustomUserDetails(String username, String password, long userId, Long tenantId,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
        this.tenantId = tenantId;
    }

    /**
     * Gets the user id.
     *
     * @return the user id
     */
    public long getUserId() {
        return userId;
    }

    /**
     * Gets the tenant id.
     *
     * @return the tenant id
     */
    public Long getTenantId() {
        return tenantId;
    }
}
