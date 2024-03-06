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
package org.eclipse.dirigible.components.tenants.service;

import java.util.List;
import org.eclipse.dirigible.components.tenants.domain.User;
import org.eclipse.dirigible.components.tenants.domain.UserRoleAssignment;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * The Class UserService.
 */
@Service
@ConditionalOnProperty(name = "tenants.enabled", havingValue = "true")
public class UserRoleAssignmentService {

    private final UserRoleAssignmentRepository repository;

    public UserRoleAssignmentService(UserRoleAssignmentRepository repository) {
        this.repository = repository;
    }

    public List<UserRoleAssignment> findByUser(User user) {
        return repository.findByUser(user);
    }

    public UserRoleAssignment save(UserRoleAssignment assignment) {
        return repository.save(assignment);
    }

}
