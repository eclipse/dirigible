/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
 /**
 * HTTP API Request
 *
 */
 
const streams = require("io/streams");

let textData = null;

exports.isValid = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.isValid();
};

exports.getMethod = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getMethod();
};

exports.getRemoteUser = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getRemoteUser();
};

exports.getPathInfo = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getPathInfo();
};

exports.getPathTranslated = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getPathTranslated();
};

exports.getHeader = function (name) {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getHeader(name);
};

exports.isUserInRole = function (role) {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.isUserInRole(role);
};

exports.getAttribute = function (name) {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getAttribute(name);
};

exports.getAuthType = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getAuthType();
};

exports.getCookies = function () {
    let cookiesJson = org.eclipse.dirigible.components.api.http.HttpRequestFacade.getCookies();
    return JSON.parse(cookiesJson);
};

exports.getAttributeNames = function () {
    let attrNamesJson = org.eclipse.dirigible.components.api.http.HttpRequestFacade.getAttributeNames();
    return JSON.parse(attrNamesJson);
};

exports.getCharacterEncoding = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getCharacterEncoding();
};

exports.getContentLength = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getContentLength();
};

exports.getHeaders = function (name) {
    let headersJson = org.eclipse.dirigible.components.api.http.HttpRequestFacade.getHeaders(name);
    return JSON.parse(headersJson);
};

exports.getContentType = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getContentType();
};

exports.getBytes = function () {
    let bytesJson = org.eclipse.dirigible.components.api.http.HttpRequestFacade.getBytes();
    return JSON.parse(bytesJson);
};

var getText = exports.getText = function () {
    if (textData === null) {
        textData = org.eclipse.dirigible.components.api.http.HttpRequestFacade.getText();
    }
    return textData;
};

exports.getJSON = function () {
    let text = getText();
    return JSON.parse(text);
};

exports.getParameter = function (name) {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getParameter(name);
};

exports.getParameters = function () {
    let paramsJson;
    paramsJson = org.eclipse.dirigible.components.api.http.HttpRequestFacade.getParameters();
    return JSON.parse(paramsJson);
};

exports.getResourcePath = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getResourcePath();
};

exports.getHeaderNames = function () {
    let headerNamesJson = org.eclipse.dirigible.components.api.http.HttpRequestFacade.getHeaderNames();
    return JSON.parse(headerNamesJson);
};

exports.getParameterNames = function () {
    let paramNamesJson = org.eclipse.dirigible.components.api.http.HttpRequestFacade.getParameterNames();
    return JSON.parse(paramNamesJson);
};

exports.getParameterValues = function (name) {
    let paramValuesJson = org.eclipse.dirigible.components.api.http.HttpRequestFacade.getParameterValues(name);
    return JSON.parse(paramValuesJson);
};

exports.getProtocol = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getProtocol();
};

exports.getScheme = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getScheme();
};

exports.getContextPath = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getContextPath();
};

exports.getServerName = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getServerName();
};

exports.getServerPort = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getServerPort();
};

var getQueryString = exports.getQueryString = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getQueryString();
};

/**
 * Returns the query string name value pairs as JS object map. When multiple query parameters with the same name are specified,
 * it will collect theirs values in an array in the order of declaration under that name in the map.
 */
exports.getQueryParametersMap = function () {
    let queryString = getQueryString();
    if (!queryString)
        return {};

    queryString = decodeURI(queryString);
    let queryStringSegments = queryString.split('&');

    let queryMap = {};
    queryStringSegments.forEach(function (seg) {
        seg = seg.replace('amp;', '');
        const kv = seg.split('=');
        const key = kv[0].trim();
        const value = kv[1] === undefined ? true : kv[1].trim();
        if (queryMap[key] !== undefined) {
            if (!Array.isArray(queryMap[key]))
                queryMap[key] = [queryMap[key]];
            else
                queryMap[key].push(value);
        } else {
            queryMap[key] = value;
        }
    }.bind(this));
    return queryMap;
};

exports.getRemoteAddress = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getRemoteAddress();
};

exports.getRemoteHost = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getRemoteHost();
};

exports.setAttribute = function (name, value) {
    org.eclipse.dirigible.components.api.http.HttpRequestFacade.setAttribute(name, value);
};

exports.removeAttribute = function (name) {
    org.eclipse.dirigible.components.api.http.HttpRequestFacade.removeAttribute(name);
};

exports.getLocale = function () {
    let localeJson = org.eclipse.dirigible.components.api.http.HttpRequestFacade.getLocale();
    return JSON.parse(localeJson);
};

exports.getRequestURI = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getRequestURI();
};

exports.isSecure = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.isSecure();
};

exports.getRequestURL = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getRequestURL();
};

exports.getServicePath = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getServicePath();
};

exports.getRemotePort = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getRemotePort();
};

exports.getLocalName = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getLocalName();
};

exports.getLocalAddress = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getLocalAddress();
};

exports.getLocalPort = function () {
    return org.eclipse.dirigible.components.api.http.HttpRequestFacade.getLocalPort();
};

exports.getInputStream = function () {
    return streams.createInputStream(org.eclipse.dirigible.components.api.http.HttpRequestFacade.getInputStream());
};
