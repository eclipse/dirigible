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
var extensions = require("core/v4/extensions");

var modules = [];
var apiModulesExtensions = extensions.getExtensions("api-modules");

apiModulesExtensions.forEach(function(apiModule) {
	var module = require(apiModule);
	modules = modules.concat(module.getContent());
});

response.println(JSON.stringify(modules));