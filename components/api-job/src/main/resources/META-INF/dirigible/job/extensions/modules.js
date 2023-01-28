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
exports.getContent = function () {
    return [
		  {
		    "name": "@dirigible/job",
		    "description": "Dirigible Job Module",
		    "isPackageDescription": true,
		    "dtsPath": "job/extensions/job.d.ts"
		  },
		  {
		    "name": "job/scheduler",
		    "description": "Job Process API",
		    "api": "scheduler",
		    "versionedPaths": [
		      "job/scheduler"
		    ],
		    "pathDefault": "job/scheduler"
		  }
		];
};

