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
	return require('utils/v4/base64');
};

exports.getDigest = function() {
	return require('utils/v4/digest');
};

exports.getHex = function() {
	return require('utils/v4/hex');
};

exports.getUuid = function() {
	return require('utils/v4/uuid');
};

exports.getXml = function() {
	return require('utils/v4/xml');
};

exports.getUrl = function() {
	return require('utils/v4/url');
};

exports.getEscape = function() {
	return require('utils/v4/escape');
};

exports.getAlphanumeric = function() {
	return require('utils/v4/alphanumeric');
};

exports.getJsonPath = function() {
	return require('utils/v4/jsonpath');
};
