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
 * API v4 Files
 *
 * Note: This module is supported only with the Mozilla Rhino engine
 */

/**
 * Escapes a CSV string
 */
exports.escapeCsv = function(input) {
	return org.eclipse.dirigible.api.v3.utils.EscapeFacade.escapeCsv(input);
};

/**
 * Escapes a Javascript string
 */
exports.escapeJavascript = function(input) {
	return org.eclipse.dirigible.api.v3.utils.EscapeFacade.escapeJavascript(input);
};

/**
 * Escapes a HTML3 string
 */
exports.escapeHtml3 = function(input) {
	return org.eclipse.dirigible.api.v3.utils.EscapeFacade.escapeHtml3(input);
};

/**
 * Escapes a HTML4 string
 */
exports.escapeHtml4 = function(input) {
	return org.eclipse.dirigible.api.v3.utils.EscapeFacade.escapeHtml4(input);
};

/**
 * Escapes a Java string
 */
exports.escapeJava = function(input) {
	return org.eclipse.dirigible.api.v3.utils.EscapeFacade.escapeJava(input);
};

/**
 * Escapes a JSON string
 */
exports.escapeJson = function(input) {
	return org.eclipse.dirigible.api.v3.utils.EscapeFacade.escapeJson(input);
};

/**
 * Escapes a XML string
 */
exports.escapeXml = function(input) {
	return org.eclipse.dirigible.api.v3.utils.EscapeFacade.escapeXml(input);
};

/**
 * Unescapes a CSV string
 */
exports.unescapeCsv = function(input) {
	return org.eclipse.dirigible.api.v3.utils.EscapeFacade.unescapeCsv(input);
};

/**
 * Unescapes a Javascript string
 */
exports.unescapeJavascript = function(input) {
	return org.eclipse.dirigible.api.v3.utils.EscapeFacade.unescapeJavascript(input);
};

/**
 * Unescapes a HTML3 string
 */
exports.unescapeHtml3 = function(input) {
	return org.eclipse.dirigible.api.v3.utils.EscapeFacade.unescapeHtml3(input);
};

/**
 * Unescapes a HTML4 string
 */
exports.unescapeHtml4 = function(input) {
	return org.eclipse.dirigible.api.v3.utils.EscapeFacade.unescapeHtml4(input);
};

/**
 * Unescapes a Java string
 */
exports.unescapeJava = function(input) {
	return org.eclipse.dirigible.api.v3.utils.EscapeFacade.unescapeJava(input);
};

/**
 * Unescapes a JSON string
 */
exports.unescapeJson = function(input) {
	return org.eclipse.dirigible.api.v3.utils.EscapeFacade.unescapeJson(input);
};

/**
 * Unescapes a XML string
 */
exports.unescapeXml = function(input) {
	return org.eclipse.dirigible.api.v3.utils.EscapeFacade.unescapeXml(input);
};
