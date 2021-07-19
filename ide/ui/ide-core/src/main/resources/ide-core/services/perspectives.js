/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
var extensions = require('core/v4/extensions');
var response = require('http/v4/response');

var perspectives = [];
var perspectiveExtensions = extensions.getExtensions('ide-perspective');

for (var i = 0; i < perspectiveExtensions.length; i++) {
    var module = perspectiveExtensions[i];
    try {
    	var perspectiveExtension = require(module);
    	var perspective = perspectiveExtension.getPerspective();
    	perspectives.push(perspective);
    } catch(error) {
    	console.error('Error occured while loading metadata for the perspective: ' + module);
    	console.error(error);
    }
    
}

perspectives.sort(function(p, n) {
	return (parseInt(p.order) - parseInt(n.order));
});

response.println(JSON.stringify(perspectives));
