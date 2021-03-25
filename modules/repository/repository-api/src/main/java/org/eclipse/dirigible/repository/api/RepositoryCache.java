/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.repository.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.commons.config.Configuration;

public class RepositoryCache {
	
	private static Map<String, byte[]> cache;
	
	public RepositoryCache() {
		initialize();
	}

	private static void initialize() {
		if (!Boolean.parseBoolean(Configuration.get(IRepository.DIRIGIBLE_REPOSITORY_DISABLE_CACHE, "false"))) {
			if (cache == null) {
				cache = Collections.synchronizedMap(new HashMap<String, byte[]>());
			}
		} else {
			cache = null;
		}
	}
	
	public byte[] get(String path) {
		if (cache != null) {
			return cache.get(path);
		}
		return null;
	}
	
	public void put(String path, byte[] content) {
		if (cache != null) {
			cache.put(path, content);
		}
	}
	
	public void remove(String path) {
		if (cache != null) {
			cache.remove(path);
		}
	}
	
	public void clear() {
		if (cache != null) {
			cache.clear();
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
