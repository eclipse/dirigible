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
		name: "bpm/v4/deployer",
		description: "BPM Deployer API"
	}, {
		name: "bpm/v4/process",
		description: "BPM Process API"
	}, {
		name: "bpm/v4/tasks",
		description: "BPM Tasks API"
	}];
};
