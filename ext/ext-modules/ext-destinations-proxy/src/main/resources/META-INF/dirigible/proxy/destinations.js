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
const rs = require("http/v4/rs");
const bytes = require("io/v4/bytes");
const httpClient = require("http/v4/client");
const xml = require("utils/v4/xml");
const destinations = require("core/v4/destinations");

const config = require("core/v4/configurations");

if (config.get("DIRIGIBLE_DESTINATIONS_PROXY_ENABLED", "false") !== "true") {
    throw new Error("Destinations Proxy is disabled");
}

const IGNORED_HEADERS = [
    "host",
    "accept-encoding",
    "content-length"
];

rs.service()
    .resource("{destinationName}/{path*}")
    .get(handleGetRequest)
    .put(handlePutRequest)
    .post(handlePostRequest)
    .delete(handleDeleteRequest)
    .execute();

function handleGetRequest(ctx, request, response) {
    let httpClientConfig = buildHttpClientConfig(request, ctx.pathParameters.destinationName, ctx.pathParameters.path);
    let proxyResponse = httpClient.get(httpClientConfig.url, httpClientConfig.options);
    processProxyResponse(response, proxyResponse);
}

function handlePutRequest(ctx, request, response) {
    let httpClientConfig = buildHttpClientConfig(request, ctx.pathParameters.destinationName, ctx.pathParameters.path);
    let proxyResponse = httpClient.put(httpClientConfig.url, httpClientConfig.options);
    processProxyResponse(response, proxyResponse);
}

function handlePostRequest(ctx, request, response) {
    let httpClientConfig = buildHttpClientConfig(request, ctx.pathParameters.destinationName, ctx.pathParameters.path);
    let proxyResponse = httpClient.post(httpClientConfig.url, httpClientConfig.options);
    processProxyResponse(response, proxyResponse);
}

function handleDeleteRequest(ctx, request, response) {
    let httpClientConfig = buildHttpClientConfig(request, ctx.pathParameters.destinationName, ctx.pathParameters.path);
    let proxyResponse = httpClient.delete(httpClientConfig.url, httpClientConfig.options);
    processProxyResponse(response, proxyResponse);
}

function processProxyResponse(response, proxyResponse) {
    proxyResponse.headers.forEach(e => response.setHeader(e.name, e.value));
    response.setStatus(proxyResponse.statusCode);
    let data = proxyResponse.text;
    if (data === undefined && proxyResponse.data) {
        data = bytes.byteArrayToText(proxyResponse.data);
    }
    response.println(data);
}

function buildHttpClientConfig(request, destinationName, path) {
    let dest = destinations.get(destinationName);

    let headers = request.getHeaderNames().filter(e => !IGNORED_HEADERS.includes(e.toLowerCase())).map(e => {
        return {
            name: e,
            value: request.getHeader(e)
        }
    })

    if (dest.authTokens && dest.authTokens[0]) {
        headers.push({
            name: dest.authTokens[0].http_header.key,
            value: dest.authTokens[0].http_header.value
        });
    }

    let url = `${dest.destinationConfiguration.URL}/${path}${request.getQueryString() ? "?" + request.getQueryString() : ""}`;

    let httpClientConfig = {
        url: url,
        options: {
            headers: headers,
            text: getPayload(request),
            contentType: getContentType(headers)
        }
    };
    setHttpClientConfigContentType(httpClientConfig);
    return httpClientConfig;
}

function setHttpClientConfigContentType(httpClientConfig) {
    if (!httpClientConfig.options.contentType && httpClientConfig.options.text) {
        try {
            JSON.parse(httpClientConfig.options.text);
            httpClientConfig.options.contentType = "application/json";
            return;
        } catch (e) {
            // Do nothing
        }
        try {
            xml.toJson(httpClientConfig.options.text)
            httpClientConfig.options.contentType = "application/xml";
            return;
        } catch (e) {
            // Do nothing
        }

        httpClientConfig.options.contentType = "plain/text";
    }
}

function getPayload(request) {
    let payload = request.getText();
    if (payload) {
        return payload;
    }
}

function getContentType(headers) {
    if (headers) {
        for (let i = 0; i < headers.length; i++) {
            if (headers[i].name.toLowerCase() === "content-type") {
                return headers[i].value;
            }
        }
    }
}
