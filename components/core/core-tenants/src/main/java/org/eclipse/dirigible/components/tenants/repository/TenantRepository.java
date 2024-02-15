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

import org.eclipse.dirigible.components.tenants.domain.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The Interface TenantRepository.
 */
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    /**
     * Find by slug.
     *
     * @param slug the slug
     * @return the optional
     */
    Optional<Tenant> findBySlug(String slug);
}
