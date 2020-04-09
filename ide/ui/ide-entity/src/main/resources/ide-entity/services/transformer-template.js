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
exports.getTemplate = function() {
	    var template = {
            "name":"Entity Data to JSON Model Transformer",
            "description":"Model transformer template",
            "extension":"edm",
            "sources": [
                   {
                       "location": "/ide-entity/template/source.model.template", 
                       "action": "generate",
                       "rename": "{{fileNameBase}}.model",
                       "engine": "javascript",
                       "handler": "/ide-entity/template/transformer.js"
		    }]
        };
        return template;
}