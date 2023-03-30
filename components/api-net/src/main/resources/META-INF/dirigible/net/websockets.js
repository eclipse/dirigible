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
exports.createWebsocket = function(uri, handler) {
	const session = org.eclipse.dirigible.components.api.websockets.WebsocketsFacade.createWebsocket(uri, handler);
	return new WebsocketClient(session, uri, handler);
};

exports.getClients = function() {
	const json = org.eclipse.dirigible.components.api.websockets.WebsocketsFacade.getClientsAsJson();
	return JSON.parse(json);
};

exports.getClient = function(id) {
	const native = org.eclipse.dirigible.components.api.websockets.WebsocketsFacade.getClient(id);
	if (native === null) {
		return null;
	}
	return new WebsocketClient(native.getSession(), native.getSession().getRequestURI(), native.getHandler());
};

exports.getClientByHandler = function(handler) {
	const native = org.eclipse.dirigible.components.api.websockets.WebsocketsFacade.getClientByHandler(handler);
	if (native === null) {
		return null;
	}
	return new WebsocketClient(native.getSession(), native.getSession().getRequestURI(), native.getHandler());
};

exports.getMessage = function() {
	return __context.get('message');
};

exports.getError = function() {
	return __context.get('error');
};

exports.getMethod = function() {
	return __context.get('method');
};

exports.isOnOpen = function() {
	return exports.getMethod() === "onopen";
};

exports.isOnMessage = function() {
	return exports.getMethod() === "onmessage";
};

exports.isOnError = function() {
	return exports.getMethod() === "onerror";
};

exports.isOnClose = function() {
	return exports.getMethod() === "onclose";
};

/**
 * WebsocketClient
 */
function WebsocketClient(session, uri, handler) {
    this.session = session;
	this.uri = uri;
	this.handler = handler;

	this.send = function(text) {
		if (!this.session || this.session === null) {
			console.error("Websocket Session is null");
		}
		this.session.send(uri, text);
	};

    this.close = function() {
		this.session.close();
	};

}
