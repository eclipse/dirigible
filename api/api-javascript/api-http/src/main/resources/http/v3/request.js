/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* eslint-env node, dirigible */

var java = require('core/v3/java');

exports.isValid = function() {
	var valid = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'isValid', []);
	return valid;
};

exports.getMethod = function() {
	var method = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getMethod', []);
	return method;
};

exports.getRemoteUser = function() {
	var remoteUser = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getRemoteUser', []);
	return remoteUser;
};

exports.getPathInfo = function() {
	var pathInfo = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getPathInfo', []);
	return pathInfo;
};

exports.getPathTranslated = function() {
	var pathTranslated = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getPathTranslated', []);
	return pathTranslated;
};

exports.getHeader = function(name) {
	var header = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getHeader', [name]);
	return header;
};

exports.isUserInRole = function(role) {
	var isInRole = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'isUserInRole', [role]);
	return isInRole;
};

exports.getAttribute = function(name) {
	var attr = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getAttribute', [name]);
	return attr;
};

exports.getAuthType = function() {
	var authType = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getAuthType', []);
	return authType;
};

exports.getCookies = function() {
	var cookiesJson = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getCookies', []);
	var cookies = JSON.parse(cookiesJson);
	return cookies;
};

exports.getAttributeNames = function() {
	var attrNamesJson = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getAttributeNames', []);
	var attrNames = JSON.parse(attrNamesJson);
	return attrNames;
};

exports.getCharacterEncoding = function() {
	var characterEncoding = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getCharacterEncoding', []);
	return characterEncoding;
};

exports.getContentLength = function() {
	var contentLength = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getContentLength', []);
	return contentLength;
};
	
exports.getHeaders = function(name) {
	var headersJson = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getHeaders', [name]);
	var headers = JSON.parse(headersJson);
	return headers;
};

exports.getContentType = function() {
	var contentType = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getContentType', []);
	return contentType;
};

exports.getBytes = function() {
	var bytesJson = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getBytes', []);
	var bytes = JSON.parse(bytesJson);
	return bytes;
};

exports.getText = function() {
	var textJson = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getText', []);
	var text = JSON.parse(textJson);
	return text;
};

exports.getParameter = function(name) {
	var param = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getParameter', [name]);
	return param;
};

exports.getParameters = function(name) {
	var params = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getParameters', []);
	return JSON.parse(params);
};

exports.getResourcePath = function(name) {
	var resourcePath = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getResourcePath', []);
	return resourcePath;
};

exports.getHeaderNames = function() {
	var headerNamesJson = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getHeaderNames', []);
	var headerNames = JSON.parse(headerNamesJson);
	return headerNames;
};

exports.getParameterNames = function() {
	var paramNamesJson = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getParameterNames', []);
	var paramNames = JSON.parse(paramNamesJson);
	return paramNames;
};

exports.getParameterValues = function(name) {
	var paramValuesJson = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getParameterValues', [name]);
	var paramValues = JSON.parse(paramValuesJson);
	return paramValues;
};

exports.getProtocol = function() {
	var protocol = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getProtocol', []);
	return protocol;
};
	
exports.getScheme = function() {
	var scheme = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getScheme', []);
	return scheme;
};

exports.getContextPath = function() {
	var contextPath = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getContextPath', []);
	return contextPath;
};

exports.getServerName = function() {
	var serverName = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getServerName', []);
	return serverName;
};

exports.getServerPort = function() {
	var serverPort = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getServerPort', []);
	return serverPort;
};

exports.getQueryString = function() {
	var queryString = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getQueryString', []);
	return queryString;
};

exports.getRemoteAddress = function() {
	var remoteAddr = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getRemoteAddress', []);
	return remoteAddr;
};

exports.getRemoteHost = function() {
	var remoteHost = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getRemoteHost', []);
	return remoteHost;
};

exports.setAttribute = function(name, value) {
	java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'setAttribute', [name, value]);
};

exports.removeAttribute = function(name) {
	java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'removeAttribute', [name]);
};

exports.getLocale = function() {
	var localeJson = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getLocale', []);
	var locale = JSON.parse(localeJson);
	return locale;
};

exports.getRequestURI = function() {
	var requestURI = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getRequestURI', []);
	return requestURI;
};

exports.isSecure = function() {
	var secure = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'isSecure', []);
	return secure;
};

exports.getRequestURL = function() {
	var requestURL = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getRequestURL', []);
	return requestURL;
};

exports.getServicePath = function() {
	var servicePath = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getServicePath', []);
	return servicePath;
};

exports.getRemotePort = function() {
	var remotePort = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getRemotePort', []);
	return remotePort;
};

exports.getLocalName = function() {
	var localName = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getLocalName', []);
	return localName;
};

exports.getLocalAddress = function() {
	var localAddr = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getLocalAddress', []);
	return localAddr;
};

exports.getLocalPort = function() {
	var localPort = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getLocalPort', []);
	return localPort;
};
