/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.api.v3.utils;

import org.apache.commons.lang3.StringEscapeUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class EscapeFacade.
 */
public class EscapeFacade {

	/**
	 * Escape csv.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static final String escapeCsv(String input) {
		return StringEscapeUtils.escapeCsv(input);
	}

	/**
	 * Escape javascript.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static final String escapeJavascript(String input) {
		return StringEscapeUtils.escapeEcmaScript(input);
	}

	/**
	 * Escape html 3.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static final String escapeHtml3(String input) {
		return StringEscapeUtils.escapeHtml3(input);
	}

	/**
	 * Escape html 4.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static final String escapeHtml4(String input) {
		return StringEscapeUtils.escapeHtml4(input);
	}

	/**
	 * Escape java.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static final String escapeJava(String input) {
		return StringEscapeUtils.escapeJava(input);
	}

	/**
	 * Escape json.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static final String escapeJson(String input) {
		return StringEscapeUtils.escapeJson(input);
	}

	/**
	 * Escape xml.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static final String escapeXml(String input) {
		return StringEscapeUtils.escapeXml(input);
	}

	/**
	 * Unescape csv.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static final String unescapeCsv(String input) {
		return StringEscapeUtils.unescapeCsv(input);
	}

	/**
	 * Unescape javascript.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static final String unescapeJavascript(String input) {
		return StringEscapeUtils.unescapeEcmaScript(input);
	}

	/**
	 * Unescape html 3.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static final String unescapeHtml3(String input) {
		return StringEscapeUtils.unescapeHtml3(input);
	}

	/**
	 * Unescape html 4.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static final String unescapeHtml4(String input) {
		return StringEscapeUtils.unescapeHtml4(input);
	}

	/**
	 * Unescape java.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static final String unescapeJava(String input) {
		return StringEscapeUtils.unescapeJava(input);
	}

	/**
	 * Unescape json.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static final String unescapeJson(String input) {
		return StringEscapeUtils.unescapeJson(input);
	}

	/**
	 * Unescape xml.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static final String unescapeXml(String input) {
		return StringEscapeUtils.unescapeXml(input);
	}

}
