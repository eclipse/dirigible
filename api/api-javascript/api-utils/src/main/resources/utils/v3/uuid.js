/*
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var java = require('core/v3/java');

exports.random = function() {
	var uuid = java.call('org.eclipse.dirigible.api.v3.utils.UuidFacade', 'random', []);
	return uuid;
};

exports.validate = function(input) {
	var valid = java.call('org.eclipse.dirigible.api.v3.utils.UuidFacade', 'validate', [input]);
	return valid;
};

