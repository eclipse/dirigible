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
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.dirigible.commons.config.Configuration;

public class RepositoryCache implements IRepositoryCache {

	private static IRepositoryCache cache;
	
	private static final AtomicBoolean ENABLED = new AtomicBoolean(false);
	
	static {
		ServiceLoader<IRepositoryCache> services = ServiceLoader.load(IRepositoryCache.class);
		for (IRepositoryCache next : services) {
			cache = next;
			break;
		}
		ENABLED.set(Boolean.parseBoolean(Configuration.get(IRepository.DIRIGIBLE_REPOSITORY_CACHE_ENABLED, Boolean.FALSE.toString())));
	}

	@Override
	public byte[] get(String path) {
		if (ENABLED.get() && cache != null) {
			return cache.get(path);
		}
		return null;
	}

	@Override
	public void put(String path, byte[] content) {
		if (ENABLED.get() && cache != null) {
			cache.put(path, content);
		}
	}

	@Override
	public void remove(String path) {
		if (ENABLED.get() && cache != null) {
			cache.remove(path);
		}
	}

	@Override
	public void clear() {
		if (ENABLED.get() && cache != null) {
			cache.clear();
		}
	}
	
	public static void enable() {
		Configuration.set(IRepository.DIRIGIBLE_REPOSITORY_CACHE_ENABLED, Boolean.TRUE.toString());
		ENABLED.set(true);
	}

	public static void disable() {
		Configuration.set(IRepository.DIRIGIBLE_REPOSITORY_CACHE_ENABLED, Boolean.FALSE.toString());
		ENABLED.set(false);
		if (ENABLED.get() && cache != null) {
			cache.clear();
		}
	}
	
	public static boolean isEnabled() {
		return ENABLED.get();
	}

}
