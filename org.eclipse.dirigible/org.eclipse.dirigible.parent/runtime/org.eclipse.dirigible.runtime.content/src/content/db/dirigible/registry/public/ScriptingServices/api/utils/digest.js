/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ java */
/* eslint-env node, dirigible */

exports.md5 = function(input) {
	var output = $.getDigestUtils().md5(input);
	return toString(output);
};

exports.md5Hex = function(input) {
	var output = $.getDigestUtils().md5Hex(input);
	return toString(output);
};

exports.sha = function(input) {
	var output = $.getDigestUtils().sha(input);
	return toString(output);
};

exports.sha256 = function(input) {
	var output = $.getDigestUtils().sha256(input);
	return toString(output);
};

exports.sha384 = function(input) {
	var output = $.getDigestUtils().sha384(input);
	return toString(output);
};

exports.sha512 = function(input) {
	var output = $.getDigestUtils().sha512(input);
	return toString(output);
};

exports.shaHex = function(input) {
	var output = $.getDigestUtils().shaHex(input);
	return toString(output);
};

function toString(bytes) {
	return new java.lang.String(bytes);
}
