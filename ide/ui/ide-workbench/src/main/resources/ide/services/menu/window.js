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
var extensions = require('core/v3/extensions');

exports.getMenu = function() {
	var menu = {
			"name":"Window",
			"link":"#",
			"order":"800",
			"onClick":"alert('Window has been clicked')",
			"items":[
				{
					"name":"Open Perspective",
					"link":"#",
					"order":"810",
					"items":[]
				},
				{
					"name":"Show View",
					"link":"#",
					"order":"820",
					"items":[]
				}
			]
		};
		
		
		
	var perspectiveExtensions = extensions.getExtensions('ide-perspective');
	for (var i=0; i<perspectiveExtensions.length; i++) {
    	var module = perspectiveExtensions[i];
    	perspectiveExtension = require(module);
    	var perspectiveInfo = perspectiveExtension.getPerspective();
    	var perspectiveMenu = {
			"name": perspectiveInfo.name,
			"link":"#",
			"order":"" + (810 + i),
			"onClick":"window.open('" + perspectiveInfo.link + "', '_blank')"};
    	menu.items[0].items.push(perspectiveMenu);
	}
	
	var viewExtensions = extensions.getExtensions('ide-view');
	for (var i=0; i<viewExtensions.length; i++) {
    	var module = viewExtensions[i];
    	viewExtension = require(module);
    	var viewInfo = viewExtension.getView();
    	var viewMenu = {
			"name": viewInfo.name,
			"link":"#",
			"order":"" + (820 + i),
			"onClick":"window.open('" + viewInfo.link + "', '_blank')"};
    	menu.items[1].items.push(viewMenu);
	}
		
	return menu;
}
