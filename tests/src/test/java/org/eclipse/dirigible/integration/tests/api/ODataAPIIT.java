/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.integration.tests.api;

import static org.junit.Assert.assertEquals;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.eclipse.dirigible.integration.tests.IntegrationTest;
import org.eclipse.dirigible.integration.tests.JsonAsserter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

class ODataAPIIT extends IntegrationTest {


    private static final String EXPECTED_METADATA = """
                {
                    "d": {
                        "EntitySets": [
                            "Cars"
                        ]
                    }
                }
            """;

    @LocalServerPort
    private int localServerPort;

    @WithMockUser(username = "developer", roles = {"developer"})
    @Test
    void testODataMetadata() throws Exception {
        String uri = "http://localhost:" + localServerPort + "/odata/v2";
        HttpGet httpGet = new HttpGet(uri);
        httpGet.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString());

        try (CloseableHttpClient httpclient = createClient()) {
            HttpClientResponseHandler<ClassicHttpResponse> handler = response -> {
                assertEquals(HttpStatus.SC_OK, response.getCode());
                assertEquals(MediaType.APPLICATION_JSON.toString(), response.getHeader(HttpHeaders.CONTENT_TYPE)
                                                                            .getValue());
                try (HttpEntity entity = response.getEntity()) {
                    JsonAsserter.assertEquals(EXPECTED_METADATA, EntityUtils.toString(entity));
                }
                return response;
            };
            httpclient.execute(httpGet, handler);
        }
    }

    private CloseableHttpClient createClient() {
        HttpHost targetHost = new HttpHost("http", "localhost", localServerPort);
        BasicCredentialsProvider provider = new BasicCredentialsProvider();
        AuthScope authScope = new AuthScope(targetHost);
        provider.setCredentials(authScope, new UsernamePasswordCredentials("admin", "admin".toCharArray()));

        return HttpClientBuilder.create()
                                .setDefaultCredentialsProvider(provider)
                                .build();
    }
}
