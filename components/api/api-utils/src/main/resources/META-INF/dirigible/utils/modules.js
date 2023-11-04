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
exports.getBase64 = function() {
	return require('utils/base64');
};

exports.getDigest = function() {
	return require('utils/digest');
};

exports.getHex = function() {
	return require('utils/hex');
};

exports.getUuid = function() {
	return require('utils/uuid');
};

exports.getXml = function() {
	return require('utils/xml');
};

exports.getUrl = function() {
	return require('utils/url');
};

exports.getEscape = function() {
	return require('utils/escape');
};

exports.getAlphanumeric = function() {
	return require('utils/alphanumeric');
};

exports.getJsonPath = function() {
	return require('utils/jsonpath');
};
