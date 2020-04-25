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

var views = [];
var viewExtensions = extensions.getExtensions('ide-view');

for (var i = 0; i < viewExtensions.length; i++) {
    var module = viewExtensions[i];
    try {
    	var viewExtension = require(module);
    	var view = viewExtension.getView();
    	views.push(view);	
    } catch(error) {
    	console.error('Error occured while loading metadata for the view: ' + module);
    	console.error(error);
    }
}

response.println(JSON.stringify(views));
