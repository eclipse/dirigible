/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.sdk.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.api.http.client.HttpClientResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * {@link HttpAsyncClient} against a local server: the caller is not blocked, the response and the
 * failure reach the future, and the call resolves configuration as the calling thread does.
 */
class HttpAsyncClientTest {

    private static final long TIMEOUT_SECONDS = 10;

    private HttpServer server;

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.start();
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    @Test
    void theCallerIsNotBlockedAndTheFutureCompletesWithTheResponse() throws Exception {
        CountDownLatch release = new CountDownLatch(1);
        server.createContext("/slow", exchange -> {
            try {
                release.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread()
                      .interrupt();
            }
            answer(exchange, "pong");
        });

        CompletableFuture<String> response = HttpAsyncClient.get(url("/slow"));

        // The server holds its answer until released, so a call that had blocked the caller could not
        // have returned yet.
        assertFalse(response.isDone(), "the future must be handed back before the server answers");
        release.countDown();
        HttpClientResponse completed = GsonHelper.fromJson(response.get(TIMEOUT_SECONDS, TimeUnit.SECONDS), HttpClientResponse.class);
        assertEquals(200, completed.getStatusCode());
        assertEquals("pong", completed.getText());
    }

    @Test
    void aFailedCallCompletesTheFutureExceptionallyWithTheIOException() throws IOException {
        int closedPort;
        try (ServerSocket socket = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
            closedPort = socket.getLocalPort();
        }

        CompletableFuture<String> response = HttpAsyncClient.get("http://127.0.0.1:" + closedPort + "/");

        ExecutionException failure = assertThrows(ExecutionException.class, () -> response.get(TIMEOUT_SECONDS, TimeUnit.SECONDS));
        assertInstanceOf(IOException.class, failure.getCause());
    }

    @Test
    void theCallResolvesConfigurationAsTheCallingThreadDoes() throws Exception {
        // The local server stands in for a proxy that only the caller's thread configuration names - a
        // tenant override, say. The host does not resolve, so the request can only arrive through it.
        server.createContext("/via-proxy", exchange -> answer(exchange, exchange.getRequestHeaders()
                                                                                .getFirst("Host")));
        Configuration.setThreadConfiguration(Map.of("http.proxyHost", server.getAddress()
                                                                            .getHostString(),
                "http.proxyPort", String.valueOf(server.getAddress()
                                                       .getPort())));
        CompletableFuture<String> response;
        try {
            response = HttpAsyncClient.get("http://http-async-client-test.invalid/via-proxy");
        } finally {
            Configuration.removeThreadConfiguration();
        }

        HttpClientResponse completed = GsonHelper.fromJson(response.get(TIMEOUT_SECONDS, TimeUnit.SECONDS), HttpClientResponse.class);
        assertEquals(200, completed.getStatusCode());
        assertEquals("http-async-client-test.invalid", completed.getText());
    }

    private String url(String path) {
        return "http://" + server.getAddress()
                                 .getHostString()
                + ":" + server.getAddress()
                              .getPort()
                + path;
    }

    private static void answer(HttpExchange exchange, String text) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders()
                .set("Content-Type", "text/plain");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }
}
