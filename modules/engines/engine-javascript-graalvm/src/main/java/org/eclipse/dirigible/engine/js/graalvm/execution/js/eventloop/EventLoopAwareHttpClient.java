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
