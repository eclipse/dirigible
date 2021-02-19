/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * API v4 OAuth
 */

var base64 = require("utils/v4/base64");
var bytes = require("io/v4/bytes");

exports.getToken = function() {
    let request = org.eclipse.dirigible.api.v3.http.HttpRequestFacade.getRequest();
	let jwtToken = org.eclipse.dirigible.oauth.utils.JwtUtils.getJwt(request);
    if (jwtToken === undefined || jwtToken === null || jwtToken === "") {
        console.error("No JWT token present, the reason could be that this is not a OAuth enabled deployment.")
        throw new Error("No JWT token present, the reason could be that this is not a OAuth enabled deployment.");
    }
    let tokens = jwtToken.split(".");
    let payload = bytes.byteArrayToText(base64.decode(tokens[1]));
    return JSON.parse(payload);
};

exports.get = function(name) {
    return exports.getToken()[name];
};

exports.getEmail = function() {
    return exports.get("email");
};

exports.getUsername = function() {
    return exports.get("user_name");
};

exports.getGrantType = function() {
    return exports.get("grant_type");
};

exports.verify = function(token) {
    try {
        org.eclipse.dirigible.oauth.utils.JwtUtils.verifyJwt(token);
    } catch (e) {
        console.warn("Error occured while validating JWT: " + e);
        return false;
    }
    return true;
};