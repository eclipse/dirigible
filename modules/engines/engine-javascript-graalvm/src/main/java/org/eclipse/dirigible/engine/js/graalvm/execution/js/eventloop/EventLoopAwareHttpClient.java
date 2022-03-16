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
package org.eclipse.dirigible.engine.js.graalvm.execution.js.eventloop;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.graalvm.polyglot.Value;

public class EventLoopAwareHttpClient {
    private final CloseableHttpClient httpClient;

    public EventLoopAwareHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void execute(HttpUriRequest httpUriRequest, Value onCompletedCallback, Value onFailedCallback) {
        GraalJSEventLoop looper = GraalJSEventLoop.getCurrent();
        looper.postAsync(() -> httpClient.execute(httpUriRequest), onCompletedCallback, onFailedCallback);
    }

}
