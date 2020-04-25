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

exports.getExtensions = function(extensionPoint) {
	var extensions = java.call('org.eclipse.dirigible.api.v3.core.ExtensionsServiceFacade', 'getExtensions', [extensionPoint]);
	extensions = JSON.parse(extensions);
	return extensions;
};

exports.getExtensionPoints = function() {
	var extensionPoints = java.call('org.eclipse.dirigible.api.v3.core.ExtensionsServiceFacade', 'getExtensionPoints', []);
	extensionPoints = JSON.parse(extensionPoints);
	return extensionPoints;
};
