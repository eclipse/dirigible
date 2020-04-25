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
exports.getConsole = function() {
	var console = require('core/v4/console');
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

exports.getTemplateEngine = function() {
	var templateEngine = require('core/v4/template-engine');
	return templateEngine;
}

exports.getDestinations = function() {
	var destinations = require('core/v4/destinations');
	return destinations;
};

