/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var response = require("http/v4/response");
var request = require("http/v4/request");
var simpleRequire = require("ide-monaco-extensions/api/utils/simpleRequire");

var suggestions = [];

var module = simpleRequire.load(request.getParameter("moduleName"));

for (var i in module) {
	var functionText = module[i].toString();
	var suggestion = i + functionText.substring(functionText.indexOf("("), functionText.indexOf(")") + 1);
	var suggestionDescription = suggestion;
	suggestions.push({
		name: suggestion,
		description: suggestionDescription
	});
}

response.print(JSON.stringify(suggestions));
response.flush();
response.close();