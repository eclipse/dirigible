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
exports.getConsole = function() {
	const console = require('core/v4/console');
	return console;
};

exports.getContext = function() {
	var context = require('core/v4/context');
	return context;
};

exports.getEnv = function() {
	var env = require('core/v4/env');
	return env;
};

exports.getExtensions = function() {
	var extensions = require('core/v4/extensions');
	return extensions;
};

exports.getGlobals = function() {
	var globals = require('core/v4/globals');
	return globals;
};

exports.getThreads = function() {
	var threads = require('core/v4/threads');
	return threads;
};

exports.getDestinations = function() {
	var destinations = require('core/v4/destinations');
	return destinations;
};

