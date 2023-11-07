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
			"name": "@dirigible/io",
			"description": "Dirigible IO module",
			"isPackageDescription": true,
			"dtsPath": "io/extensions/io.d.ts"
		},
		{
			"name": "io/bytes",
			"description": "Bytes API",
			"api": "bytes",
			"versionedPaths": [
				"io/bytes"
			],
			"pathDefault": "io/bytes"
		},
		{
			"name": "io/files",
			"description": "Files API",
			"api": "files",
			"versionedPaths": [
				"io/files"
			],
			"pathDefault": "io/files"
		},
		{
			"name": "io/ftp",
			"description": "FTP API",
			"api": "ftp",
			"versionedPaths": [
				"io/ftp"
			],
			"pathDefault": "io/ftp"
		},
		{
			"name": "io/image",
			"description": "Image API",
			"api": "image",
			"versionedPaths": [
				"io/image"
			],
			"pathDefault": "io/image"
		},
		{
			"name": "io/streams",
			"description": "Streams API",
			"api": "streams",
			"versionedPaths": [
				"io/streams"
			],
			"pathDefault": "io/streams"
		},
		{
			"name": "io/zip",
			"description": "ZIP API",
			"api": "zip",
			"versionedPaths": [
				"io/zip"
			],
			"pathDefault": "io/zip"
		}
	];
};
