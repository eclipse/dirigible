/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.repository.api;

import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.eclipse.dirigible.commons.config.Configuration;

public class RepositoryCache {
	
	private static Cache<String, byte[]> cache;
	
	public RepositoryCache() {
		initialize();
	}

	private static void initialize() {
		if (!Boolean.parseBoolean(Configuration.get(IRepository.DIRIGIBLE_REPOSITORY_DISABLE_CACHE, "false"))) {
			if (cache == null) {
				String timePolicy = Configuration.get(IRepository.DIRIGIBLE_REPOSITORY_CACHE_TIME_LIMIT_IN_MINUTES, "10");
				String sizePolicy = Configuration.get(IRepository.DIRIGIBLE_REPOSITORY_CACHE_SIZE_LIMIT_IN_MEGABYTES, "100");

				cache = Caffeine.newBuilder()
						.expireAfterAccess(Long.parseLong(timePolicy), TimeUnit.MINUTES)
						.maximumWeight(Long.parseLong(sizePolicy) * 1024 * 1024)
						.weigher((String k, byte[] v) -> v.length)
						.build();
			}
		} else {
			cache = null;
		}
	}
	
	public byte[] get(String path) {
		if (cache != null) {
			return cache.getIfPresent(path);
		}
		return null;
	}
	
	public void put(String path, byte[] content) {
		if (cache != null && content != null) {
			cache.put(path, content);
		}
	}
	
	public void remove(String path) {
		if (cache != null) {
			cache.invalidate(path);
		}
	}
	
	public void clear() {
		if (cache != null) {
			cache.invalidateAll();
		}
	}
	
	public static void enable() {
		Configuration.set(IRepository.DIRIGIBLE_REPOSITORY_DISABLE_CACHE, "false");
		initialize();
	}
	
	public static void disable() {
		Configuration.set(IRepository.DIRIGIBLE_REPOSITORY_DISABLE_CACHE, "true");
		initialize();
	}

}
