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
exports.getContent = function () {
	return [
		  {
		    "name": "@dirigible/platform",
		    "description": "Dirigible Platform module",
		    "isPackageDescription": true,
		    "dtsPath": "platform/extensions/platform.d.ts"
		  },
		  {
		    "name": "platform/lifecycle",
		    "description": "Lifecycle API",
		    "api": "lifecycle",
		    "versionedPaths": [
		      "platform/lifecycle"
		    ],
		    "pathDefault": "platform/lifecycle"
		  },
		  {
		    "name": "platform/registry",
		    "description": "Registry API",
		    "api": "registry",
		    "versionedPaths": [
		      "platform/registry"
		    ],
		    "pathDefault": "platform/registry"
		  },
		  {
		    "name": "platform/repository",
		    "description": "Repository API",
		    "api": "repository",
		    "versionedPaths": [
		      "platform/repository"
		    ],
		    "pathDefault": "platform/repository"
		  },
		  {
		    "name": "platform/workspace",
		    "description": "Workspace API",
		    "api": "workspace",
		    "versionedPaths": [
		      "platform/workspace"
		    ],
		    "pathDefault": "platform/workspace"
		  },
		  {
		    "name": "platform/engines",
		    "description": "Engines API",
		    "api": "engines",
		    "versionedPaths": [
		      "platform/engines"
		    ],
		    "pathDefault": "platform/engines"
		  },
		  {
		    "name": "platform/problems",
		    "description": "Problems API",
		    "api": "problems",
		    "versionedPaths": [
		      "platform/problems"
		    ],
		    "pathDefault": "platform/problems"
		  }
		];
};
