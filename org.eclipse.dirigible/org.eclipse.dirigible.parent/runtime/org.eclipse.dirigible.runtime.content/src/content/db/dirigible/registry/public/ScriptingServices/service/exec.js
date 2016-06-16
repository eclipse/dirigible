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

var exec_internal = function(type, path, context) {
    var result = {};
	var executionContext = $.getExecutionService().createContext();
	if (context) {
		for (var property in context) {
		    if (context.hasOwnProperty(property)) {
		        executionContext.put(property, context[property]);
		    }
		}
	}

	result.output = $.getExecutionService().execute($.getRequest(), $.getResponse(), path, executionContext, type);
	
	result.context = {};
	var iterator = executionContext.keySet().iterator();
	while (iterator.hasNext()) {
		var key = iterator.next();
		var value = executionContext.get(key);
		result.context[key] = value;
	}

	return result;
};

exports.js = function(path, context) {
	return exec_internal("js", path, context);
};

exports.test = function(path, context) {
	return exec_internal("test", path, context);
};

exports.flow = function(path, context) {
	return exec_internal("flow", path, context);
};

exports.job = function(path, context) {
	return exec_internal("job", path, context);
};

exports.sql = function(path, context) {
	return exec_internal("sql", path, context);
};

exports.wiki = function(path, context) {
	return exec_internal("wiki", path, context);
};

exports.command = function(path, context) {
	return exec_internal("command", path, context);
};
