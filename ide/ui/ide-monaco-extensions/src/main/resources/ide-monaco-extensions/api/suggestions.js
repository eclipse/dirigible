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
var suggestionsParser = require("ide-monaco-extensions/api/utils/suggestionsParser");

var suggestions = suggestionsParser.parse(request.getParameter("moduleName"));

response.print(JSON.stringify(suggestions));
response.flush();
response.close();
