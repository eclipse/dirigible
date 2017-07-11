/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* eslint-env node, dirigible */

var extensions = require('core/v3/extensions');
var response = require('http/v3/response');

function getMainMenu() {
    var mainmenu = [];
    // response.println('getting extensions...');
	var menuExtensions = extensions.getExtensions('/ide/extensions/mainmenu');
	// response.println('got extensions: ' + menuExtensions.length);
	for (var i=0; i<menuExtensions.length; i++) {
		var module = menuExtensions[i];
		// response.println('processing extension module: ' + module);
		menuExtension = require(module);
		var menu = menuExtension.getMenu();
		// response.println('menu to add: ' + JSON.stringify(menu));
		mainmenu.push(menu);
	}
	response.println(JSON.stringify(mainmenu));
}

getMainMenu();
