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
package org.eclipse.dirigible.core.publisher.api;

import org.eclipse.dirigible.core.scheduler.api.SchedulerException;

/**
 * The Interface IPublisherHandler.
 */
public interface IPublisherHandler {

    /**
     * Before publish.
     *
     * @param location the location
     * @throws SchedulerException the scheduler exception
     */
    void beforePublish(String location) throws SchedulerException;

    /**
     * After publish.
     *
     * @param workspaceLocation the workspace location
     * @param registryLocation the registry location
     * @throws SchedulerException the scheduler exception
     */
    void afterPublish(String workspaceLocation, String registryLocation) throws SchedulerException;

    /**
     * Before unpublish.
     *
     * @param location the location
     * @throws SchedulerException the scheduler exception
     */
    void beforeUnpublish(String location) throws SchedulerException;

    /**
     * After unpublish.
     *
     * @param location the location
     * @throws SchedulerException the scheduler exception
     */
    void afterUnpublish(String location) throws SchedulerException;

}
