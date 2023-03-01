/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * API OAuth
 */
const httpClient = require("http/client");
const url = require("utils/url");
const base64 = require("utils/base64");
const bytes = require("io/bytes");

exports.getToken = function() {
    let request = org.eclipse.dirigible.components.api.http.HttpRequestFacade.getRequest();
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

exports.getClient = function(config) {
    return new OAuthClient(config);
};

function OAuthClient (config) {
    this.url = config ? config.url : null;
    this.isAbsoluteUrl = config && config.isAbsoluteUrl ? config.isAbsoluteUrl : false;
    this.clientId = config ? config.clientId : null;
    this.clientSecret = config ? config.clientSecret : null;
    this.grantType = config && config.granType ? config.granType : "client_credentials";

    this.setUrl = function(url) {
        this.url = url;
    };

    this.setClientId = function(clientId) {
        this.clientId = clientId;
    };

    this.setClientSecret = function(clientSecret) {
        this.clientSecret = clientSecret;
    };

    this.setGrantType = function(grantType) {
        this.grantType = grantType;
    };

    this.getToken = function() {
        if (!this.url) {
            console.error("The OAuth 'url' property is not provided.")
            throw new Error("The OAuth 'url' property is not provided.");
        } else if (!this.clientId) {
            console.error("The OAuth 'clientId' property is not provided.")
            throw new Error("The OAuth 'clientId' property is not provided.");
        } else if (!this.clientSecret) {
            console.error("The OAuth 'clientSecret' property is not provided.")
            throw new Error("The OAuth 'clientSecret' property is not provided.");
        }
        let oauthUrl = this.url;
        if (!this.isAbsoluteUrl) {
            oauthUrl += "/oauth/token";
        }
        let oauthResponse = httpClient.post(oauthUrl, {
            params: [{
                name: "grant_type",
                value: this.grantType
            }, {
                name: "client_id",
                value: url.encode(this.clientId)
            }, {
                name: "client_secret",
                value: url.encode(this.clientSecret)
            }],
            headers: [{
                name: "Content-Type",
                value: "application/x-www-form-urlencoded"
            }]
        });
        if (oauthResponse.statusCode !== 200) {
            let errorMessage = `Error occurred while retrieving OAuth token. Status code: [${response.status}], text: [${response.text}]`;
            console.error(errorMessage);
            throw new Error(errorMessage);
        }
        return JSON.parse(oauthResponse.text);
    };
}
