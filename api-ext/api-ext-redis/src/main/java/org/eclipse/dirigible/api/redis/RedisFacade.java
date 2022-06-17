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
package org.eclipse.dirigible.api.redis;

import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;

import org.eclipse.dirigible.commons.config.Configuration;
import redis.clients.jedis.Jedis;

public class RedisFacade implements IScriptingFacade {
	private static final String DIRIGIBLE_REDIS_CLIENT_URI = "DIRIGIBLE_REDIS_CLIENT_URI";
	private static final String CLIENT_URI = "localhost:6379";
	
	public static Jedis getClient() {
		
		String[] splitUri = Configuration.get(DIRIGIBLE_REDIS_CLIENT_URI, CLIENT_URI).split(":");

		String host = splitUri[0];
		int port = Integer.parseInt(splitUri[1]);
		
		Jedis redisClient = new Jedis(host, port);
		
		return redisClient;
	}
}
