/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.security.service;

import org.eclipse.dirigible.components.base.artefact.BaseArtefactService;
import org.eclipse.dirigible.components.security.domain.Role;
import org.eclipse.dirigible.components.security.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class RoleService.
 */

@Service
@Transactional
public class RoleService extends BaseArtefactService<Role, Long> {

    private final RoleRepository roleRepository;

    /**
     * Instantiates a new role service.
     *
     * @param repository the repository
     */
    public RoleService(RoleRepository repository) {
        super(repository);
        this.roleRepository = repository;
    }

    /**
     * Role exists by name. Several projects may declare a role of the same name, so it is an existence
     * check rather than a lookup of one role.
     *
     * @param name the name
     * @return true, if successful
     */
    @Transactional(readOnly = true)
    public boolean roleExistsByName(String name) {
        return roleRepository.existsByName(name);
    }

}
