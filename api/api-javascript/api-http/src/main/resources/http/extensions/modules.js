/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
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