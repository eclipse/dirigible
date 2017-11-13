/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

var java = require('core/v3/java');

exports.md5 = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.DigestFacade', 'md5', [JSON.stringify(input)]);
	return output;
};

exports.md5Hex = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.DigestFacade', 'md5Hex', [JSON.stringify(input)]);
	return output;
};

exports.sha1 = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.DigestFacade', 'sha1', [JSON.stringify(input)]);
	return output;
};

exports.sha256 = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.DigestFacade', 'sha256', [JSON.stringify(input)]);
	return output;
};

exports.sha384 = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.DigestFacade', 'sha384', [JSON.stringify(input)]);
	return output;
};

exports.sha512 = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.DigestFacade', 'sha512', [JSON.stringify(input)]);
	return output;
};

exports.sha1Hex = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.DigestFacade', 'sha1Hex', [JSON.stringify(input)]);
	return output;
};
