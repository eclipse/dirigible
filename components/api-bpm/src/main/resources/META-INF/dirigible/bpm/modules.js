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
exports.getDeployer = function() {
	var deployer = require('bpm/deployer');
	return deployer;
};

exports.getProcess = function() {
	var process = require('bpm/process');
	return process;
};

exports.getTasks = function() {
	var tasks = require('bpm/tasks');
	return tasks;
};
