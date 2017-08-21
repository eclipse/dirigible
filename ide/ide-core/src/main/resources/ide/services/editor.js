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

var editors = [];
var editorExtensions = extensions.getExtensions('ide-editor');
for (var i=0; i<editorExtensions.length; i++) {
    var module = editorExtensions[i];
    editorExtension = require(module);
    var editor = editorExtension.getEditor();
    editors.push(editor);
}
response.println(JSON.stringify(editors));
