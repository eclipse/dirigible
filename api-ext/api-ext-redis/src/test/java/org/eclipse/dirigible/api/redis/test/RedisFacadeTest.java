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
package org.eclipse.dirigible.api.redis.test;

import org.eclipse.dirigible.api.redis.RedisFacade;
import org.eclipse.dirigible.commons.config.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.Jedis;

import static org.junit.Assert.assertEquals;

public class RedisFacadeTest {

    @Before
    public void setUp(){
        GenericContainer redis = new GenericContainer(DockerImageName.parse("redis:5.0.3-alpine"))
                .withExposedPorts(6379);
        redis.start();

        String host = redis.getHost();
        Integer port = redis.getFirstMappedPort();

        Configuration.set("DIRIGIBLE_REDIS_CLIENT_URI", host + ":" + port);
    }

    @Test
    public void getClient(){
        Jedis client = RedisFacade.getClient();
        client.set("key", "value");
        assertEquals("value", client.get("key"));
    }
}
