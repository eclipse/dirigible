/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

var extensions = require('core/v3/extensions');
var response = require('http/v3/response');

var templates = [];
var templateExtensions = extensions.getExtensions('ide-template');
for (var i=0; i<templateExtensions.length; i++) {
    var module = templateExtensions[i];
    templateExtension = require(module);
    var template = templateExtension.getTemplate();
    template.id = module;
    templates.push(template);
}
response.println(JSON.stringify(templates));
