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
			"name": "@dirigible/bpm",
			"description": "Dirigible BPM Module",
			"isPackageDescription": true,
			"dtsPath": "bpm/extensions/bpm.d.ts"
		},
		{
			"name": "bpm/deployer",
			"description": "BPM Deployer API",
			"api": "deployer",
			"versionedPaths": [
				"bpm/deployer"
			],
			"pathDefault": "bpm/deployer"
		},
		{
			"name": "bpm/process",
			"description": "BPM Process API",
			"api": "process",
			"versionedPaths": [
				"bpm/process"
			],
			"pathDefault": "bpm/process"
		},
		{
			"name": "bpm/tasks",
			"description": "BPM Tasks API",
			"api": "tasks",
			"versionedPaths": [
				"bpm/tasks"
			],
			"pathDefault": "bpm/tasks"
		}
	];
};

