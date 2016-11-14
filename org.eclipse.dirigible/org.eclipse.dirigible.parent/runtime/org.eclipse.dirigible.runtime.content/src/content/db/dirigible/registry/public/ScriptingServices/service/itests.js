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

var httpClient = require('net/http/client');
var request = require('net/http/request');
var response = require('net/http/response');

var setUpFunction;
var cleanUpFunction;

exports.before = function(setUp) {
	setUpFunction = setUp;
};

exports.after = function(cleanUp) {
	cleanUpFunction = cleanUp;
};

exports.execute = function(testPaths) {
	var testResult = {
		'status': 'OK',
		'tests': {
			'count': 0,
			'failed': {
				'count': 0,
				'failureInfo': []
			}
			
		}
	};

	for (var i = 0 ; i < testPaths.length; i ++) {
		try {
			beforeTest();
	
			var httpResponse = httpClient.get(getTestURL(testPaths[i]));
			var testData = JSON.parse(httpResponse.data);
			testResult.tests.count += testData.tests.count;
			if (testData.status !== 'OK') {
				testResult.status = 'Failed';
				testResult.tests.failed.count += testData.tests.failed.count;
				testResult.tests.failed.failureInfo = testResult.tests.failed.failureInfo.concat(testData.tests.failed.failureInfo);
			}
	
			afterTest();
		} catch (e) {
			afterTest();
			testResult.status = 'Error';
			testResult.tests.failed.count ++;
			testResult.tests.failed.failureInfo.push({
				'path': testPaths[i],
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

function getTestURL(path) {
	var requestInfo = request.getInfo();
	return requestInfo.scheme + '://' + requestInfo.serverName + ':' + requestInfo.serverPort + requestInfo.contextPath + path;
}