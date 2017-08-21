/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* eslint-env node, dirigible */

var java = require('core/v3/java');

exports.encode = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.Base64Facade', 'encode', [JSON.stringify(input)]);
	return output;
};

exports.decode = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.Base64Facade', 'decode', [input]);
	if (output && output != null) {
		return JSON.parse(output);
	}
	return output;
};

