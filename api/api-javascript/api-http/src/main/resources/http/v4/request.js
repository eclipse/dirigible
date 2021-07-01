/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
var streams = require("io/v4/streams");

let textData = null;

exports.isValid = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.isValid();
};

exports.getMethod = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getMethod();
};

exports.getRemoteUser = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getRemoteUser();
};

exports.getPathInfo = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getPathInfo();
};

exports.getPathTranslated = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getPathTranslated();
};

exports.getHeader = function(name) {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getHeader(name);
};

exports.isUserInRole = function(role) {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.isUserInRole(role);
};

exports.getAttribute = function(name) {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getAttribute(name);
};

exports.getAuthType = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getAuthType();
};

exports.getCookies = function() {
	var cookiesJson;
	cookiesJson = org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getCookies();
	var cookies = JSON.parse(cookiesJson);
	return cookies;
};

exports.getAttributeNames = function() {
	var attrNamesJson;
	attrNamesJson = org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getAttributeNames();
	var attrNames = JSON.parse(attrNamesJson);
	return attrNames;
};

exports.getCharacterEncoding = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getCharacterEncoding();
};

exports.getContentLength = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getContentLength();
};
	
exports.getHeaders = function(name) {
	var headersJson;
	headersJson = org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getHeaders(name);
	var headers = JSON.parse(headersJson);
	return headers;
};

exports.getContentType = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getContentType();
};

exports.getBytes = function() {
	var bytesJson;
	bytesJson = org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getBytes();
	var bytes = JSON.parse(bytesJson);
	return bytes;
};

var getText = exports.getText = function() {
	if (textData === null) {
		textData = org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getText();
	}
	return textData;
};

exports.getJSON = function() {
	let text = getText();
	return JSON.parse(text);
};

exports.getParameter = function(name) {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getParameter(name);
};

exports.getParameters = function() {
	var paramsJson;
	paramsJson = org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getParameters();
	var params = JSON.parse(paramsJson);
	return params;
};

exports.getResourcePath = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getResourcePath();
};

exports.getHeaderNames = function() {
	var headerNamesJson;
	headerNamesJson = org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getHeaderNames();
	var headerNames = JSON.parse(headerNamesJson);
	return headerNames;
};

exports.getParameterNames = function() {
	var paramNamesJson;
	paramNamesJson = org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getParameterNames();
	var paramNames = JSON.parse(paramNamesJson);
	return paramNames;
};

exports.getParameterValues = function(name) {
	var paramValuesJson;
	paramValuesJson = org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getParameterValues(name);
	var paramValues = JSON.parse(paramValuesJson);
	return paramValues;
};

exports.getProtocol = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getProtocol();
};
	
exports.getScheme = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getScheme();
};

exports.getContextPath = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getContextPath();
};

exports.getServerName = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getServerName();
};

exports.getServerPort = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getServerPort();
};

var getQueryString = exports.getQueryString = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getQueryString();
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
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getRemoteAddress();
};

exports.getRemoteHost = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getRemoteHost();
};

exports.setAttribute = function(name, value) {
	org.eclipse.dirigible.api.v3.http.HttpRequestFacade.setAttribute(name, value);
};

exports.removeAttribute = function(name) {
	org.eclipse.dirigible.api.v3.http.HttpRequestFacade.removeAttribute(name);
};

exports.getLocale = function() {
	var localeJson;
	localeJson = org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getLocale();
	var locale = JSON.parse(localeJson);
	return locale;
};

exports.getRequestURI = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getRequestURI();
};

exports.isSecure = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.isSecure();
};

exports.getRequestURL = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getRequestURL();
};

exports.getServicePath = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getServicePath();
};

exports.getRemotePort = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getRemotePort();
};

exports.getLocalName = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getLocalName();
};

exports.getLocalAddress = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getLocalAddress();
};

exports.getLocalPort = function() {
	return org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getLocalPort();
};

exports.getInputStream = function() {
	return streams.createInputStream(org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getInputStream());
};
