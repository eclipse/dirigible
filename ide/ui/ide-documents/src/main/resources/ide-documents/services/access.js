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
var rs = require("http/v4/rs");
var repositoryContent = require("repository/v4/content");
var repositoryManager = require("repository/v4/manager");

rs.service()
    .resource("")
        .get(function (ctx, request, response) {
            if (request.isUserInRole("Operator")) {
                let accessDefinitions = getAccessDefinitions();
                response.println(JSON.stringify(accessDefinitions));
            } else {
                response.setStatus(response.FORBIDDEN);
                response.println("Access forbidden");
            }
        })
        .put(function(ctx, request, response) {
            if (request.isUserInRole("Operator")) {
                let accessDefinitions = request.getJSON();
                updateAccessDefinitions(accessDefinitions);
                response.println(JSON.stringify(accessDefinitions));
            } else {
                response.setStatus(response.FORBIDDEN);
                response.println("Access forbidden");
            }
        })
.execute();

function getAccessDefinitions() {
    return JSON.parse(repositoryContent.getText("ide-documents/security/roles.access"));
}

function updateAccessDefinitions(accessDefinitions) {
    let path = "/registry/public/ide-documents/security/roles.access";
    let content = JSON.stringify(accessDefinitions);
    let resource = repositoryManager.getResource(path);
    if (resource.exists()) {    	
    	repositoryManager.deleteResource(path);
    }
    repositoryManager.createResource(path, content);
}
