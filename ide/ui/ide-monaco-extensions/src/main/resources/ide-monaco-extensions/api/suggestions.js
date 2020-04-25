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
var response = require("http/v4/response");
var request = require("http/v4/request");
var suggestionsParser = require("ide-monaco-extensions/api/utils/suggestionsParser");

var suggestions = suggestionsParser.parse(request.getParameter("moduleName"));

var secondLevelSuggestions = [];

suggestions
    .filter(e => e.returnType)
    .forEach(function(e) {
        e.returnType.functions.forEach(function(f) {
            f.parent = e.name.substring(0, e.name.indexOf("("));
            f.fqn = e.name + "." + f.name
        });
        secondLevelSuggestions = secondLevelSuggestions.concat(e.returnType.functions);
    });

suggestions = suggestions.concat(secondLevelSuggestions);

response.print(JSON.stringify(suggestions));
response.flush();
response.close();
