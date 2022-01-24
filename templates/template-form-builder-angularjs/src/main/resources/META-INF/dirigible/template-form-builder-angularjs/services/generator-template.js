/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
exports.getTemplate = function() {
	    var template = {
            "name":"AngularJS Generator from Form Model",
            "description":"AngularJS Form Model generator template",
            "extension":"form",
            "sources": 
                [
                    {
                       "location": "/template-form-builder-angularjs/template/angularjs.html.template", 
                       "action": "generate",
                       "rename": "{{fileNameBase}}.html",
                       "engine": "javascript",
                       "handler": "/template-form-builder-angularjs/template/generatorView.js"
		            },
                    {
                       "location": "/template-form-builder-angularjs/template/angularjs.js.template", 
                       "action": "generate",
                       "rename": "{{fileNameBase}}.js",
                       "engine": "javascript",
                       "handler": "/template-form-builder-angularjs/template/generatorController.js"
		            }
                ]
        };
        return template;
}