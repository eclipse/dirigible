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
exports.getClient = function() {
	return require('http/v4/client');
};

exports.getRequest = function() {
	return require('http/v4/request');
};

exports.getResponse = function() {
	return require('http/v4/response');
};

exports.getSession = function() {
	return require('http/v4/session');
};

exports.getUpload = function() {
	return require('http/v4/upload');
};

exports.getRest = function() {
	return require('http/v4/rs');
};

exports.getRestData = function() {
	return require('http/v4/rs-data');
};
