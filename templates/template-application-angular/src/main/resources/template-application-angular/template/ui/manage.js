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
exports.getSources = function(parameters) {
    return [{
        'location': '/template-application-angular/ui/perspectives/views/manage/index.html.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/index.html',
        'engine': 'velocity',
        'collection': 'uiManageModels'
    }, {
        'location': '/template-application-angular/ui/perspectives/views/manage/controller.js.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/controller.js',
        'engine': 'velocity',
        'collection': 'uiManageModels'
    }, {
        'location': '/template-application-angular/ui/perspectives/views/manage/extensions/view.js.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.js',
        'collection': 'uiManageModels'
    }, {
		'location': '/template-application-angular/ui/perspectives/views/manage/extensions/view.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/view.extension',
		'collection': 'uiManageModels'
	}, {
        'location': '/template-application-angular/ui/perspectives/views/manage/extensions/menu/item.extension.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/menu/item.extension',
        'collection': 'uiManageModels'
    }, {
        'location': '/template-application-angular/ui/perspectives/views/manage/extensions/menu/item.js.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/{{fileName}}/extensions/menu/item.js',
        'collection': 'uiManageModels'
    }];
};
