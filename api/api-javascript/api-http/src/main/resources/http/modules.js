/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
exports.getClient = function() {
	var client = require('http/v4/client');
	return client;
};

exports.getRequest = function() {
	var request = require('http/v4/request');
	return request;
};

exports.getResponse = function() {
	var response = require('http/v4/response');
	return response;
};

exports.getSession = function() {
	var session = require('http/v4/session');
	return session;
};

exports.getUpload = function() {
	var upload = require('http/v4/upload');
	return upload;
};

exports.getRest = function() {
	var rest = require('http/v4/rs');
	return rest;
};

exports.getRestData = function() {
	var restData = require('http/v4/rs-data');
	return restData;
};
