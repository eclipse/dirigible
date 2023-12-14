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
 * API Destinations
 *
 * Note: This module is supported only with the Mozilla Rhino engine
 */

import { configurations } from ".";
import { httpClient } from "@dirigible/http/client";
let oauth = dirigibleRequire("security/oauth"); // TODO: check this API

const JClass = Java.type("java.lang.Class");
const DestinationsFacade = Java.type("org.eclipse.dirigible.components.api.core.DestinationsFacade");

export class Destinations{

    public static get (name) {
        if (this.isCloudEnvironment()) {
            return JSON.parse(this.getCloudDestination(name));
        }
        return JSON.parse(DestinationsFacade.get(name));
    };

    public static set (name, destination) {
        if (this.isCloudEnvironment()) {
            this.createOrUpdateCloudDestination(name, destination);
        }
        DestinationsFacade.set(name, JSON.stringify(destination));
    };

    public static remove(name) {
        if (!this.isCloudEnvironment()) {
            throw new Error("The delete destination operation is not supported for non-cloud environments");
        }
        this.deleteCloudDestination(name);
    };

    public static isCloudEnvironment() {
        try {
            JClass.forName("org.eclipse.dirigible.cf.CloudFoundryModule");
            return true;
        } catch (e) {
            // Do nothing
        }
        try {
            JClass.forName("org.eclipse.dirigible.kyma.KymaModule");
            return true;
        } catch (e) {
            // Do nothing
        }
        return false;
    }

    private static getCloudDestination(name) {
        let token = this.getOAuthToken();

        let destinationUrl = `${this.getDestinationsBasePath()}/${name}`;

        let requestOptions = {
            headers: [{
                name: "Authorization",
                value: `${token.token_type} ${token.access_token}`
            }]
        };

        let response = httpClient.get(destinationUrl, requestOptions);

        if (response.statusCode === 404) {
            throw new Error(`Destination with name '${name}' not found`);
        };

        return response.text;

    }

    private static getDestinationsBasePath() {
        return `${configurations.get("DIRIGIBLE_DESTINATION_URI")}/destination-configuration/v1/destinations`;
    }

    private static getInstanceDestinationsBasePath() {
        return `${configurations.get("DIRIGIBLE_DESTINATION_URI")}/destination-configuration/v1/instanceDestinations`;
    }

    private static getSubaccountDestinationsBasePath() {
        return `${configurations.get("DIRIGIBLE_DESTINATION_URI")}/destination-configuration/v1/subaccountDestinations`;
    }

    private static createOrUpdateCloudDestination(name, destination) {
        let isExistingDestination = true;
        try {
            this.getCloudDestination(name);
        } catch (e) {
            isExistingDestination = false;
        }

        let instanceDestinationUrl = this.getInstanceDestinationsBasePath();
        destination.Name = name;

        let token = this.getOAuthToken();
        let requestOptions = {
            headers: [{
                name: "Authorization",
                value: `${token.token_type} ${token.access_token}`
            }],
            text: JSON.stringify(destination)
        };

        if (isExistingDestination) {
            let response = httpClient.put(instanceDestinationUrl, requestOptions);
            if (response.statusCode != 200) {
                throw new Error(`Error occurred while updating destinaton '${name}': ${response.text}`);
            }
        } else {
            let response = httpClient.post(instanceDestinationUrl, requestOptions);
            if (response.statusCode != 201) {
                throw new Error(`Error occurred while creating destinaton '${name}': ${response.text}`);
            }
        }
    }

    private static deleteCloudDestination(name) {
        let token = this.getOAuthToken();
        let requestOptions = {
            headers: [{
                name: "Authorization",
                value: `${token.token_type} ${token.access_token}`
            }]
        };
        let url = `${this.getInstanceDestinationsBasePath()}/${name}`;
        let response = httpClient.delete(url, requestOptions);
        if (response.statusCode != 200) {
            throw new Error(`Error occurred while deleting destinaton '${name}': ${response.text}`);
        }
    }

    private static getOAuthToken() {
        let oauthClientId = configurations.get("DIRIGIBLE_DESTINATION_CLIENT_ID");
        let oauthClientSecret = configurations.get("DIRIGIBLE_DESTINATION_CLIENT_SECRET");
        let oauthUrl = configurations.get("DIRIGIBLE_DESTINATION_URL");
        let uri = configurations.get("DIRIGIBLE_DESTINATION_URI");

        if (!oauthClientId || !oauthClientSecret || !oauthUrl || !uri) {
            throw new Error("Invalid destination configuration");
        }

        let oauthConfig = {
            url: oauthUrl,
            clientId: oauthClientId,
            clientSecret: oauthClientSecret
        };

        let client = oauth.getClient(oauthConfig);
        return client.getToken();
    }

}