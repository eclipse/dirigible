/*
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var java = require('core/v3/java');

exports.escapeCsv = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.EscapeFacade', 'escapeCsv', [input]);
	return output;
};

exports.escapeJavascript = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.EscapeFacade', 'escapeJavascript', [input]);
	return output;
};

exports.escapeHtml3 = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.EscapeFacade', 'escapeHtml3', [input]);
	return output;
};

exports.escapeHtml4 = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.EscapeFacade', 'escapeHtml4', [input]);
	return output;
};

exports.escapeJava = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.EscapeFacade', 'escapeJava', [input]);
	return output;
};

exports.escapeJson = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.EscapeFacade', 'escapeJson', [input]);
	return output;
};

exports.escapeXml = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.EscapeFacade', 'escapeXml', [input]);
	return output;
};

exports.unescapeCsv = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.EscapeFacade', 'unescapeCsv', [input]);
	return output;
};

exports.unescapeJavascript = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.EscapeFacade', 'unescapeJavascript', [input]);
	return output;
};

exports.unescapeHtml3 = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.EscapeFacade', 'unescapeHtml3', [input]);
	return output;
};

exports.unescapeHtml4 = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.EscapeFacade', 'unescapeHtml4', [input]);
	return output;
};

exports.unescapeJava = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.EscapeFacade', 'unescapeJava', [input]);
	return output;
};

exports.unescapeJson = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.EscapeFacade', 'unescapeJson', [input]);
	return output;
};

exports.unescapeXml = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.EscapeFacade', 'unescapeXml', [input]);
	return output;
};
