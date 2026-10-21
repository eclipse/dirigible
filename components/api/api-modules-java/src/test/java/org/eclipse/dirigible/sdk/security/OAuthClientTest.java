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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

/**
 * The token request {@link OAuthClient} sends, against a local token endpoint.
 */
class OAuthClientTest {

    private static final String TOKEN_RESPONSE = "{\"access_token\":\"abc\",\"token_type\":\"Bearer\",\"expires_in\":60}";

    private HttpServer server;
    private String method;
    private String contentType;
    private String body;

    @BeforeEach
    void startTokenEndpoint() throws IOException {
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.start();
    }

    @AfterEach
    void stopTokenEndpoint() {
        server.stop(0);
    }

    @Test
    void theClientCredentialsArePostedAsAFormAndTheTokenResponseIsReturned() throws IOException {
        String url = tokenEndpoint(200, TOKEN_RESPONSE);

        String token = new OAuthClient(url, "it client", "s3cr+t/=&").getToken();

        assertEquals(TOKEN_RESPONSE, token);
        assertEquals("POST", method);
        assertTrue(contentType.startsWith("application/x-www-form-urlencoded"), contentType);
        // One decoding gives back the credentials as written: they are form-encoded exactly once.
        assertEquals(Map.of("grant_type", "client_credentials", "client_id", "it client", "client_secret", "s3cr+t/=&"), form(body));
    }

    @Test
    void theGrantTypeItIsGivenIsSent() throws IOException {
        String url = tokenEndpoint(200, TOKEN_RESPONSE);

        new OAuthClient(url, "id", "secret", "password").getToken();

        assertEquals("password", form(body).get("grant_type"));
    }

    @Test
    void aRefusalIsAnIOExceptionCarryingTheStatusAndTheEndpointsAnswer() {
        String url = tokenEndpoint(401, "{\"error\":\"invalid_client\"}");

        IOException refusal = assertThrows(IOException.class, () -> new OAuthClient(url, "id", "wrong").getToken());

        assertTrue(refusal.getMessage()
                          .contains("401"),
                refusal.getMessage());
        assertTrue(refusal.getMessage()
                          .contains("invalid_client"),
                refusal.getMessage());
    }

    private String tokenEndpoint(int status, String response) {
        server.createContext("/token", exchange -> {
            method = exchange.getRequestMethod();
            contentType = exchange.getRequestHeaders()
                                  .getFirst("Content-Type");
            body = new String(exchange.getRequestBody()
                                      .readAllBytes(),
                    StandardCharsets.UTF_8);
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders()
                    .set("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(bytes);
            }
        });
        return "http://" + server.getAddress()
                                 .getHostString()
                + ":" + server.getAddress()
                              .getPort()
                + "/token";
    }

    private static Map<String, String> form(String encoded) {
        return Arrays.stream(encoded.split("&"))
                     .map(pair -> pair.split("=", 2))
                     .collect(Collectors.toMap(pair -> decode(pair[0]), pair -> decode(pair[1])));
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
