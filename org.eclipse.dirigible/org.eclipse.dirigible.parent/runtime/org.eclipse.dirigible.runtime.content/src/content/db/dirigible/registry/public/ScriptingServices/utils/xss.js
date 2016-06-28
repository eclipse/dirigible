/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ java engine */
/* eslint-env node, dirigible */

exports.escapeCsv = function(data) {
	try {
		var value;
		if (engine === "nashorn") {
			value = $.getXssUtils().class.static.escapeCsv(data);
		} else {
			value = $.getXssUtils().escapeCsv(data);
		}
	} catch(e) {
		console.error(e);
		return null;
	}
	return value;
};

exports.escapeHtml = function(data) {
	try {
		var value;
		if (engine === "nashorn") {
			value = $.getXssUtils().class.static.escapeHtml(data);
		} else {
			value = $.getXssUtils().escapeHtml(data);
		}
	} catch(e) {
		console.error(e);
		return null;
	}
	return value;
};

exports.escapeJava = function(data) {
	try {
		var value;
		if (engine === "nashorn") {
			value = $.getXssUtils().class.static.escapeJava(data);
		} else {
			value = $.getXssUtils().escapeJava(data);
		}
	} catch(e) {
		console.error(e);
		return null;
	}
	return value;
};

exports.escapeJavaScript = function(data) {
	try {
		var value;
		if (engine === "nashorn") {
			value = $.getXssUtils().class.static.escapeJavaScript(data);
		} else {
			value = $.getXssUtils().escapeJavaScript(data);
		}
	} catch(e) {
		console.error(e);
		return null;
	}
	return value;
};

exports.escapeSql = function(data) {
	try {
		var value;
		if (engine === "nashorn") {
			value = $.getXssUtils().class.static.escapeSql(data);
		} else {
			value = $.getXssUtils().escapeSql(data);
		}
	} catch(e) {
		console.error(e);
		return null;
	}
	return value;
};

exports.escapeXml = function(data) {
	try {
		var value;
		if (engine === "nashorn") {
			value = $.getXssUtils().class.static.escapeXml(data);
		} else {
			value = $.getXssUtils().escapeXml(data);
		}
	} catch(e) {
		console.error(e);
		return null;
	}
	return value;
};

exports.unescapeCsv = function(data) {
	try {
		var value;
		if (engine === "nashorn") {
			value = $.getXssUtils().class.static.unescapeCsv(data);
		} else {
			value = $.getXssUtils().unescapeCsv(data);
		}
	} catch(e) {
		console.error(e);
		return null;
	}
	return value;
};

exports.unescapeHtml = function(data) {
	try {
		var value;
		if (engine === "nashorn") {
			value = $.getXssUtils().class.static.unescapeHtml(data);
		} else {
			value = $.getXssUtils().unescapeHtml(data);
		}
	} catch(e) {
		console.error(e);
		return null;
	}
	return value;
};

exports.unescapeJava = function(data) {
	try {
		var value;
		if (engine === "nashorn") {
			value = $.getXssUtils().class.static.unescapeJava(data);
		} else {
			value = $.getXssUtils().unescapeJava(data);
		}
	} catch(e) {
		console.error(e);
		return null;
	}
	return value;
};

exports.unescapeJavaScript = function(data) {
	try {
		var value;
		if (engine === "nashorn") {
			value = $.getXssUtils().class.static.unescapeJavaScript(data);
		} else {
			value = $.getXssUtils().unescapeJavaScript(data);
		}
	} catch(e) {
		console.error(e);
		return null;
	}
	return value;
};

exports.unescapeSql = function(data) {
	try {
		var value;
		if (engine === "nashorn") {
			value = $.getXssUtils().class.static.unescapeSql(data);
		} else {
			value = $.getXssUtils().unescapeSql(data);
		}
	} catch(e) {
		console.error(e);
		return null;
	}
	return value;
};

exports.unescapeXml = function(data) {
	try {
		var value;
		if (engine === "nashorn") {
			value = $.getXssUtils().class.static.unescapeXml(data);
		} else {
			value = $.getXssUtils().unescapeXml(data);
		}
	} catch(e) {
		console.error(e);
		return null;
	}
	return value;
};
