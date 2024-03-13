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
package org.eclipse.dirigible.components.api.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.benmanes.caffeine.cache.Cache;


public class CacheFacade {

    private static Cache<String, Object> cache;

    /**
     * Instantiates a new caffeine database metadata cache.
     */
    static {
        initCache();
    }

    /**
     * Inits the cache.
     */
    private static void initCache() {
        cache = Caffeine.newBuilder()
                        .build();
    }

    /**
     * Getter for the value of the property by its key.
     *
     * @param key the key
     * @return the string
     */
    public Object get(String key) {
        return cache.getIfPresent(key);
    }

    /**
     * Set.
     *
     * @param key the path
     * @param content the content
     */
    public void set(String key, Object content) {
        if (content != null) {
            cache.put(key, content);
        }
    }

    /**
     * Deletes the instance.
     *
     * @param key the path
     */
    public void delete(String key) {
        cache.invalidate(key);
    }

    /**
     * Clear.
     */
    public void clear() {
        cache.invalidateAll();
    }
}
