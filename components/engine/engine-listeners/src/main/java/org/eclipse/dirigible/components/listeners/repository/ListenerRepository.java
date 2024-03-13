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
package org.eclipse.dirigible.components.listeners.repository;

import org.eclipse.dirigible.components.base.artefact.ArtefactRepository;
import org.eclipse.dirigible.components.listeners.domain.Listener;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Interface ListenerRepository.
 */
@Repository("listenerRepository")
public interface ListenerRepository extends ArtefactRepository<Listener, Long> {

    @Override
    @Modifying
    @Transactional
    @Query(value = "UPDATE Listener SET running = :running")
    void setRunningToAll(@Param("running") boolean running);
}
