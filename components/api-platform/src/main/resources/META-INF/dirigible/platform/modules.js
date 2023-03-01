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
exports.getLifecycle = function() {
	const lifecycle = require("platform/lifecycle");
	return lifecycle;
};

exports.getRegistry = function() {
	const registry = require("platform/registry");
	return registry;
};

exports.getRepository = function() {
	const repository = require("platform/repository");
	return repository;
};

exports.getWorkspace = function() {
	const workspace = require("platform/workspace");
	return workspace;
};

exports.getEngines = function() {
	const engines = require("platform/engines");
	return engines;
};

exports.getProblems = function() {
	const problems = require("platform/problems");
	return problems;
};

