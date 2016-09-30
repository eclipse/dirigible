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

exports.getAttribute = function(name) {
	return $.getRequest().getAttribute(name);
};

exports.setAttribute = function(name, value) {
	return $.getRequest().setAttribute(name, value);
};

exports.getAttributeNames = function() {
	var names = [];
	var values = $.getRequest().getAttributeNames();
	while (values.hasMoreElements()) {
		names.push(values.nextElement());
	}
	return names;
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
		var internalCookie = values[i];
		var cookie = new HttpCookie();
		cookie.name = internalCookie.getName();
		cookie.value = internalCookie.getValue();
		cookie.comment = internalCookie.getComment();
		cookie.maxAge = internalCookie.getMaxAge();
		cookie.path = internalCookie.getPath();
		cookie.domain = internalCookie.getDomain();
		cookie.secure = internalCookie.getSecure();
		cookie.version = internalCookie.getVersion();
		cookie.httpOnly = internalCookie.isHttpOnly();
		cookies.push(cookie);
	}

	return cookies;
};

exports.isUserInRole = function(role) {
	return $.getRequest().isUserInRole(role);
};

exports.getInfo = function() {
	var info = new HttpRequestInfo();
	info.contextPath = $.getRequest().getContextPath();
	info.pathInfo = $.getRequest().getPathInfo();
	info.protocol = $.getRequest().getProtocol();
	info.queryString = $.getRequest().getQueryString();
	info.scheme = $.getRequest().getScheme();
	info.serverName = $.getRequest().getServerName();
	info.serverPort = $.getRequest().getServerPort();
	return info;
};

exports.readInput = function() {
	var input = new streams.InputStream($.getRequest().getInputStream());
	return streams.read(input);
};

// deprecated
exports.getInput = function() {
	var input = new streams.InputStream($.getRequest().getInputStream());
	return input;
};

exports.getInputStream = function() {
	var input = new streams.InputStream($.getRequest().getInputStream());
	return input;
};

exports.readInputText = function() {
	var input = new streams.InputStream($.getRequest().getInputStream());
	return streams.readText(input);
};

/**
 * HTTP Cookie object
 */
function HttpCookie() {
	this.name = "";
	this.value = "";
	this.comment = "";
	this.maxAge = 0;
	this.path = "";
	this.domain = "";
	this.secure = false;
	this.version = 0;
	this.httpOnly = false;
}

/**
 * HTTP Response object
 */
function HttpRequestInfo() {
	this.contextPath = "";
	this.pathInfo = "";
	this.protocol = "";
	this.queryString = "";
	this.scheme = "";
	this.serverName = "";
	this.serverPort = 0;
}
