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
			"name": "@dirigible/core",
			"description": "Dirigible Core Module",
			"isPackageDescription": true,
			"dtsPath": "core/extensions/core.d.ts"
		},
		{
			"name": "core/configurations",
			"description": "Configurations API",
			"api": "configurations",
			"versionedPaths": [
				"core/configurations"
			],
			"pathDefault": "core/configurations"
		},
		{
			"name": "core/context",
			"description": "Context API",
			"api": "context",
			"versionedPaths": [
				"core/context"
			],
			"pathDefault": "core/context"
		},
		{
			"name": "core/env",
			"description": "Env API",
			"api": "env",
			"versionedPaths": [
				"core/env"
			],
			"pathDefault": "core/env"
		},
		{
			"name": "core/globals",
			"description": "Globals API",
			"api": "globals",
			"versionedPaths": [
				"core/globals"
			],
			"pathDefault": "core/globals"
		}
	];
};
