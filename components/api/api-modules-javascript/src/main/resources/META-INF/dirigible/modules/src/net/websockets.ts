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

class Websocket{

	public static createWebsocket(uri: string, handler: string): WebsocketClient {
		const session = WebsocketsFacade.createWebsocket(uri, handler);
		return new WebsocketClient(session, uri, handler);
	};

	public static getClients(): any {
		const json = WebsocketsFacade.getClientsAsJson();
		return JSON.parse(json);
	};

	public static getClient(id: string): WebsocketClient | null {
		const native = WebsocketsFacade.getClient(id);
		if (native === null) {
			return null;
		}
		return new WebsocketClient(native.getSession(), native.getSession().getRequestURI(), native.getHandler());
	};

	public static getClientByHandler(handler: string): WebsocketClient | null {
		const native = WebsocketsFacade.getClientByHandler(handler);
		if (native === null) {
			return null;
		}
		return new WebsocketClient(native.getSession(), native.getSession().getRequestURI(), native.getHandler());
	};

	public static getMessage(): any {
		return __context.get('message');
	};

	public static getError(): any {
		return __context.get('error');
	};

	public static getMethod(): any {
		return __context.get('method');
	};

	public static isOnOpen(): boolean {
		return this.getMethod() === "onopen";
	};

	public static isOnMessage(): boolean {
		return this.getMethod() === "onmessage";
	};

	public static isOnError(): boolean {
		return this.getMethod() === "onerror";
	};

	public static isOnClose(): boolean {
		return this.getMethod() === "onclose";
	};

}

/**
 * WebsocketClient
 */
class WebsocketClient {

	constructor(private session, private uri, private handler) { }

	send(text: string) {
		if (!this.session || this.session === null) {
			console.error("Websocket Session is null");
		}
		this.session.send(this.uri, text);
	};

	close() {
		this.session.close();
	};

}
