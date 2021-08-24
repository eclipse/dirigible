/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
exports.getContent = function() {
	return [{
		name: "http/v4/client",
		description: "HTTP Client API"
	}, {
		name: "http/v4/clientAsync",
		description: "HTTP Client Async API"
	}, {
		name: "http/v4/request",
		description: "HTTP Request API"
	}, {
		name: "http/v4/response",
		description: "HTTP Response API"
	}, {
		name: "http/v4/rs-data",
		description: "HTTP RS-Data API"
	}, {
		name: "http/v4/rs",
		description: "HTTP RS API"
	}, {
		name: "http/v4/session",
		description: "HTTP Session API"
	}, {
		name: "http/v4/upload",
		description: "HTTP Upload API"
	}];
};
