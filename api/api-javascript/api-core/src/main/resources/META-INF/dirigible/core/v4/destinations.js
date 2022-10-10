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
/**
 * API v4 Destinations
 * 
 * Note: This module is supported only with the Mozilla Rhino engine
 */
let config = require("core/v4/configurations");
let httpClient = require("http/v4/client");
let oauth = require("security/v4/oauth");

exports.get = function (name) {
	if (isCloudEnvironment()) {
		return JSON.parse(getCloudDestination(name));
	}
	return JSON.parse(org.eclipse.dirigible.api.v3.core.DestinationsFacade.get(name));
};

exports.set = function (name, destination) {
	if (isCloudEnvironment()) {
		createOrUpdateCloudDestination(name, destination);
	}
	org.eclipse.dirigible.api.v3.core.DestinationsFacade.set(name, JSON.stringify(destination));
};

exports.delete = function (name) {
	if (!isCloudEnvironment()) {
		throw new Error("The delete destination operation is not supported for non-cloud environments");
	}
	deleteCloudDestination(name);
};

function isCloudEnvironment() {
	try {
		java.lang.Class.forName("org.eclipse.dirigible.cf.CloudFoundryModule");
		return true;
	} catch (e) {
		// Do nothing
	}
	try {
		java.lang.Class.forName("org.eclipse.dirigible.kyma.KymaModule");
		return true;
	} catch (e) {
		// Do nothing
	}
	return false;
}

function getCloudDestination(name) {
	let token = getOAuthToken();

	let instanceDestinationUrl = `${getInstanceDestinationsBasePath()}/${name}`;
	let subaccountDestinationUrl = `${getSubaccountDestinationsBasePath()}/${name}`;

	let requestOptions = {
		headers: [{
			name: "Authorization",
			value: `${token.token_type} ${token.access_token}`
		}]
	};

	let response = httpClient.get(instanceDestinationUrl, requestOptions);

	// Fallback to subaccount level destination
	if (response.statusCode === 404) {
		response = httpClient.get(subaccountDestinationUrl, requestOptions);
	}

	if (response.statusCode === 404) {
		throw new Error(`Destination with name '${name}' not found`);
	};

	return response.text;
}

function getInstanceDestinationsBasePath() {
	return `${config.get("DIRIGIBLE_DESTINATION_URI")}/destination-configuration/v1/instanceDestinations`;
}

function getSubaccountDestinationsBasePath() {
	return `${config.get("DIRIGIBLE_DESTINATION_URI")}/destination-configuration/v1/subaccountDestinations`;
}

function createOrUpdateCloudDestination(name, destination) {
	let isExistingDestination = true;
	try {
		getCloudDestination(name);
	} catch (e) {
		isExistingDestination = false;
	}

	let instanceDestinationUrl = getInstanceDestinationsBasePath();
	destination.Name = name;

	let token = getOAuthToken();
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

function deleteCloudDestination(name) {
	let token = getOAuthToken();
	let requestOptions = {
		headers: [{
			name: "Authorization",
			value: `${token.token_type} ${token.access_token}`
		}]
	};
	let url = `${getInstanceDestinationsBasePath()}/${name}`;
	let response = httpClient.delete(url, requestOptions);
	if (response.statusCode != 200) {
		throw new Error(`Error occurred while deleting destinaton '${name}': ${response.text}`);
	}
}

function getOAuthToken() {
	let oauthClientId = config.get("DIRIGIBLE_DESTINATION_CLIENT_ID");
	let oauthClientSecret = config.get("DIRIGIBLE_DESTINATION_CLIENT_SECRET");
	let oauthUrl = config.get("DIRIGIBLE_DESTINATION_URL");
	let uri = config.get("DIRIGIBLE_DESTINATION_URI");

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