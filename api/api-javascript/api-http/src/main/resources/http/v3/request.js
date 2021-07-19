/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
var java = require('core/v3/java');

exports.isValid = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.isValid();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.isValid();
	}
	var valid = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'isValid', []);
	return valid;
};

exports.getMethod = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getMethod();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getMethod();
	}
	var method = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getMethod', []);
	return method;
};

exports.getRemoteUser = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getRemoteUser();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getRemoteUser();
	}
	var remoteUser = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getRemoteUser', []);
	return remoteUser;
};

exports.getPathInfo = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getPathInfo();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getPathInfo();
	}
	var pathInfo = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getPathInfo', []);
	return pathInfo;
};

exports.getPathTranslated = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getPathTranslated();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getPathTranslated();
	}
	var pathTranslated = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getPathTranslated', []);
	return pathTranslated;
};

exports.getHeader = function(name) {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getHeader(name);
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getHeader(name);
	}
	var header = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getHeader', [name]);
	return header;
};

exports.isUserInRole = function(role) {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.isUserInRole(role);
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.isUserInRole(role);
	}
	var isInRole = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'isUserInRole', [role]);
	return isInRole;
};

exports.getAttribute = function(name) {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getAttribute(name);
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getAttribute(name);
	}
	var attr = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getAttribute', [name]);
	return attr;
};

exports.getAuthType = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getAuthType();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getAuthType();
	}
	var authType = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getAuthType', []);
	return authType;
};

exports.getCookies = function() {
	var cookiesJson;
	cookiesJson = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getCookies', []);
	var cookies = JSON.parse(cookiesJson);
	return cookies;
};

exports.getAttributeNames = function() {
	var attrNamesJson;
	attrNamesJson = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getAttributeNames', []);
	var attrNames = JSON.parse(attrNamesJson);
	return attrNames;
};

exports.getCharacterEncoding = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getCharacterEncoding();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getCharacterEncoding();
	}
	var characterEncoding = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getCharacterEncoding', []);
	return characterEncoding;
};

exports.getContentLength = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getContentLength();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getContentLength();
	}
	var contentLength = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getContentLength', []);
	return contentLength;
};
	
exports.getHeaders = function(name) {
	var headersJson;
	headersJson = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getHeaders', [name]);
	var headers = JSON.parse(headersJson);
	return headers;
};

exports.getContentType = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getContentType();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getContentType();
	}
	var contentType = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getContentType', []);
	return contentType;
};

exports.getBytes = function() {
	var bytesJson;
	bytesJson = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getBytes', []);
	var bytes = JSON.parse(bytesJson);
	return bytes;
};

var getText = exports.getText = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getText();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getText();
	}
	return java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getText', []);
};

exports.getJSON = function() {
	var text = getText();
	var json = JSON.parse(text);
	return json;
};


exports.getParameter = function(name) {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getParameter(name);
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getParameter(name);
	}
	var param = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getParameter', [name]);
	return param;
};

exports.getParameters = function() {
	var paramsJson;
	paramsJson = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getParameters', []);
	var params = JSON.parse(paramsJson);
	return params;
};

exports.getResourcePath = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getResourcePath();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getResourcePath();
	}
	var resourcePath = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getResourcePath', []);
	return resourcePath;
};

exports.getHeaderNames = function() {
	var headerNamesJson;
	headerNamesJson = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getHeaderNames', []);
	var headerNames = JSON.parse(headerNamesJson);
	return headerNames;
};

exports.getParameterNames = function() {
	var paramNamesJson;
	paramNamesJson = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getParameterNames', []);
	var paramNames = JSON.parse(paramNamesJson);
	return paramNames;
};

exports.getParameterValues = function(name) {
	var paramValuesJson;
	paramValuesJson = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getParameterValues', [name]);
	var paramValues = JSON.parse(paramValuesJson);
	return paramValues;
};

