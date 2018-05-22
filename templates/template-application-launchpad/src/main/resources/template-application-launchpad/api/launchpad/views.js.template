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
var request = require('http/v3/request');
var response = require('http/v3/response');

var extensionPoint = request.getParameter('extensionPoint');

var extensionPoints = extensionPoint.split(',');
var views = [];

for (var i = 0; i < extensionPoints.length; i ++) {
	var viewExtensions = extensions.getExtensions(extensionPoints[i]);
	for (var j = 0; viewExtensions !== null && j < viewExtensions.length; j++) {
	    var viewExtension = require(viewExtensions[j]);
	    var view = viewExtension.getView();
	    views.push(view);
	}
}

response.println(JSON.stringify(views));
