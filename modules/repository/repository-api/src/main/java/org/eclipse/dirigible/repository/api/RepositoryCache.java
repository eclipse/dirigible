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

import java.util.ServiceLoader;

import org.eclipse.dirigible.commons.config.Configuration;

public class RepositoryCache implements IRepositoryCache {

	private static IRepositoryCache cache;

	public RepositoryCache() {
		initialize();
	}

	private void initialize() {
		if (Boolean.parseBoolean(Configuration.get(IRepository.DIRIGIBLE_REPOSITORY_CACHE_ENABLED, Boolean.TRUE.toString()))) {
			ServiceLoader<IRepositoryCache> services = ServiceLoader.load(IRepositoryCache.class);
			for (IRepositoryCache next : services) {
				cache = next;
				break;
			}
		}
	}

	@Override
	public byte[] get(String path) {
		if (cache != null) {
			return cache.get(path);
		}
		return null;
	}

	@Override
	public void put(String path, byte[] content) {
		if (cache != null) {
			cache.put(path, content);
		}
	}

	@Override
	public void remove(String path) {
		if (cache != null) {
			cache.remove(path);
		}
	}

	@Override
	public void clear() {
		if (cache != null) {
			cache.clear();
		}
	}

	@Override
	public void enable() {
		Configuration.set(IRepository.DIRIGIBLE_REPOSITORY_CACHE_ENABLED, Boolean.TRUE.toString());
		initialize();
	}

	@Override
	public void disable() {
		Configuration.set(IRepository.DIRIGIBLE_REPOSITORY_CACHE_ENABLED, Boolean.FALSE.toString());
		initialize();
	}

}
