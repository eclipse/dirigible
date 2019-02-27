/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
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
	var output = org.eclipse.dirigible.api.v3.utils.EscapeFacade.escapeCsv(input);
	return output;
};

/**
 * Escapes a Javascript string
 */
exports.escapeJavascript = function(input) {
	var output = org.eclipse.dirigible.api.v3.utils.EscapeFacade.escapeJavascript(input);
	return output;
};

/**
 * Escapes a HTML3 string
 */
exports.escapeHtml3 = function(input) {
	var output = org.eclipse.dirigible.api.v3.utils.EscapeFacade.escapeHtml3(input);
	return output;
};

/**
 * Escapes a HTML4 string
 */
exports.escapeHtml4 = function(input) {
	var output = org.eclipse.dirigible.api.v3.utils.EscapeFacade.escapeHtml4(input);
	return output;
};

/**
 * Escapes a Java string
 */
exports.escapeJava = function(input) {
	var output = org.eclipse.dirigible.api.v3.utils.EscapeFacade.escapeJava(input);
	return output;
};

/**
 * Escapes a JSON string
 */
exports.escapeJson = function(input) {
	var output = org.eclipse.dirigible.api.v3.utils.EscapeFacade.escapeJson(input);
	return output;
};

/**
 * Escapes a XML string
 */
exports.escapeXml = function(input) {
	var output = org.eclipse.dirigible.api.v3.utils.EscapeFacade.escapeXml(input);
	return output;
};

/**
 * Unescapes a CSV string
 */
exports.unescapeCsv = function(input) {
	var output = org.eclipse.dirigible.api.v3.utils.EscapeFacade.unescapeCsv(input);
	return output;
};

/**
 * Unescapes a Javascript string
 */
exports.unescapeJavascript = function(input) {
	var output = org.eclipse.dirigible.api.v3.utils.EscapeFacade.unescapeJavascript(input);
	return output;
};

/**
 * Unescapes a HTML3 string
 */
exports.unescapeHtml3 = function(input) {
	var output = org.eclipse.dirigible.api.v3.utils.EscapeFacade.unescapeHtml3(input);
	return output;
};

/**
 * Unescapes a HTML4 string
 */
exports.unescapeHtml4 = function(input) {
	var output = org.eclipse.dirigible.api.v3.utils.EscapeFacade.unescapeHtml4(input);
	return output;
};

/**
 * Unescapes a Java string
 */
exports.unescapeJava = function(input) {
	var output = org.eclipse.dirigible.api.v3.utils.EscapeFacade.unescapeJava(input);
	return output;
};

/**
 * Unescapes a JSON string
 */
exports.unescapeJson = function(input) {
	var output = org.eclipse.dirigible.api.v3.utils.EscapeFacade.unescapeJson(input);
	return output;
};

/**
 * Unescapes a XML string
 */
exports.unescapeXml = function(input) {
	var output = org.eclipse.dirigible.api.v3.utils.EscapeFacade.unescapeXml(input);
	return output;
};
