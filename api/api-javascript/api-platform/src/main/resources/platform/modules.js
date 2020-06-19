/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
exports.getLifecycle = function() {
	var lifecycle = require("platform/v4/lifecycle");
	return lifecycle;
};

exports.getRegistry = function() {
	var registry = require("platform/v4/registry");
	return registry;
};

exports.getRepository = function() {
	var repository = require("platform/v4/repository");
	return repository;
};

exports.getWorkspace = function() {
	var workspace = require("platform/v4/workspace");
	return workspace;
};

exports.getEngines = function() {
	var engines = require("platform/v4/engines");
	return engines;
};

exports.getTemplateEngines = function() {
	var templateEngines = require("platform/v4/template-engines");
	return templateEngines;
};
