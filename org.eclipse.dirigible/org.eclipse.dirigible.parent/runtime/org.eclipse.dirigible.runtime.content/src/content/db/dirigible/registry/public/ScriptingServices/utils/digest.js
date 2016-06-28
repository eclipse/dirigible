/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ java engine */
/* eslint-env node, dirigible */

var streams = require("io/streams");

exports.md5 = function(input) {
	var output;
	if (engine === "nashorn") {
		output = $.getDigestUtils().class.static.md5(input);
	} else {
		output = $.getDigestUtils().md5(input);
	}
	return streams.toJavaScriptBytes(output);
};

exports.md5Hex = function(input) {
	var output;
	if (engine === "nashorn") {
		output = $.getDigestUtils().class.static.md5Hex(input);
	} else {
		output = $.getDigestUtils().md5Hex(input);
	}
	return toString(output);
};

exports.sha1 = function(input) {
	var output;
	if (engine === "nashorn") {
		output = $.getDigestUtils().class.static.sha(input);
	} else {
		output = $.getDigestUtils().sha(input);
	}
	return streams.toJavaScriptBytes(output);
};

exports.sha = function(input) {
	return exports.sha1(input);
};

exports.sha256 = function(input) {
	var output;
	if (engine === "nashorn") {
		output = $.getDigestUtils().class.static.sha256(input);
	} else {
		output = $.getDigestUtils().sha256(input);
	}
	return streams.toJavaScriptBytes(output);
};

exports.sha384 = function(input) {
	var output;
	if (engine === "nashorn") {
		output = $.getDigestUtils().class.static.sha384(input);
	} else {
		output = $.getDigestUtils().sha384(input);
	}
	return streams.toJavaScriptBytes(output);
};

exports.sha512 = function(input) {
	var output;
	if (engine === "nashorn") {
		output = $.getDigestUtils().class.static.sha512(input);
	} else {
		output = $.getDigestUtils().sha512(input);
	}
	return streams.toJavaScriptBytes(output);
};

exports.shaHex = function(input) {
	var output;
	if (engine === "nashorn") {
		output = $.getDigestUtils().class.static.shaHex(input);
	} else {
		output = $.getDigestUtils().shaHex(input);
	}
	return toString(output);
};

function toString(bytes) {
	return new java.lang.String(bytes);
}
