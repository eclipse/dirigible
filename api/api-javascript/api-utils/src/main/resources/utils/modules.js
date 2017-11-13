/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

exports.getBase64 = function() {
	var base64 = require('utils/v3/base64');
	return base64;
};

exports.getDigest = function() {
	var digest = require('utils/v3/digest');
	return digest;
};

exports.getHex = function() {
	var hex = require('utils/v3/hex');
	return hex;
};

exports.getUuid = function() {
	var uuid = require('utils/v3/uuid');
	return uuid;
};

exports.getXml = function() {
	var xml = require('utils/v3/xml');
	return xml;
};

exports.getUrl = function() {
	var url = require('utils/v3/url');
	return url;
};

exports.getEscape = function() {
	var url = require('utils/v3/escape');
	return url;
};
