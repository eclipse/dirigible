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
package org.eclipse.dirigible.components.tenants.repository;

import java.util.Optional;

import org.eclipse.dirigible.components.tenants.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * The Interface UserRepository.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user.
     *
     * @param email the email
     * @param slug the slug
     * @return the optional
     */
    @Query("SELECT user FROM User AS user INNER JOIN FETCH user.tenant" + " WHERE user.email = :email" + " AND user.tenant.slug = :slug")
    Optional<User> findUser(String email, String slug);

    /**
     * Find general admin.
     *
     * @param email the email
     * @return the optional
     */
    @Query("SELECT user FROM User AS user" + " WHERE user.tenant IS NULL" + " AND user.email = :email" + " AND user.role = 0")
    Optional<User> findGeneralAdmin(String email);
}
