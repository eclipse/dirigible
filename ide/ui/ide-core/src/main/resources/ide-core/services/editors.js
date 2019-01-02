/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var extensions = require('core/v3/extensions');
var response = require('http/v3/response');

var editors = [];
var editorExtensions = extensions.getExtensions('ide-editor');

for (var i = 0; editorExtensions != null && i < editorExtensions.length; i++) {
    var module = editorExtensions[i];
    try {
    	var editorExtension = require(module);
    	var editor = editorExtension.getEditor();
    	editors.push(editor);	
    } catch(error) {
    	console.error('Error occured while loading metadata for the editor: ' + module);
    	console.error(error);
    }
}

response.println(JSON.stringify(editors));
