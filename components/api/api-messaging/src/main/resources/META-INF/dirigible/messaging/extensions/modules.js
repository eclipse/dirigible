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
	    "name": "@dirigible/messaging",
	    "description": "Dirigible Messaging module",
	    "isPackageDescription": true,
	    "dtsPath": "messaging/extensions/messaging.d.ts"
	  },
	  {
	    "name": "messaging/consumer",
	    "description": "Messaging Consumer API",
	    "api": "consumer",
	    "versionedPaths": [
	      "messaging/consumer"
	    ],
	    "pathDefault": "messaging/consumer"
	  },
	  {
	    "name": "messaging/producer",
	    "description": "Messaging Producer API",
	    "api": "producer",
	    "versionedPaths": [
	      "messaging/producer"
	    ],
	    "pathDefault": "messaging/producer"
	  }
	];
};
