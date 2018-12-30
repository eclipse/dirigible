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
exports.getSources = function(parameters) {
	return [{
		'location': '/template-application-angular/ui/perspectives/views/report/line/index.html.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/index.html',
		'engine': 'velocity',
		'collection': 'uiReportLinesModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/report/line/controller.js.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/controller.js',
		'engine': 'velocity',
		'collection': 'uiReportLinesModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/report/line/extensions/view.js.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.js',
		'collection': 'uiReportLinesModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/report/line/extensions/view.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.extension',
		'collection': 'uiReportLinesModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/report/line/extensions/menu/item.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/menu/item.extension',
		'collection': 'uiReportLinesModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/report/line/extensions/menu/item.js.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/menu/item.js',
		'collection': 'uiReportLinesModels'
	}];
};
