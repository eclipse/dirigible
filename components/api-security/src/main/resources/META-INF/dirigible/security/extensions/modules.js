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
			"name": "@dirigible/security",
			"description": "Dirigible Security module",
			"isPackageDescription": true,
			"dtsPath": "security/extensions/security.d.ts"
		},
		{
			"name": "security/user",
			"description": "User API",
			"api": "user",
			"versionedPaths": [
				"security/user"
			],
			"pathDefault": "security/user"
		},
		{
			"name": "security/oauth",
			"description": "OAuth API",
			"api": "oauth",
			"versionedPaths": [
				"security/oauth"
			],
			"pathDefault": "security/oauth"
		}
	];
};
