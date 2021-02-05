/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
var extensions = require('core/v4/extensions');
var response = require('http/v4/response');

var mainmenu = [];
var menuExtensions = extensions.getExtensions('ide-discussions-menu');
for (var i=0; i<menuExtensions.length; i++) {
    var module = menuExtensions[i];
    menuExtension = require(module);
    var menu = menuExtension.getMenu();
    mainmenu.push(menu);
}
mainmenu.sort(function(p, n) {
	return (parseInt(p.order) - parseInt(n.order));
});
response.println(JSON.stringify(mainmenu));

