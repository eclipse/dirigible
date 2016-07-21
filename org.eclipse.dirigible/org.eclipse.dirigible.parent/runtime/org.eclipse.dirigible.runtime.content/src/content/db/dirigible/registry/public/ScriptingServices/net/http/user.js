/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ javax */
/* eslint-env node, dirigible */

var request = require("net/http/request");

exports.getName = function() {
	var principal = $.getRequest().getUserPrincipal();
	if (principal) {
		return principal.getName();
	}
	return null;
};

exports.isInRole = function(role) {
	return request.isUserInRole(role);
};
