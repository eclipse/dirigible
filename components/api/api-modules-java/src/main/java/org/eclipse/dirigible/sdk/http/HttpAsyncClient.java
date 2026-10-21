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

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.api.http.HttpClientFacade;

/**
 * Asynchronous outbound HTTP client: the calls of {@link HttpClient}, each run on a virtual thread
 * of its own, so the caller is not blocked. Options are the same JSON document, and the future
 * completes with the same JSON response document ({@code statusCode}, {@code text},
 * {@code headers}, ...) - or exceptionally, with the {@link IOException} as its cause.
 * <p>
 * The call resolves configuration (proxy settings among it) as the calling thread would, tenant
 * overrides included. Continuations attached with {@code thenApply} and the like may run on the
 * call's virtual thread, which carries neither the caller's tenant nor its user: do the work that
 * depends on them - database access, security checks - on the calling thread, after
 * {@link CompletableFuture#join()}.
 *
 * <pre>{@code
 * CompletableFuture<String> rates = HttpAsyncClient.get("https://api.example.com/rates");
 * CompletableFuture<String> stock = HttpAsyncClient.get("https://api.example.com/stock");
 * CompletableFuture.allOf(rates, stock)
 *                  .join();
 * }</pre>
 */
public final class HttpAsyncClient {

    /**
     * One virtual thread per call: a call blocks on I/O, and a blocked virtual thread holds no OS
     * thread.
     */
    private static final Executor EXECUTOR = Executors.newThreadPerTaskExecutor(Thread.ofVirtual()
                                                                                      .name("sdk-http-async-", 0)
                                                                                      .factory());

    private HttpAsyncClient() {}

    /**
     * Sends a GET request.
     *
     * @param url the URL
     * @return the response as JSON
     */
    public static CompletableFuture<String> get(String url) {
        return send(() -> HttpClientFacade.get(url, null));
    }

    /**
     * Sends a GET request.
     *
     * @param url the URL
     * @param optionsJson the request options
     * @return the response as JSON
     */
    public static CompletableFuture<String> get(String url, String optionsJson) {
        return send(() -> HttpClientFacade.get(url, optionsJson));
    }

    /**
     * Sends a POST request.
     *
     * @param url the URL
     * @param optionsJson the request options, carrying the body
     * @return the response as JSON
     */
    public static CompletableFuture<String> post(String url, String optionsJson) {
        return send(() -> HttpClientFacade.post(url, optionsJson));
    }

    /**
     * Sends a PUT request.
     *
     * @param url the URL
     * @param optionsJson the request options, carrying the body
     * @return the response as JSON
     */
    public static CompletableFuture<String> put(String url, String optionsJson) {
        return send(() -> HttpClientFacade.put(url, optionsJson));
    }

    /**
     * Sends a PATCH request.
     *
     * @param url the URL
     * @param optionsJson the request options, carrying the body
     * @return the response as JSON
     */
    public static CompletableFuture<String> patch(String url, String optionsJson) {
        return send(() -> HttpClientFacade.patch(url, optionsJson));
    }

    /**
     * Sends a DELETE request.
     *
     * @param url the URL
     * @param optionsJson the request options
     * @return the response as JSON
     */
    public static CompletableFuture<String> delete(String url, String optionsJson) {
        return send(() -> HttpClientFacade.delete(url, optionsJson));
    }

    /**
     * Sends a HEAD request.
     *
     * @param url the URL
     * @param optionsJson the request options
     * @return the response as JSON
     */
    public static CompletableFuture<String> head(String url, String optionsJson) {
        return send(() -> HttpClientFacade.head(url, optionsJson));
    }

    private static CompletableFuture<String> send(Call call) {
        Map<String, String> callerConfiguration = Configuration.getThreadConfiguration();
        return CompletableFuture.supplyAsync(() -> {
            Configuration.setThreadConfiguration(callerConfiguration);
            try {
                return call.execute();
            } catch (IOException e) {
                throw new CompletionException(e);
            } finally {
                Configuration.removeThreadConfiguration();
            }
        }, EXECUTOR);
    }

    /** A synchronous {@link HttpClientFacade} call. */
    @FunctionalInterface
    private interface Call {

        String execute() throws IOException;
    }
}
