/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ javax */
/* eslint-env node, dirigible */

exports.getAttribute = function(name) {
	return $.getSession().getAttribute(name);
};

exports.setAttribute = function(name, value) {
	$.getSession().setAttribute(name, value);
};

exports.removeAttribute = function(name) {
	$.getSession().removeAttribute(name);
};

exports.getAttributeNames = function() {
	var names = [];
	var values = $.getSession().getAttributeNames();
	while (values.hasMoreElements()) {
		names.push(values.nextElement());
	}
	return names;
};

exports.getId = function() {
	return $.getSession().getId();
};

exports.getCreationTime = function() {
	return new Date($.getSession().getCreationTime());
};

exports.getLastAccessedTime = function() {
	return new Date($.getSession().getLastAccessedTime());
};

exports.getMaxInactiveInterval = function() {
	return $.getSession().getMaxInactiveInterval();
};

exports.setMaxInactiveInterval = function(interval) {
	$.getSession().setMaxInactiveInterval(interval);
};

exports.invalidate = function() {
	$.getSession().invalidate();
};
