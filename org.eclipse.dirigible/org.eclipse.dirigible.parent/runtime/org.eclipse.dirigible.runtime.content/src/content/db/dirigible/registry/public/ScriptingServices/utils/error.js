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

exports.createError = function(errorMessage) {
	var internalStackTrace = new java.lang.Exception().getStackTrace();
	var stackTrace = [];
	for (var i = internalStackTrace.length -1; i > 0 ; i --) {
		var stackTraceItem = internalStackTrace[i];
		var className = internalStackTrace[i].getClassName();
		var message = null;

		if (className.indexOf('org.mozilla.javascript.gen') >= 0) {
			message = constructMessage(getFileName(stackTraceItem), getLineNumber(stackTraceItem), getMethodName(stackTraceItem));
		} else if (className.indexOf('jdk.nashorn.internal.scripts') >= 0) {
			message = constructMessage(getFileName(stackTraceItem), getLineNumber(stackTraceItem), getMethodName(stackTraceItem));
		}
		if (message) {
			console.error(message);
			stackTrace.push(message);
		}
	}

	// Fallback for unsupported runtime engines
	if (stackTrace.length === 0) {
		for (i = internalStackTrace.length -1 ; i > 0 ; i --) {
			message = internalStackTrace[i].toString();
			console.error(message);
			stackTrace.push(message);
		}
	}

	var error = new Error(errorMessage);
	error.stackTrace = stackTrace;
	return error;
};

function getFileName(stackTraceItem) {
	var fileName = stackTraceItem.getFileName();
	return isSystemFileName(fileName) ? null : fileName;
}

function getLineNumber(stackTraceItem) {
	var lineNumber = stackTraceItem.getLineNumber();
	return lineNumber >= 0 ? lineNumber : null;
}

function getMethodName(stackTraceItem) {
	var methodName = stackTraceItem.getMethodName();
	methodName = isSystemMethodName(methodName) ? '' : methodName;
	var indexOf = methodName.indexOf('_c_');
	if (indexOf === 0) {
		methodName = methodName.substr(3);
		methodName = methodName.substr(0, methodName.lastIndexOf('_'));
	}
	return methodName + '()';
}

function isSystemMethodName(methodName) {
	return methodName === 'exec' || methodName === 'call' || methodName === ':program' || methodName === '_c_script_0';
}

function isSystemFileName(fileName) {
	return fileName === 'utils/error.js' || fileName === 'core/assert.js' || fileName === 'service/tests.js';
}

function constructMessage(fileName, lineNumber, methodName) {
	if (fileName && lineNumber && methodName) {
		return '(' + fileName + '):' + lineNumber + ' -> ' + methodName;
	}
	return null;
}
