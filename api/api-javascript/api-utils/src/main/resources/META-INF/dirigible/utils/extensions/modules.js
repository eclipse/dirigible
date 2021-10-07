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
              "name": "@dirigible/utils",
              "description": "Dirigible Utils module",
              "isPackageDescription": true
            },
    {
		name: "utils/v4/alphanumeric",
		description: "Alphanumeric API"
	}, {
		name: "utils/v4/base64",
		description: "Base64 API"
	}, {
		name: "utils/v4/digest",
		description: "Digest API"
	}, {
		name: "utils/v4/escape",
		description: "Escape API"
	}, {
		name: "utils/v4/hex",
		description: "Hex API"
	}, {
		name: "utils/v4/jsonpath",
		description: "JsonPath API"
	}, {
		name: "utils/v4/url",
		description: "URL API"
	}, {
		name: "utils/v4/uuid",
		description: "UUID API"
	}, {
		name: "utils/v4/xml",
		description: "XML API"
	}];
};

