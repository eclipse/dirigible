/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
exports.getSources = function(parameters) {
    return [{
			'location': '/template-application-openui5/ui/perspectives/views/manage/view/Navigation.view.xml.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/view/Navigation.view.xml'
		}, {
			'location': '/template-application-openui5/ui/perspectives/views/manage/view/View.view.xml.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/view/{{fileName}}.view.xml',
			'start': '[[',
			'end': ']]',
			'collection': 'uiManageModels'
		},  {
			'location': '/template-application-openui5/ui/perspectives/views/manage/view/AddEditDialog.fragment.xml.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/view/AddEditDialog{{fileName}}.fragment.xml',
			'start': '[[',
			'end': ']]',
			'collection': 'uiManageModels'
		}, {
			'location': '/template-application-openui5/ui/perspectives/views/manage/controller/Navigation.controller.js.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/controller/Navigation.controller.js',
			'collection': 'uiManageModels'
		}, {
			'location': '/template-application-openui5/ui/perspectives/views/manage/controller/View.controller.js.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/controller/{{fileName}}.controller.js',
			'start': '[[',
			'end': ']]',
			'collection': 'uiManageModels'
		}, {
			'location': '/template-application-openui5/ui/perspectives/views/manage/Component.js.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/Component.js'
		}, {
			'location': '/template-application-openui5/ui/perspectives/views/manage/manifest.json.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/manifest.json'
		}, {
			'location': '/template-application-openui5/ui/perspectives/views/manage/model/models.js.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/model/models.js'
		}, {
			'location': '/template-application-openui5/ui/perspectives/views/manage/model/entity.json.template', 
			'action': 'generate',
			'rename': 'ui/{{perspectiveName}}/model/entity.json'
		}];
};
