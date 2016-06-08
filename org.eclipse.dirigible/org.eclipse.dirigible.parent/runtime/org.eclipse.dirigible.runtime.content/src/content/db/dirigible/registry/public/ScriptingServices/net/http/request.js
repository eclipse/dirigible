/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ javax */
/* eslint-env node, dirigible */

var streams = require("io/streams");

exports.getMethod = function() {
	return $.getRequest().getMethod();
};

exports.getParameter = function(name) {
	return $.getRequest().getParameter(name);
};

exports.getParameterNames = function() {
	var names = [];
	var values = $.getRequest().getParameterNames();
	while (values.hasMoreElements()) {
		names.push(values.nextElement());
	}
	return names;
};

exports.getParameters = function() {
	var parameters = {};
	var values = $.getRequest().getParameterMap().entrySet();
	var iteretor = values.iterator();
	while (iteretor.hasNext()) {
		var parameter = iteretor.next();
		var key = parameter.getKey();
		parameters[key] = [];
		var parameterValues = parameter.getValue();
		for (var i = 0; i < parameterValues.length; i ++) {
			parameters[key].push(parameterValues[i]);
		}
	}
	return parameters;
};

exports.getHeader = function(name) {
	return $.getRequest().getHeader(name);
};

exports.getHeaderNames = function() {
	var names = [];
	var values = $.getRequest().getHeaderNames();
	while (values.hasMoreElements()) {
		names.push(values.nextElement());
	}
	return names;
};

exports.getCookies = function() {
	var cookies = [];
	var values = $.getRequest().getCookies();
	for (var i = 0; i < values.length; i ++) {
		var cookie = values[i];
		cookies.push({
			"name": cookie.getName(),
			"value": cookie.getValue(),
			"maxAge": cookie.getMaxAge(),
			"path": cookie.getPath(),
			"domain": cookie.getDomain(),
			"secure": cookie.getSecure()
		});
	}

	return cookies;
};

exports.isUserInRole = function(role) {
	return $.getRequest().isUserInRole(role);
};

exports.getInfo = function() {
	return {
		"contextPath": $.getRequest().getPathInfo(),
		"pathInfo": $.getRequest().getPathInfo(),
		"protocol": $.getRequest().getProtocol(),
		"queryString": $.getRequest().getQueryString(),
		"scheme": $.getRequest().getScheme(),
		"serverName": $.getRequest().getServerName(),
		"serverPort": $.getRequest().getServerPort()
	};
};

exports.readInput = function() {
	var input = new streams.InputStream($.getRequest().getInputStream());
	return streams.read(input);
};

exports.readInputText = function() {
	var input = new streams.InputStream($.getRequest().getInputStream());
	return streams.readText(input);
};