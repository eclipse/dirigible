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
package org.eclipse.dirigible.services.spring.websockets;

import javax.websocket.server.ServerEndpoint;

import org.eclipse.dirigible.runtime.ide.console.service.ConsoleWebsocketService;
import org.springframework.stereotype.Component;

/**
 * The Class SpringBootConsoleWebsocketService.
 */
@Component
@ServerEndpoint("/websockets/v4/ide/console")
public class SpringBootConsoleWebsocketService extends ConsoleWebsocketService {

}
