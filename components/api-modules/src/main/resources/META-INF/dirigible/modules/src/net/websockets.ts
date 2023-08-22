/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */

const WebsocketsFacade = Java.type("org.eclipse.dirigible.components.api.websockets.WebsocketsFacade");

export function createWebsocket(uri, handler) {
	const session = WebsocketsFacade.createWebsocket(uri, handler);
	return new WebsocketClient(session, uri, handler);
};

export function getClients() {
	const json = WebsocketsFacade.getClientsAsJson();
	return JSON.parse(json);
};

export function getClient(id) {
	const native = WebsocketsFacade.getClient(id);
	if (native === null) {
		return null;
	}
	return new WebsocketClient(native.getSession(), native.getSession().getRequestURI(), native.getHandler());
};

export function getClientByHandler(handler) {
	const native = WebsocketsFacade.getClientByHandler(handler);
	if (native === null) {
		return null;
	}
	return new WebsocketClient(native.getSession(), native.getSession().getRequestURI(), native.getHandler());
};

export function getMessage() {
	return __context.get('message');
};

export function getError() {
	return __context.get('error');
};

export function getMethod() {
	return __context.get('method');
};

export function isOnOpen() {
	return getMethod() === "onopen";
};

export function isOnMessage() {
	return getMethod() === "onmessage";
};

export function isOnError() {
	return getMethod() === "onerror";
};

export function isOnClose() {
	return getMethod() === "onclose";
};

/**
 * WebsocketClient
 */
class WebsocketClient {

	constructor(private session, private uri, private handler) { }

	send(text) {
		if (!this.session || this.session === null) {
			console.error("Websocket Session is null");
		}
		this.session.send(this.uri, text);
	};

	close() {
		this.session.close();
	};

}
