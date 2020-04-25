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
var java = require('core/v3/java');

exports.fromJson = function(input) {
	if(typeof input !== 'string'){
		input = JSON.stringify(input);
	}
	var output = java.call('org.eclipse.dirigible.api.v3.utils.Xml2JsonFacade', 'fromJson', [input]);
	return output;
};

exports.toJson = function(input) {
	if(typeof input !== 'string'){
		input = JSON.stringify(input);
	}
	var output = java.call('org.eclipse.dirigible.api.v3.utils.Xml2JsonFacade', 'toJson', [input]);
	return output;
};

