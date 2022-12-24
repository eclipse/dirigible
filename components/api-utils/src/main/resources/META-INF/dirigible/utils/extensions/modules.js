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
exports.getContent = function() {

	return [
		{
			"name": "@dirigible/utils",
			"description": "Dirigible Utils module",
			"isPackageDescription": true,
			"dtsPath": "utils/extensions/utils.d.ts"
		},
		{
			"name": "utils/alphanumeric",
			"description": "Alphanumeric API",
			"api": "alphanumeric",
			"versionedPaths": [
				"utils/alphanumeric"
			],
			"pathDefault": "utils/alphanumeric"
		},
		{
			"name": "utils/base64",
			"description": "Base64 API",
			"api": "base64",
			"versionedPaths": [
				"utils/base64"
			],
			"pathDefault": "utils/base64"
		},
		{
			"name": "utils/digest",
			"description": "Digest API",
			"api": "digest",
			"versionedPaths": [
				"utils/digest"
			],
			"pathDefault": "utils/digest"
		},
		{
			"name": "utils/escape",
			"description": "Escape API",
			"api": "escape",
			"versionedPaths": [
				"utils/escape"
			],
			"pathDefault": "utils/escape"
		},
		{
			"name": "utils/hex",
			"description": "Hex API",
			"api": "hex",
			"versionedPaths": [
				"utils/hex"
			],
			"pathDefault": "utils/hex"
		},
		{
			"name": "utils/jsonpath",
			"description": "JsonPath API",
			"api": "jsonpath",
			"versionedPaths": [
				"utils/jsonpath"
			],
			"pathDefault": "utils/jsonpath"
		},
		{
			"name": "utils/url",
			"description": "URL API",
			"api": "url",
			"versionedPaths": [
				"utils/url"
			],
			"pathDefault": "utils/url"
		},
		{
			"name": "utils/uuid",
			"description": "UUID API",
			"api": "uuid",
			"versionedPaths": [
				"utils/uuid"
			],
			"pathDefault": "utils/uuid"
		},
		{
			"name": "utils/xml",
			"description": "XML API",
			"api": "xml",
			"versionedPaths": [
				"utils/xml"
			],
			"pathDefault": "utils/xml"
		},
		{
			"name": "utils/qrcode",
			"description": "QR Code Generator API",
			"api": "qrcode",
			"versionedPaths": [
				"utils/qrcode"
			],
			"pathDefault": "utils/qrcode"
		}
	]
		;
};

