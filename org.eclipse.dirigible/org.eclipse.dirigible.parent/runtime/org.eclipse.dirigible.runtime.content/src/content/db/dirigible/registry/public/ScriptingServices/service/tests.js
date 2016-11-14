/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ */
/* eslint-env node, dirigible */

var response = require('net/http/response');

var setUpFunction;
var cleanUpFunction;

exports.before = function(setUp) {
	setUpFunction = setUp;
};

exports.after = function(cleanUp) {
	cleanUpFunction = cleanUp;
};

exports.execute = function(testFunctions) {
	var testResult = {
		'status': 'OK',
		'tests': {
			'count': testFunctions.length,
			'failed': {
				'count': 0,
				'failureInfo': []
			}
			
		}
	};
	
	for (var i = 0; i < testFunctions.length; i ++) {
		try {
			beforeTest();
			testFunctions[i]();
			afterTest();
		} catch (e) {
			afterTest();
			testResult.status = 'Failed';
			testResult.tests.failed.count ++;
			testResult.tests.failed.failureInfo.push({
				'function': testFunctions[i].name + '()',
				'error': e
			});
		}
	}
	return testResult;
};

exports.getHttpStatus = function(testResult) {
	if (isValidJSON(testResult) && testResult.status === 'OK') {
		return response.OK;
	}
	return response.EXPECTATION_FAILED;
};

exports.getText = function(testResult) {
	if (isValidJSON(testResult)) {
		if (testResult.tests.failed.count === 0) {
			testResult.tests.failed = undefined;
		}
		return JSON.stringify(testResult, null, 2);
	}
	return testResult;
};

function beforeTest() {
	if (setUpFunction !== undefined && setUpFunction !== null) {
		setUpFunction();
	}
}

function afterTest() {
	if (cleanUpFunction !== undefined && cleanUpFunction !== null) {
		cleanUpFunction();
	}
}
function isValidJSON(data) {
	try {
		JSON.stringify(data);
		return true;
	} catch(e) {
		return false;
	}
}
