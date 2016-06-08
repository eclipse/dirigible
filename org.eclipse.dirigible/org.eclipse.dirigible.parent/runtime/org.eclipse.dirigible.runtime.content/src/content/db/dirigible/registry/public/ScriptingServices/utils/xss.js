/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ java */
/* eslint-env node, dirigible */

exports.escapeCsv = function(data) {
	try {
		var value = $.getXssUtils().escapeCsv(data);
	} catch(e) {
		return null;
	}
	return value;
};

exports.escapeHtml = function(data) {
	try {
		var value = $.getXssUtils().escapeHtml(data);
	} catch(e) {
		return null;
	}
	return value;
};

exports.escapeJava = function(data) {
	try {
		var value = $.getXssUtils().escapeJava(data);
	} catch(e) {
		return null;
	}
	return value;
};

exports.escapeJavaScript = function(data) {
	try {
		var value = $.getXssUtils().escapeJavaScript(data);
	} catch(e) {
		return null;
	}
	return value;
};

exports.escapeSql = function(data) {
	try {
		var value = $.getXssUtils().escapeSql(data);
	} catch(e) {
		return null;
	}
	return value;
};

exports.escapeXml = function(data) {
	try {
		var value = $.getXssUtils().escapeXml(data);
	} catch(e) {
		return null;
	}
	return value;
};

exports.unescapeCsv = function(data) {
	try {
		var value = $.getXssUtils().unescapeCsv(data);
	} catch(e) {
		return null;
	}
	return value;
};

exports.unescapeHtml = function(data) {
	try {
		var value = $.getXssUtils().unescapeHtml(data);
	} catch(e) {
		return null;
	}
	return value;
};

exports.unescapeJava = function(data) {
	try {
		var value = $.getXssUtils().unescapeJava(data);
	} catch(e) {
		return null;
	}
	return value;
};

exports.unescapeJavaScript = function(data) {
	try {
		var value = $.getXssUtils().unescapeJavaScript(data);
	} catch(e) {
		return null;
	}
	return value;
};

exports.unescapeSql = function(data) {
	try {
		var value = $.getXssUtils().unescapeSql(data);
	} catch(e) {
		return null;
	}
	return value;
};

exports.unescapeXml = function(data) {
	try {
		var value = $.getXssUtils().unescapeXml(data);
	} catch(e) {
		return null;
	}
	return value;
};
