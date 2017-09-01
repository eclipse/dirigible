package org.eclipse.dirigible.api.v3.utils;

import org.apache.commons.lang3.StringEscapeUtils;

public class EscapeFacade {

	public static final String escapeCsv(String input) {
		return StringEscapeUtils.escapeCsv(input);
	}

	public static final String escapeJavascript(String input) {
		return StringEscapeUtils.escapeEcmaScript(input);
	}

	public static final String escapeHtml3(String input) {
		return StringEscapeUtils.escapeHtml3(input);
	}

	public static final String escapeHtml4(String input) {
		return StringEscapeUtils.escapeHtml4(input);
	}

	public static final String escapeJava(String input) {
		return StringEscapeUtils.escapeJava(input);
	}

	public static final String escapeJson(String input) {
		return StringEscapeUtils.escapeJson(input);
	}

	public static final String escapeXml(String input) {
		return StringEscapeUtils.escapeXml(input);
	}

	public static final String unescapeCsv(String input) {
		return StringEscapeUtils.unescapeCsv(input);
	}

	public static final String unescapeJavascript(String input) {
		return StringEscapeUtils.unescapeEcmaScript(input);
	}

	public static final String unescapeHtml3(String input) {
		return StringEscapeUtils.unescapeHtml3(input);
	}

	public static final String unescapeHtml4(String input) {
		return StringEscapeUtils.unescapeHtml4(input);
	}

	public static final String unescapeJava(String input) {
		return StringEscapeUtils.unescapeJava(input);
	}

	public static final String unescapeJson(String input) {
		return StringEscapeUtils.unescapeJson(input);
	}

	public static final String unescapeXml(String input) {
		return StringEscapeUtils.unescapeXml(input);
	}

}
