/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
	var data = input;
	if(typeof data !== "string"){
		data = JSON.stringify(input);
	}
	return org.eclipse.dirigible.api.v3.utils.Xml2JsonFacade.fromJson(data);
};

exports.toJson = function(input) {
	var data = input;
	if(typeof data !== "string"){
		data = JSON.stringify(input);
	}
	return org.eclipse.dirigible.api.v3.utils.Xml2JsonFacade.toJson(data);
};

