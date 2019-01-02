/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
exports.getConsole = function() {
	var console = require('core/v3/console');
	return console;
};

exports.getContext = function() {
	var context = require('core/v3/context');
	return context;
};

exports.getEnv = function() {
	var env = require('core/v3/env');
	return env;
};

exports.getExtensions = function() {
	var extensions = require('core/v3/extensions');
	return extensions;
};

exports.getGlobals = function() {
	var globals = require('core/v3/globals');
	return globals;
};

exports.getJava = function() {
	var java = require('core/v3/java');
	return java;
};

