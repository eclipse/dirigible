/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */

const EscapeFacade = Java.type("org.eclipse.dirigible.components.api.utils.EscapeFacade");

export function escapeCsv(input) {
	return EscapeFacade.escapeCsv(input);
};

/**
 * Escapes a Javascript string
 */
export function escapeJavascript(input) {
	return EscapeFacade.escapeJavascript(input);
};

/**
 * Escapes a HTML3 string
 */
export function escapeHtml3(input) {
	return EscapeFacade.escapeHtml3(input);
};

/**
 * Escapes a HTML4 string
 */
export function escapeHtml4(input) {
	return EscapeFacade.escapeHtml4(input);
};

/**
 * Escapes a Java string
 */
export function escapeJava(input) {
	return EscapeFacade.escapeJava(input);
};

/**
 * Escapes a JSON string
 */
export function escapeJson(input) {
	return EscapeFacade.escapeJson(input);
};

/**
 * Escapes a XML string
 */
export function escapeXml(input) {
	return EscapeFacade.escapeXml(input);
};

/**
 * Unescapes a CSV string
 */
export function unescapeCsv(input) {
	return EscapeFacade.unescapeCsv(input);
};

/**
 * Unescapes a Javascript string
 */
export function unescapeJavascript(input) {
	return EscapeFacade.unescapeJavascript(input);
};

/**
 * Unescapes a HTML3 string
 */
export function unescapeHtml3(input) {
	return EscapeFacade.unescapeHtml3(input);
};

/**
 * Unescapes a HTML4 string
 */
export function unescapeHtml4(input) {
	return EscapeFacade.unescapeHtml4(input);
};

/**
 * Unescapes a Java string
 */
export function unescapeJava(input) {
	return EscapeFacade.unescapeJava(input);
};

/**
 * Unescapes a JSON string
 */
export function unescapeJson(input) {
	return EscapeFacade.unescapeJson(input);
};

/**
 * Unescapes a XML string
 */
export function unescapeXml(input) {
	return EscapeFacade.unescapeXml(input);
};
