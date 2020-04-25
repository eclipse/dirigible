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
exports.getSources = function(parameters) {
	return [{
		'location': '/template-application-openui5/ui/perspectives/extensions/tile/tile.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{name}}/extensions/tile/tile.extension',
		'collection': 'uiPrimaryModels'
	}, {
		'location': '/template-application-openui5/ui/perspectives/extensions/tile/tile.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{name}}/extensions/tile/tile.extension',
		'collection': 'uiReportModels'
	}, {
		'location': '/template-application-openui5/ui/perspectives/extensions/tile/tile.js.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{name}}/extensions/tile/tile.js',
		'engine': 'velocity',
		'collection': 'uiPrimaryModels'
	}, {
		'location': '/template-application-openui5/ui/perspectives/extensions/tile/tile.js.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{name}}/extensions/tile/tile.js',
		'engine': 'velocity',
		'collection': 'uiReportModels'
	}];
};
