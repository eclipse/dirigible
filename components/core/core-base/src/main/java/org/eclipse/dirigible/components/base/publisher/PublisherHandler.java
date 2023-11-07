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
package org.eclipse.dirigible.components.base.publisher;

/**
 * The Interface PublisherHandler.
 */
public interface PublisherHandler {

    /**
     * Before publish.
     *
     * @param location the location
     */
    default void beforePublish(String location) {}

    /**
     * After publish.
     *
     * @param workspaceLocation the workspace location
     * @param registryLocation the registry location
     * @param metadata the metadata
     */
    default void afterPublish(String workspaceLocation, String registryLocation, AfterPublishMetadata metadata) {}

    /**
     * Before unpublish.
     *
     * @param location the location
     */
    default void beforeUnpublish(String location) {}

    /**
     * After unpublish.
     *
     * @param location the location
     */
    default void afterUnpublish(String location) {}

    /**
     * The AfterPublishMetadata.
     *
     * @param workspace the workspace location
     * @param projectName the project name
     * @param entryPath the entry path
     * @param isDirectory whether is a directory
     */
    record AfterPublishMetadata(String workspace, String projectName, String entryPath, boolean isDirectory) {
    }

}
