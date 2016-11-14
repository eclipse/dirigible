/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ java */
/* eslint-env node, dirigible */

var errorUtils = require('utils/error');

exports.assertTrue = function(condition, message) {
	if (!condition) {
		var errorMessage = getMessage(message) + ' Expected [true], but was [' + condition + ']!';
		console.error(errorMessage);
		throw errorUtils.createError(errorMessage);
	}
};

exports.assertFalse = function(condition, message) {
	if (condition) {
		var errorMessage = getMessage(message) + ' Expected [false], but was [' + condition + ']!';
		console.error(errorMessage);
		throw errorUtils.createError(errorMessage);
	}
};

exports.assertEquals = function(o1, o2, message) {
	if (JSON.stringify(o1) !== JSON.stringify(o2) ) {
		var errorMessage = getMessage(message) + ' Expected [' + o1 + '], but was [' + o2+ ']!';
		console.error(errorMessage);
		throw errorUtils.createError(errorMessage);
	}
};

exports.assertNull = function(o, message) {
	if (o !== undefined && o !== null) {
		var errorMessage = getMessage(message) + ' Expected [null], but was [' + o + ']!';
		console.error(errorMessage);
		throw errorUtils.createError(errorMessage);
	}
};

exports.assertNotNull = function(o, message) {
	if (o === undefined || o === null) {
		var errorMessage = getMessage(message) + ' Expected [not null], but was [' + o + ']!';
		console.error(errorMessage);
		throw errorUtils.createError(errorMessage);
	}
};

exports.fail = function(message) {
	var errorMessage = getMessage(message) + ' Expected conditions were not met!';
	console.error(errorMessage);
	throw errorUtils.createError(errorMessage);
};

function getMessage(message) {
	return message = message !== undefined && message !== null ? message : '';
}