exports.getProtocol = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getProtocol();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getProtocol();
	}
	var protocol = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getProtocol', []);
	return protocol;
};
	
exports.getScheme = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getScheme();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getScheme();
	}
	var scheme = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getScheme', []);
	return scheme;
};

exports.getContextPath = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getContextPath();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getContextPath();
	}
	var contextPath = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getContextPath', []);
	return contextPath;
};

exports.getServerName = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getServerName();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getServerName();
	}
	var serverName = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getServerName', []);
	return serverName;
};

exports.getServerPort = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getServerPort();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getServerPort();
	}
	var serverPort = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getServerPort', []);
	return serverPort;
};

var getQueryString = exports.getQueryString = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getQueryString();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getQueryString();
	}
	var queryString = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getQueryString', []);
	return queryString;
};

/**
 * Returns the query string name value pairs as JS object map. When multiple query parameters with the same name are specified,
 * it will collect theirs values in an array in the order of declaration under that name in the map.
 */
exports.getQueryParametersMap = function() {
	var queryString = getQueryString();
	if(!queryString)
			return {};
			
	queryString = decodeURI(queryString);
	var queryStringSegments = queryString.split('&');
	
	var queryMap = {};
	queryStringSegments.forEach(function(seg){
		seg = seg.replace('amp;','');
		var kv = seg.split('=');
		var key = kv[0].trim();
		var value = kv[1]===undefined ? true : kv[1].trim();
		if(queryMap[key] !== undefined){
			if(!Array.isArray(queryMap[key]))
				queryMap[key] = [queryMap[key]];
			else
				queryMap[key].push(value);
		} else{
			queryMap[key] = value;	
		}
	}.bind(this));
	return queryMap;
};

exports.getRemoteAddress = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getRemoteAddress();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getRemoteAddress();
	}
	var remoteAddr = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getRemoteAddress', []);
	return remoteAddr;
};

exports.getRemoteHost = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getRemoteHost();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getRemoteHost();
	}
	var remoteHost = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getRemoteHost', []);
	return remoteHost;
};

exports.setAttribute = function(name, value) {
	if (__engine === 'rhino') {
		org.eclipse.dirigible.api.v3.http.HttpRequestFacade.setAttribute(name, value);
	}
	if (__engine === 'nashorn') {
		Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.setAttribute(name, value);
	}
	java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'setAttribute', [name, value]);
};

exports.removeAttribute = function(name) {
	if (__engine === 'rhino') {
		org.eclipse.dirigible.api.v3.http.HttpRequestFacade.removeAttribute(name);
	}
	if (__engine === 'nashorn') {
		Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.removeAttribute(name);
	}
	java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'removeAttribute', [name]);
};

exports.getLocale = function() {
	var localeJson;
	localeJson = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getLocale', []);
	var locale = JSON.parse(localeJson);
	return locale;
};

exports.getRequestURI = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getRequestURI();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getRequestURI();
	}
	var requestURI = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getRequestURI', []);
	return requestURI;
};

exports.isSecure = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.isSecure();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.isSecure();
	}
	var secure = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'isSecure', []);
	return secure;
};

exports.getRequestURL = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getRequestURL();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getRequestURL();
	}
	var requestURL = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getRequestURL', []);
	return requestURL;
};

exports.getServicePath = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getServicePath();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getServicePath();
	}
	var servicePath = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getServicePath', []);
	return servicePath;
};

exports.getRemotePort = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getRemotePort();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getRemotePort();
	}
	var remotePort = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getRemotePort', []);
	return remotePort;
};

exports.getLocalName = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getLocalName();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getLocalName();
	}
	var localName = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getLocalName', []);
	return localName;
};

exports.getLocalAddress = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getLocalAddress();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getLocalAddress();
	}
	var localAddr = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getLocalAddress', []);
	return localAddr;
};

exports.getLocalPort = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getLocalPort();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getLocalPort();
	}
	var localPort = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getLocalPort', []);
	return localPort;
};
