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
package org.eclipse.dirigible.components.security.service;

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.security.domain.SecurityAccess;
import org.eclipse.dirigible.components.security.repository.SecurityAccessRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * The Class SecurityAccessService.
 */
@Service
@Transactional
public class SecurityAccessService implements ArtefactService<SecurityAccess> {

    /**
     * The security access repository.
     */
    @Autowired
    private SecurityAccessRepository securityAccessRepository;

    /**
     * Gets the all.
     *
     * @return the all
     */
    @Override
    @Transactional(readOnly = true)
    public List<SecurityAccess> getAll() {
        return securityAccessRepository.findAll();
    }

    /**
     * Find all.
     *
     * @param pageable the pageable
     * @return the page
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SecurityAccess> findAll(Pageable pageable) {
        return securityAccessRepository.findAll(pageable);
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the security access
     */
    @Override
    @Transactional(readOnly = true)
    public SecurityAccess findById(Long id) {
        Optional<SecurityAccess> securityAccess = securityAccessRepository.findById(id);
        if (securityAccess.isPresent()) {
            return securityAccess.get();
        } else {
            throw new IllegalArgumentException("SecurityAccess with id does not exist: " + id);
        }
    }

    /**
     * Find by name.
     *
     * @param name the name
     * @return the security access
     */
    @Override
    @Transactional(readOnly = true)
    public SecurityAccess findByName(String name) {
        SecurityAccess filter = new SecurityAccess();
        filter.setName(name);
        Example<SecurityAccess> example = Example.of(filter);
        Optional<SecurityAccess> securityAccess = securityAccessRepository.findOne(example);
        if (securityAccess.isPresent()) {
            return securityAccess.get();
        } else {
            throw new IllegalArgumentException("SecurityAccess with name does not exist: " + name);
        }
    }

    /**
     * Save.
     *
     * @param securityAccess the security access
     * @return the security access
     */
    @Override
    public SecurityAccess save(SecurityAccess securityAccess) {
        return securityAccessRepository.saveAndFlush(securityAccess);
    }

    /**
     * Delete.
     *
     * @param securityAccess the security access
     */
    @Override
    public void delete(SecurityAccess securityAccess) {
        securityAccessRepository.delete(securityAccess);
    }

}
