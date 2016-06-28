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

exports.validate = function(uuid) {
	try {
		var uuidUtils = $.getUuidUtils();
		if (engine === "nashorn") {
			uuidUtils.class.static.fromString(uuid).toString();
		} else {
			uuidUtils.fromString(uuid).toString();
		}
		return true;
	} catch(e) {
		console.error(e.message);
		return false;
	}


};

exports.random = function() {
	var uuidUtils = $.getUuidUtils();
	if (engine === "nashorn") {
		return uuidUtils.class.static.randomUUID().toString();
	}
	return uuidUtils.randomUUID().toString();
};
