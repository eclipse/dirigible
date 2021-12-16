/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */

/**
 * API v4 Files
 *
 * Note: This module is supported only with the Mozilla Rhino engine
 */

exports.fromJson = function(input) {
	let data = input;
	if(typeof data !== "string"){
		data = JSON.stringify(input);
	}
	return org.eclipse.dirigible.api.v3.utils.Xml2JsonFacade.fromJson(data);
};

exports.toJson = function(input) {
	let data = input;
	if(typeof data !== "string"){
		data = JSON.stringify(input);
	}
	return org.eclipse.dirigible.api.v3.utils.Xml2JsonFacade.toJson(data);
};

