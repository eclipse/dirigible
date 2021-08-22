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
var accessUtils = require("ide-documents/security/accessUtils");

rs.service()
    .resource("")
        .get(function (ctx, request, response) {
            if (request.isUserInRole("Operator")) {
                let accessDefinitions = accessUtils.getAccessDefinitions();
                response.println(JSON.stringify(accessDefinitions, null, 2));
            } else {
                response.setStatus(response.FORBIDDEN);
                response.println("Access forbidden");
            }
        })
        .put(function(ctx, request, response) {
            if (request.isUserInRole("Operator")) {
                let accessDefinitions = request.getJSON();
                accessUtils.updateAccessDefinitions(accessDefinitions);
                response.println(JSON.stringify(accessDefinitions, null, 2));
            } else {
                response.setStatus(response.FORBIDDEN);
                response.println("Access forbidden");
            }
        })
.execute();