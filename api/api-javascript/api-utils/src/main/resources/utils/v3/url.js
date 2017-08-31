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

exports.encode = function(input, charset) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.UrlFacade', 'encode', [input, charset]);
	return output;
};

exports.decode = function(input, charset) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.UrlFacade', 'decode', [input, charset]);
	return output;
};

exports.escape = function(input) {
	var output = java.call('org.eclipse.dirigible.api.v3.utils.UrlFacade', 'escape', [input]);
	return output;
};

