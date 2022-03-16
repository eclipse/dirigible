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
let extensions = require('core/v4/extensions');
let response = require('http/v4/response');

let mainmenu = [];
let menuExtensions = extensions.getExtensions('ide-documents-menu');

for (let i = 0; menuExtensions != null && i < menuExtensions.length; i++) {
    let module = menuExtensions[i];
    let menuExtension = require(module);
    let menu = menuExtension.getMenu();
    mainmenu.push(menu);
}

mainmenu.sort(function (p, n) {
    return (parseInt(p.order) - parseInt(n.order));
});

response.println(JSON.stringify(mainmenu));