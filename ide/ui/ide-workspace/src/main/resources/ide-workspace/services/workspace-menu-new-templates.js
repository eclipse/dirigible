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
var extensions = require('core/v4/extensions');
var response = require('http/v4/response');

var templates = [];
var templateExtensions = extensions.getExtensions('ide-workspace-menu-new-template');

for (var i = 0; i < templateExtensions.length; i++) {
    var module = templateExtensions[i];
    try {
    	var templateExtension = require(module);
    	var template = templateExtension.getTemplate();
    	template.id = module;
    	templates.push(template);	
    } catch(error) {
    	console.error('Error occured while loading metadata for the template: ' + module);
    	console.error(error);
    }
}

response.println(JSON.stringify(templates));
