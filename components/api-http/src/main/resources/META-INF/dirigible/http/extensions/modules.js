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
exports.getContent = function() {
	return [
		{
			"name": "@dirigible/http",
			"description": "Dirigible HTTP module",
			"isPackageDescription": true,
			"dtsPath": "http/extensions/http.d.ts"
		},
		{
			"name": "http/client",
			"description": "HTTP Client API",
			"api": "client",
			"versionedPaths": [
				"http/client"
			],
			"pathDefault": "http/client"
		},
		{
			"name": "http/clientAsync",
			"description": "HTTP ClientAsync API",
			"api": "clientAsync",
			"versionedPaths": [
				"http/clientAsync"
			],
			"pathDefault": "http/clientAsync"
		},
		{
			"name": "http/request",
			"description": "HTTP Request API",
			"api": "request",
			"versionedPaths": [
				"http/request"
			],
			"pathDefault": "http/request"
		},
		{
			"name": "http/response",
			"description": "HTTP Response API",
			"api": "response",
			"versionedPaths": [
				"http/response"
			],
			"pathDefault": "http/response"
		},
		{
			"name": "http/session",
			"description": "HTTP Session API",
			"api": "session",
			"versionedPaths": [
				"http/session"
			],
			"pathDefault": "http/session"
		},
		{
			"name": "http/upload",
			"description": "HTTP Upload API",
			"api": "upload",
			"versionedPaths": [
				"http/upload"
			],
			"pathDefault": "http/upload"
		},
		{
			"name": "http/rs",
			"description": "HTTP RS API",
			"api": "rs",
			"versionedPaths": [
				"http/rs"
			],
			"pathDefault": "http/rs"
		}
	];
};
