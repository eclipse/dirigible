/*
 * Copyright (c) 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
var response = require("http/v4/response");
var extensions = require("core/v4/extensions");
var modulesParser = require("ide-monaco-extensions/api/utils/modulesParser");

var modules = [];
var apiModulesExtensions = extensions.getExtensions("api-modules");

apiModulesExtensions.forEach(function(apiModule) {
	var module = require(apiModule);
	modules = modules.concat(module.getContent());
});

let repositoryModules = modulesParser.getModules();
modules = modules.concat(repositoryModules);

response.println(JSON.stringify(modules));