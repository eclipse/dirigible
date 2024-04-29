/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.api.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

public class CacheFacade {

    private static final Cache<String, Object> cache = Caffeine.newBuilder()
                                                               .expireAfterWrite(15, TimeUnit.MINUTES)
                                                               .maximumSize(1000)
                                                               .build();

    public static boolean contains(String key) {
        return get(key) != null;
    }

    /**
     * Getter for the value of the property by its key.
     *
     * @param key the key
     * @return the string
     */
    public static Object get(String key) {
        return cache.getIfPresent(key);
    }

    /**
     * Set.
     *
     * @param key the path
     * @param content the content
     */
    public static void set(String key, Object content) {
        if (content != null) {
            cache.put(key, content);
        }
    }

    /**
     * Deletes the instance.
     *
     * @param key the path
     */
    public static void delete(String key) {
        cache.invalidate(key);
    }

    /**
     * Clear.
     */
    public static void clear() {
        cache.invalidateAll();
    }
}
