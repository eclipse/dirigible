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
exports.getBase64 = function() {
	var base64 = require('utils/v4/base64');
	return base64;
};

exports.getDigest = function() {
	var digest = require('utils/v4/digest');
	return digest;
};

exports.getHex = function() {
	var hex = require('utils/v4/hex');
	return hex;
};

exports.getUuid = function() {
	var uuid = require('utils/v4/uuid');
	return uuid;
};

exports.getXml = function() {
	var xml = require('utils/v4/xml');
	return xml;
};

exports.getUrl = function() {
	var url = require('utils/v4/url');
	return url;
};

exports.getEscape = function() {
	var url = require('utils/v4/escape');
	return url;
};

exports.getAlphanumeric = function() {
	var alphanumeric = require('utils/v4/alphanumeric');
	return alphanumeric;
};

exports.getJsonPath = function() {
	var jsonpath = require('utils/v4/jsonpath');
	return jsonpath;
};
