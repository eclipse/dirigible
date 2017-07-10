/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $*/
/* eslint-env node, dirigible */

exports.getExtensions = function(extensionPoint) {
	var extensions = org.eclipse.dirigible.api.v3.core.ExtensionsServiceFacade.getExtensions(extensionPoint);
	return extensions;
};

exports.getExtensionPoints = function() {
	var extensionPoints = org.eclipse.dirigible.api.v3.core.ExtensionsServiceFacade.getExtensionPoints();
	return extensionPoints;
};