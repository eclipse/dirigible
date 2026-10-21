/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.sdk.security;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.components.api.http.HttpClientFacade;
import org.eclipse.dirigible.components.api.http.client.HttpClientResponse;

/**
 * Obtains OAuth access tokens from a token endpoint, by default with the client credentials grant.
 * The grant type, client id and client secret are sent as an
 * {@code application/x-www-form-urlencoded} POST body (RFC 6749, section 4.4), through the
 * platform's outbound HTTP client - so the platform's proxy settings apply.
 *
 * <pre>{@code
 * OAuthClient client = new OAuthClient("https://example.com/oauth/token", "client-id", "client-secret");
 * String token = client.getToken(); // the JSON token response: access_token, token_type, expires_in
 * }</pre>
 */
public final class OAuthClient {

    private static final String CLIENT_CREDENTIALS = "client_credentials";
    private static final int OK = 200;

    private final String url;
    private final String clientId;
    private final String clientSecret;
    private final String grantType;

    /**
     * A client for the client credentials grant.
     *
     * @param url the token endpoint
     * @param clientId the client id
     * @param clientSecret the client secret
     */
    public OAuthClient(String url, String clientId, String clientSecret) {
        this(url, clientId, clientSecret, CLIENT_CREDENTIALS);
    }

    /**
     * A client for the given grant type.
     *
     * @param url the token endpoint
     * @param clientId the client id
     * @param clientSecret the client secret
     * @param grantType the grant type, e.g. {@code client_credentials}
     */
    public OAuthClient(String url, String clientId, String clientSecret, String grantType) {
        this.url = Objects.requireNonNull(url, "url");
        this.clientId = Objects.requireNonNull(clientId, "clientId");
        this.clientSecret = Objects.requireNonNull(clientSecret, "clientSecret");
        this.grantType = Objects.requireNonNull(grantType, "grantType");
    }

    /**
     * Requests a token.
     *
     * @return the token endpoint's JSON response, carrying the {@code access_token}
     * @throws IOException if the endpoint cannot be reached, or answers with another status than 200
     */
    public String getToken() throws IOException {
        Map<String, Object> options = Map.of("params",
                List.of(param("grant_type", grantType), param("client_id", clientId), param("client_secret", clientSecret)));
        HttpClientResponse response = GsonHelper.fromJson(HttpClientFacade.post(url, GsonHelper.toJson(options)), HttpClientResponse.class);
        if (response.getStatusCode() != OK) {
            throw new IOException(
                    "The OAuth token endpoint [" + url + "] answered with status " + response.getStatusCode() + ": " + response.getText());
        }
        return response.getText();
    }

    private static Map<String, String> param(String name, String value) {
        return Map.of("name", name, "value", value);
    }
}
