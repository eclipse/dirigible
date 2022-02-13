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
package org.eclipse.dirigible.repository.api;

import java.util.concurrent.TimeUnit;

import org.eclipse.dirigible.commons.config.Configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class CaffeineRepositoryCache implements IRepositoryCache {

	private static Cache<String, byte[]> cache;

	public CaffeineRepositoryCache() {
		initCache();
	}

	private static void initCache() {
		long timePolicy = Long.parseLong(Configuration.get(IRepository.DIRIGIBLE_REPOSITORY_CACHE_TIME_LIMIT_IN_MINUTES, "10"));
		long sizePolicy = Long.parseLong(Configuration.get(IRepository.DIRIGIBLE_REPOSITORY_CACHE_SIZE_LIMIT_IN_MEGABYTES, "100"));
		cache = Caffeine.newBuilder()
				.expireAfterAccess(timePolicy, TimeUnit.MINUTES)
				.maximumWeight(sizePolicy * 1024 * 1024)
				.weigher((String k, byte[] v) -> v.length)
				.build();
	}

	public static Cache<String, byte[]> getInternalCache() {
		if (cache == null) {
			initCache();
		}

		return cache;
	}

	@Override
	public byte[] get(String path) {
		return cache.getIfPresent(path);
	}

	@Override
	public void put(String path, byte[] content) {
		if (content != null) {
			cache.put(path, content);
		}
	}

	@Override
	public void remove(String path) {
		cache.invalidate(path);
	}

	@Override
	public void clear() {
		cache.invalidateAll();
	}

}
