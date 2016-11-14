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

var streams = require('io/streams');

exports.decode = function(input) {
	var inputBytes = getBytes(input);
	
	var outputBytes;
	if (engine === "nashorn") {
		outputBytes = $.getBase64Utils().class.static.decodeBase64(inputBytes);
	} else {
		outputBytes = $.getBase64Utils().decodeBase64(inputBytes);
	}
	
	return toString(outputBytes);
};

exports.encode = function(input) {
	var inputBytes = getBytes(input);
	var outputBytes;
	if (engine === "nashorn") {
		outputBytes = $.getBase64Utils().class.static.encodeBase64(inputBytes);
	} else {
		outputBytes = $.getBase64Utils().encodeBase64(inputBytes);
	}
	return toString(outputBytes);
};

function getBytes(input) {
	return new java.lang.String(input).getBytes();
}

function toString(bytes) {
	return String.fromCharCode.apply(String, streams.toJavaScriptBytes(bytes));
}
