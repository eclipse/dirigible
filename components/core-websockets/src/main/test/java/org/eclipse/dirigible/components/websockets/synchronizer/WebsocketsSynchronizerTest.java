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
package org.eclipse.dirigible.components.websockets.synchronizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.eclipse.dirigible.components.websockets.domain.Websocket;
import org.eclipse.dirigible.components.websockets.synchronizer.WebsocketsSynchronizer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = { "org.eclipse.dirigible.components" })
@EntityScan("org.eclipse.dirigible.components")
public class WebsocketsSynchronizerTest {
	@Autowired
	private WebsocketsSynchronizer websocketsSynchronizer;


	@Test
    public void isAcceptedPath() {
		assertTrue(websocketsSynchronizer.isAccepted(Path.of("/a/b/c/e1.websocket"), null));
    }

	@Test
    public void isAcceptedArtefact() {
		assertTrue(websocketsSynchronizer.isAccepted(new Websocket("/a/b/c/w1.websocket", "name1", "description", "endpoint1", "handler1", "engine1").getType()));
    }

	@Test
    public void load() {
		String content = "{\"location\":\"/control/control.websocket\",\"handler\":\"control/handler.js\",\"endpoint\":\"mywebsocket\",\"description\":\"Control Websocket\",\"createdBy\":\"system\",\"createdAt\":\"2017-07-06T2:24:12+0000\"}";
		List<Websocket> list = websocketsSynchronizer.load("/test/test.websocket", content.getBytes());
		assertNotNull(list);
		assertEquals("/test/test.websocket", list.get(0).getLocation());
    }

	@SpringBootApplication
	static class TestConfiguration {
	}

}
