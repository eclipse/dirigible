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
exports.getClient = function() {
	var client = require('http/v3/client');
	return client;
};

exports.getRequest = function() {
	var request = require('http/v3/request');
	return request;
};

exports.getResponse = function() {
	var response = require('http/v3/response');
	return response;
};

exports.getSession = function() {
	var session = require('http/v3/session');
	return session;
};

exports.getUpload = function() {
	var upload = require('http/v3/upload');
	return upload;
};

exports.getRest = function() {
	var rest = require('http/v3/rs');
	return rest;
};

exports.getRestData = function() {
	var restData = require('http/v3/rs-data');
	return restData;
};
